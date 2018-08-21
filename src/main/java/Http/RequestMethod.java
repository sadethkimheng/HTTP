package Http;


import java.io.*;
import java.net.Socket;
import java.util.*;

public class RequestMethod {

    private final static String producerPropsFile = "output.properties";
    public static final String
            HTTP_BADREQUEST = "400 Bad Request",
            HTTP_INTERNALERROR = "500 Internal Server Error",
            MIME_PLAINTEXT = "text/plain";


    public static Socket socket;

    /**
     * Decodes the sent headers and loads the data into
     * java Properties' key - value pairs
     **/
    static void decodeHeader(BufferedReader in, Properties pre, Properties parms, Properties header)
            throws InterruptedException
    {
        try {
            // Read the request line
            String inLine = in.readLine();
            if (inLine == null) return;
            StringTokenizer st = new StringTokenizer( inLine );
            if ( !st.hasMoreTokens())
                sendError( HTTP_BADREQUEST, "BAD REQUEST: Syntax error. Usage: GET /example/file.html" );

            String method = st.nextToken();
            pre.put("method", method);

            if ( !st.hasMoreTokens())
                sendError( HTTP_BADREQUEST, "BAD REQUEST: Missing URI. Usage: GET /example/file.html" );

            String uri = st.nextToken();

            // Decode parameters from the URI
            int qmi = uri.indexOf( '?' );
            if ( qmi >= 0 )
            {
                decodeParms( uri.substring( qmi+1 ), parms );
                uri = decodePercent( uri.substring( 0, qmi ));
            }
            else uri = decodePercent(uri);

            // If there's another token, it's protocol version,
            // followed by HTTP headers. Ignore version but parse headers.
            // NOTE: this now forces header names lowercase since they are
            // case insensitive and vary by client.

            if ( st.hasMoreTokens())
            {
                String line = in.readLine();
                while ( line != null && line.trim().length() > 0 )
                {
                    int p = line.indexOf( ':' );
                    if ( p >= 0 )
                        header.put( line.substring(0,p).trim().toLowerCase(), line.substring(p+1).trim());
                    line = in.readLine();
                }
            }

            pre.put("uri", uri);
        }
        catch ( IOException ioe )
        {
            sendError( HTTP_INTERNALERROR, "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage());
        }
    }

     // convert Ex : " an+example%20string" -> "an example string"

    private static String decodePercent(String str) throws InterruptedException
    {
        try
        {
            StringBuffer sb = new StringBuffer();
            for( int i=0; i<str.length(); i++ )
            {
                char c = str.charAt( i );
                switch ( c )
                {
                    case '+':
                        sb.append( ' ' );
                        break;
                    case '%':
                        sb.append((char)Integer.parseInt( str.substring(i+1,i+3), 16 ));
                        i += 2;
                        break;
                    default:
                        sb.append( c );
                        break;
                }
            }
            return sb.toString();
        }
        catch( Exception e )
        {
            sendError( HTTP_BADREQUEST, "BAD REQUEST: Bad percent-encoding." );
            return null;
        }
    }

     // Decodes parameters in percent-encoded URI-format
     // ( e.g. "name=Jack%20Daniels&pass=Single%20Malt" )
    public static void decodeParms(String parms, Properties p)
            throws InterruptedException
    {
        if ( parms == null )
            return;

        StringTokenizer st = new StringTokenizer( parms, "&" );
        while ( st.hasMoreTokens())
        {
            String e = st.nextToken();
            int sep = e.indexOf( '=' );
            if ( sep >= 0 )
                p.put( decodePercent( e.substring( 0, sep )).trim(),
                        decodePercent( e.substring( sep+1 )));
        }
    }

    public static void decodeMultipartData(String boundary, byte[] fbuf, BufferedReader in, Properties parms, Properties files, Properties pre,String Root)
            throws InterruptedException
    {
        try
        {
            int[] bpositions = getBoundaryPositions(fbuf,boundary.getBytes());
            int boundarycount = 1;
            String mpline = in.readLine();
            while ( mpline != null )
            {
                if (mpline.indexOf(boundary) == -1)
                    sendError( HTTP_BADREQUEST, "BAD REQUEST: Content type is multipart/form-data but next chunk does not start with boundary. Usage: GET /example/file.html" );
                boundarycount++;
                Properties item = new Properties();
                mpline = in.readLine();
                while (mpline != null && mpline.trim().length() > 0)
                {
                    int p = mpline.indexOf( ':' );
                    if (p != -1)
                        item.put( mpline.substring(0,p).trim().toLowerCase(), mpline.substring(p+1).trim());
                    mpline = in.readLine();
                }
                if (mpline != null)
                {
                    String contentDisposition = item.getProperty("content-disposition");
                    if (contentDisposition == null)
                    {
                        sendError( HTTP_BADREQUEST, "BAD REQUEST: Content type is multipart/form-data but no content-disposition info found. Usage: GET /example/file.html" );
                    }
                    StringTokenizer st = new StringTokenizer( contentDisposition , ";" );
                    Properties disposition = new Properties();
                    while ( st.hasMoreTokens())
                    {
                        String token = st.nextToken().trim();
                        System.out.println();
                        int p = token.indexOf( '=' );
                        if (p!=-1)
                            disposition.put( token.substring(0,p).trim().toLowerCase(), token.substring(p+1).trim());
                    }
                    String pname = disposition.getProperty("filename");
                    pname = pname.substring(1,pname.length()-1);

                    System.out.println("pname"+pname);
                    String uri = pre.getProperty("uri");



                    String value = "";
                        int offset = stripMultipartHeaders(fbuf, bpositions[boundarycount-2]);
                        String path = saveFile(fbuf, offset, bpositions[boundarycount-1]-offset-4,uri,pname,Root);
                        files.put(pname, path);
                        value = disposition.getProperty("filename");
                        value = value.substring(1,value.length()-1);
                        do {
                            mpline = in.readLine();
                        } while (mpline != null && mpline.indexOf(boundary) == -1);

                    parms.put(pname, value);
                }
            }
        }
        catch ( IOException ioe )
        {
            sendError( HTTP_INTERNALERROR, "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage());
        }
    }



     // It returns the offset separating multipart file headers
     // from the file's data.

    private static int stripMultipartHeaders(byte[] b, int offset)
    {
        int i = 0;
        for (i=offset; i<b.length; i++)
        {
            if (b[i] == '\r' && b[++i] == '\n' && b[++i] == '\r' && b[++i] == '\n')
                break;
        }
        return i+1;
    }



     // Retrieves the content of a sent file and saves it
     // to a temporary file.
     // The full path to the saved file is returned.

     static String saveFile(byte[] b, int offset, int len,String uri,String pname,String Root)  {

        String path = "";
        if (len > 0)
        {
;
            File file = new File(Root +uri+"/"+pname);
            try {
                OutputStream fstream = new FileOutputStream(file);
                fstream.write(b, offset, len);
                fstream.close();
                path = file.getAbsolutePath();
            } catch (Exception e) { // Catch exception if any
                myErr.println("Error: " + e.getMessage());
            }
        }
        return path;
    }


    /**
     * Find the byte positions where multipart boundaries start.
     **/
    public static int[] getBoundaryPositions(byte[] b, byte[] boundary)
    {
        int matchcount = 0;
        int matchbyte = -1;
        Vector<Integer> matchbytes = new Vector<Integer>();
        for (int i=0; i<b.length; i++)
        {
            if (b[i] == boundary[matchcount])
            {
                if (matchcount == 0)
                    matchbyte = i;
                matchcount++;
                if (matchcount==boundary.length)
                {
                    matchbytes.addElement(new Integer(matchbyte));
                    matchcount = 0;
                    matchbyte = -1;
                }
            }
            else
            {
                i -= matchcount;
                matchcount = 0;
                matchbyte = -1;
            }
        }
        int[] ret = new int[matchbytes.size()];
        for (int i=0; i < ret.length; i++)
        {
            ret[i] = ((Integer)matchbytes.elementAt(i)).intValue();
        }
        return ret;
    }


    public static void sendError(String status, String msg) throws InterruptedException
    {
        sendResponse( status, MIME_PLAINTEXT, null, new ByteArrayInputStream( msg.getBytes()));
        throw new InterruptedException();
    }

    /**
     * Sends given response to the socket.
     */
    private static void sendResponse(String status, String mime, Properties header, InputStream data)
    {
        try
        {
            if ( status == null )
                throw new Error( "sendResponse(): Status can't be null." );

            OutputStream out = socket.getOutputStream();
            PrintWriter pw = new PrintWriter( out );
            pw.print("HTTP/1.0 " + status + " \r\n");

            if ( mime != null )
                pw.print("Content-Type: " + mime + "\r\n");

            if ( header == null || header.getProperty( "Date" ) == null )
                pw.print( "Date: " + gmtFrmt.format( new Date()) + "\r\n");

            if ( header != null )
            {
                Enumeration e = header.keys();
                while ( e.hasMoreElements())
                {
                    String key = (String)e.nextElement();
                    String value = header.getProperty( key );
                    pw.print( key + ": " + value + "\r\n");
                }
            }

            pw.print("\r\n");
            pw.flush();

            if ( data != null )
            {
                int pending = data.available();	// This is to support partial sends, see serveFile()
                byte[] buff = new byte[theBufferSize];
                //while (pending>0)
                while (true)
                {
                    pending = data.available();
                    if (pending == 0) pending = 1;
                    int read = data.read( buff, 0, ( (pending>theBufferSize) ?  theBufferSize : pending ));
                    System.out.println(Integer.toString(read));
                    //if (read <= 0)	break;
                    if (read == -1) break;
                    out.write( buff, 0, read );
                    pending -= read;
                }
            }
            out.flush();
            out.close();
            if ( data != null )
                data.close();
        }
        catch( IOException ioe )
        {
            // Couldn't write? No can do.
            try { socket.close(); } catch( Throwable t ) {}
        }

    }





    private static int theBufferSize = 16 * 1024;
    private static java.text.SimpleDateFormat gmtFrmt;
    protected static PrintStream myOut = System.out;
    protected static PrintStream myErr = System.err;
    static
    {
        gmtFrmt = new java.text.SimpleDateFormat( "E, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        gmtFrmt.setTimeZone(TimeZone.getTimeZone("GMT"));
    }



}


//    public static void decode1(String sourceFile, String targetFile) throws Exception {
//
//        byte[] decodedBytes = Base64.decodeBase64(loadFileAsBytesArray(sourceFile));
//
//        writeByteArraysToFile(targetFile, decodedBytes);
//    }
//
//    public static byte[] loadFileAsBytesArray(String fileName) throws Exception {
//
//        File file = new File(fileName);
//        int length = (int) file.length();
//        BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file));
//        byte[] bytes = new byte[length];
//        reader.read(bytes, 0, length);
//        reader.close();
//        return bytes;
//
//    }
//    public static void writeByteArraysToFile(String fileName, byte[] content) throws IOException {
//
//        File file = new File(fileName);
//        BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(file));
//        writer.write(content);
//        writer.flush();
//        writer.close();
//
//    }





//fileName ,extension, path, content
