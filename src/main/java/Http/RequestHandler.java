package Http;



import java.io.*;
import java.net.Socket;
import java.util.*;

public class RequestHandler {
    public static final String

            HTTP_INTERNALERROR = "500 Internal Server Error";
    public static Socket socket;
    private final static String producerPropsFile = "output.properties";
    InputStream inputStream = ResourceLoader.class.getClassLoader().getResourceAsStream(producerPropsFile);


    public void handleRequest(Socket sock) {
        this.socket = sock;

        try {
            InputStream is = sock.getInputStream();
            if (is == null) return;


            int bufsize = 8192;
            byte[] buf = new byte[bufsize];
            int rlen = is.read(buf, 0, bufsize);
            if (rlen <= 0) return;

            // Create a BufferedReader for parsing the header.
            ByteArrayInputStream hbis = new ByteArrayInputStream(buf, 0, rlen);
            BufferedReader hin = new BufferedReader(new InputStreamReader(hbis));
            Properties pre = new Properties();
            Properties parms = new Properties();
            Properties header = new Properties();
            Properties files = new Properties();
            Properties properties = new Properties();
            properties.load(inputStream);
            String Root = properties.getProperty("RootServer");



            // Decode the header into parms and header java properties
            RequestMethod.decodeHeader(hin, pre, parms, header);
            String method = pre.getProperty("method");
            System.out.println("Method: "+method);
            String uri = pre.getProperty("uri");
            System.out.println("URL: "+uri);

            long size = 0x7FFFFFFFFFFFFFFFl;
            String contentLength = header.getProperty("content-length");
            if (contentLength != null) {
                try {
                    size = Integer.parseInt(contentLength);
                } catch (NumberFormatException ex) {
                }
            }

            // We are looking for the byte separating header from body.
            // It must be the last byte of the first two sequential new lines.
            int splitbyte = 0;
            boolean sbfound = false;
            while (splitbyte < rlen) {
                if (buf[splitbyte] == '\r' && buf[++splitbyte] == '\n' && buf[++splitbyte] == '\r' && buf[++splitbyte] == '\n') {
                    sbfound = true;
                    break;
                }
                splitbyte++;
            }
            splitbyte++;

            // Write the part of body already read to ByteArrayOutputStream f
            ByteArrayOutputStream f = new ByteArrayOutputStream();
            if (splitbyte < rlen) f.write(buf, splitbyte, rlen - splitbyte);

            // While Firefox sends on the first read all the data fitting
            // our buffer, Chrome and Opera sends only the headers even if
            // there is data for the body.


            if (splitbyte < rlen)
                size -= rlen - splitbyte + 1;
            else if (!sbfound || size == 0x7FFFFFFFFFFFFFFFl)
                size = 0;

            // Now read all the body and write it to f
            buf = new byte[512];
            while (rlen >= 0 && size > 0) {
                rlen = is.read(buf, 0, 512);
                size -= rlen;
                if (rlen > 0)
                    f.write(buf, 0, rlen);
            }

            // Get the raw body as a byte []
            byte[] fbuf = f.toByteArray();

            // Create a BufferedReader for easily reading it as string.
            ByteArrayInputStream bin = new ByteArrayInputStream(fbuf);
            BufferedReader in = new BufferedReader(new InputStreamReader(bin));

            // If the method is POST, there may be parameters

            if (method.equalsIgnoreCase("POST")) {
                ResourceLoader.fileExisted1(uri);

                String contentType = "";
                String contentTypeHeader = header.getProperty("content-type");
                StringTokenizer st = new StringTokenizer(contentTypeHeader, "; ");
                if (st.hasMoreTokens()) {
                    contentType = st.nextToken();
                }

                if (contentType.equalsIgnoreCase("multipart/form-data")) {
                    // Handle multipart/form-data
                    String boundaryExp = st.nextToken();
                    st = new StringTokenizer(boundaryExp, "=");
                    st.nextToken();
                    String boundary = st.nextToken();

                    RequestMethod.decodeMultipartData(boundary, fbuf, in, parms, files,pre,Root);
                }

            }
            else if (method.equalsIgnoreCase("GET")) {
                ResponseHandler responseMethod = new ResponseHandler();
                responseMethod.response(sock, uri);
            }
            else if (method.equalsIgnoreCase("PUT")) {
                boolean te = ResourceLoader.fileExisted1(uri);

                if (te) {
                    {
                        String contentType = "";
                        String contentTypeHeader = header.getProperty("content-type");
                        StringTokenizer st = new StringTokenizer(contentTypeHeader, "; ");
                        if (st.hasMoreTokens()) {
                            contentType = st.nextToken();
                        }

                        if (contentType.equalsIgnoreCase("multipart/form-data")) {
                            // Handle multipart/form-data
                            String boundaryExp = st.nextToken();
                            st = new StringTokenizer(boundaryExp, "=");
                            st.nextToken();
                            String boundary = st.nextToken();

                            RequestMethod.decodeMultipartData(boundary, fbuf, in, parms, files, pre, Root);
                        }

                    }

                }
            }

            else if (method.matches("DELETE")) {
                String delete = Root+ uri;
                System.out.println("Delete"+delete);

                File file = new File(delete);

                if(file.delete())
                {
                    System.out.println("File deleted successfully");
                }
                else
                {
                    System.out.println("Failed to delete the file");
                }
            }

            in.close();
            is.close();
        } catch (IOException ioe) {
            try {
                RequestMethod.sendError(HTTP_INTERNALERROR, "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage());
            } catch (Throwable t) {
            }
        } catch (InterruptedException ie) {
            // Thrown by sendError, ignore and exit the thread.
        }
    }


}
