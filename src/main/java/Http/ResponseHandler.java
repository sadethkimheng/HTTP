package Http;

import java.io.*;
import java.net.Socket;
import java.util.Properties;


public class ResponseHandler {

    static final String FILE_NOT_FOUND = "src/404.html";
    static final File WEB_ROOT = new File(".");


    BufferedReader in = null;
    PrintWriter out = null;
    BufferedOutputStream dataOut = null;
    private final static String producerPropsFile = "output.properties";

    Properties properties = new Properties();

    InputStream inputStream = ResourceLoader.class.getClassLoader().getResourceAsStream(producerPropsFile);


    public void response(Socket sock, String path) {


        try {

            // we read characters from the client via input stream on the socket
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            // we get character output stream to client (for headers)
            out = new PrintWriter(sock.getOutputStream());
            // get binary output stream to client (for requested data)
            dataOut = new BufferedOutputStream(sock.getOutputStream());


            properties.load(inputStream);
            String Root = properties.getProperty("RootServer");
//            System.out.println("Before Try Root"+Root);



            File file = new File(Root+"/"+path);
//            System.out.println("Before try Path"+path);
//            System.out.println("Before Try File"+file);

            int fileLength = (int) file.length();

                if (file.isDirectory()) {
                    String content = getListOfFilesAndFolders(path);
//                    System.out.println("Content ++" + content);
                    // send HTTP Headers
                    out.println("HTTP/1.1 200 OK");
                    out.println("Server: Java HTTP Server from SSaurel : 1.0");
                    out.println("Content-Type: " + "text/html");
                    out.println("Content-Length: " + content.length());
                    out.println(); // blank line between headers and content, very important !
                    out.println(content);
                    out.flush(); // flush character output stream buffer

                    dataOut.flush();
                }
                else
                {
                    byte[] fileData = readFileData(file, fileLength);
                    String contentType = guessContentType(path);
//                    System.out.println("Before Try ContentType"+contentType);

//                    System.out.println("List all file" + fileData);
//                    System.out.println(fileLength);
                    // send HTTP Headers
                    out.println("HTTP/1.1 200 OK");
                    out.println("Server: Java HTTP Server from SSaurel : 1.0");
                    out.println("Content-Disposition: " + "attachment; filename=" +file.getName() );
                    out.println("Content-Type: " + contentType);
                    out.println("Content-Length: " + fileLength);
                    out.println(); // blank line between headers and content, very important !
                    out.flush(); // flush character output stream buffer

                    dataOut.write(fileData, 0, fileLength);
                    dataOut.flush();
                }



        }
        catch (FileNotFoundException fnfe) {
            try {
                fileNotFound(out, dataOut);
            } catch (IOException ioe) {
                System.err.println("Error with file not found exception : " + ioe.getMessage());
            }

        }
        catch (IOException ioe) {
            System.err.println("Server error : " + ioe);
        } finally {
            try {
                in.close();
                out.close();
                dataOut.close();
//                sock.close(); // we close socket connection
            } catch (Exception e) {
                System.err.println("Error closing stream : " + e.getMessage());
            }
        }
    }


            private static String guessContentType (String path){
                if (path.endsWith(".html") || path.endsWith(".htm"))
                    return "text/html";
                else if (path.endsWith(".txt"))
                    return "text/plain";
                else if (path.endsWith(".gif"))
                    return "image/gif";
                else if (path.endsWith(".class"))
                    return "application/octet-stream";
                else if (path.endsWith(".jpg") || path.endsWith(".jpeg"))
                    return "image/jpeg";
                else if (path.endsWith(".pptx"))
                    return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
                else if (path.endsWith(".png/"))
                    return "image/png";
                else if (path.endsWith(".docx"))
                    return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
                else
                    return "text/html";
            }

            private String getListOfFilesAndFolders (String path){
                String content = "";
                String Root = properties.getProperty("RootServer");
                System.out.println("RootRoot"+Root);

                try {
                    for (File file : new File(Root+"/"+path).listFiles()) {

                        System.out.println("Show all file " + file);
                        if (file.isDirectory()) {
                            content += "<a href='" + path + file.getName() + "/'>" + file.getName() + "</a>";
                        } else if (file.isFile()) {
                            content += "<a href='" + path + file.getName() + "'>" + file.getName() + "</a>";
                        }
                        content += "<br/><br/>";
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return content;
            }

    private byte[] readFileData(File file, int fileLength) throws IOException {
        FileInputStream fileIn = null;
        byte[] fileData = new byte[fileLength];


        try {
            fileIn = new FileInputStream(file);
            fileIn.read(fileData);

        } finally {
            if (fileIn != null)
                fileIn.close();
        }

        return fileData;
    }


    private void fileNotFound(PrintWriter out, OutputStream dataOut) throws IOException {


        File file = new File(WEB_ROOT, FILE_NOT_FOUND);

        int fileLength = (int) file.length();
        String content = "text/html";
        byte[] fileData = readFileData(file, fileLength);

        out.println("HTTP/1.1 404 File Not Found");
        out.println("Server: Java HTTP Server from SSaurel : 1.0");
        out.println("Content-type: " + content);
        out.println("Content-length: " + fileLength);
        out.println(); // blank line between headers and content, very important !
        out.flush(); // flush character output stream buffer


        dataOut.write(fileData, 0, fileLength);
        dataOut.flush();


    }
}


