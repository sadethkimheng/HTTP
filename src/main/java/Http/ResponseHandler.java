package Http;

import java.io.*;
import java.net.Socket;
import java.util.Properties;


public class ResponseHandler {
    static final String PROJECT_DIR = System.getProperty("user.dir");


    BufferedReader in = null;
    PrintWriter out = null;
    BufferedOutputStream dataOut = null;

    // verbose mode
    static final boolean verbose = true;

    private final static String producerPropsFile = "output.properties";

    Properties properties = new Properties();

    InputStream inputStream = ResourceLoader.class.getClassLoader().getResourceAsStream(producerPropsFile);


    public void response(Socket sock, String path) throws IOException {


        try {

            // we read characters from the client via input stream on the socket
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            // we get character output stream to client (for headers)
            out = new PrintWriter(sock.getOutputStream());
            // get binary output stream to client (for requested data)
            dataOut = new BufferedOutputStream(sock.getOutputStream());
            properties.load(inputStream);
            String Root = properties.getProperty("RootServer");
            System.out.println("Root"+Root);
            File file = new File(Root+"/"+path);
            System.out.println("Path"+path);
            System.out.println("File"+file);
            int fileLength = (int) file.length();
            String contentType = guessContentType(path);
            System.out.println("ContentType"+contentType);
                if (file.isDirectory()) {
                    String content = getListOfFilesAndFolders(path);
                    System.out.println("Content ++" + content);
                    // send HTTP Headers
                    out.println("HTTP/1.1 200 OK");
                    out.println("Server: Java HTTP Server from SSaurel : 1.0");
                    out.println("Content-type: " + "text/html");
                    out.println("Content-length: " + content.length());
                    out.println(); // blank line between headers and content, very important !
                    out.println(content);
                    out.flush(); // flush character output stream buffer

                    dataOut.flush();
                }
                else if(file.isFile()) {
                    byte[] fileData = readFileData(file, fileLength);

                    System.out.println("List all file" + fileData);
                    System.out.println(fileLength);
                    // send HTTP Headers
                    out.println("HTTP/1.1 200 OK");
                    out.println("Server: Java HTTP Server from SSaurel : 1.0");
                    out.println("Content-type: " + contentType);
                    out.println("Content-length: " + fileLength);
                    out.println(); // blank line between headers and content, very important !
                    out.flush(); // flush character output stream buffer

                    dataOut.write(fileData, 0, fileLength);
                    dataOut.flush();
                }

            if (verbose) {
                System.out.println("File " + path + " of type " + contentType + " returned");
            }

        }
        catch (IOException ioe) {
            System.err.println("Server error : " + ioe);
        } finally {
            try {
                in.close();
                out.close();
                dataOut.close();
                sock.close(); // we close socket connection
            } catch (Exception e) {
                System.err.println("Error closing stream : " + e.getMessage());
            }

            if (verbose) {
                System.out.println("Connection closed.\n");
            }
        }
    }

            private static String guessContentType (String path){
                if (path.endsWith(".html/") || path.endsWith(".htm/"))
                    return "text/html";
                else if (path.endsWith(".txt/") || path.endsWith(".txt"))
                    return "text/plain";
                else if (path.endsWith(".gif/"))
                    return "image/gif";
                else if (path.endsWith(".class/"))
                    return "application/octet-stream";
                else if (path.endsWith(".jpg/") || path.endsWith(".jpeg/"))
                    return "image/jpeg";
                else if (path.endsWith("/"))
                    return "text/html";
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




}


