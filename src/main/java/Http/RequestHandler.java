package Http;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Properties;

public class RequestHandler {
    static final String PROJECT_DIR = System.getProperty("user.dir");
    private final static String producerPropsFile = "output.properties";
    InputStream inputStream = ResourceLoader.class.getClassLoader().getResourceAsStream(producerPropsFile);


    Properties properties = new Properties();

    public void handleRequest(Socket sock) {
        OutputStream out = null;


        try {

            String request = RequestMethod.getRequest(sock);
            System.out.println(request);
            String method = RequestMethod.getMethod(request);


            if (method.matches("GET")) {
                String path = RequestMethod.getRequestUri(request);
                if (path.endsWith("/")) {
                    properties.load(inputStream);
                    String DEFAULT_PATH = properties.getProperty("DEFAULT_PATH");
                    path += DEFAULT_PATH;
                }
                if (path.startsWith("/folder1/") )
                {

                    ResponseHandler responeMethod = new ResponseHandler();
                    responeMethod.response(sock, path);
                }
                else
                {
                    System.out.println("Fail");


                }

            }
            else if (method.matches("PUT")) {

                String path = RequestMethod.getRequestUri(request);



                if (path.startsWith("/folder1/")) {

                    boolean te = ResourceLoader.fileExisted1(path);
                    System.out.println(te);

                    if (te) {
                        List<UploadedFile> uploadFileList = RequestMethod.getUploadedFileInfo(request);
                        properties.load(inputStream);
                        String TOPIC = properties.getProperty("TOPIC");

                        for (int i = 0; i < uploadFileList.size(); i++) {
                            String FILEPATH = PROJECT_DIR +TOPIC;
                            System.out.println("______________"+FILEPATH);
                            File file = new File(FILEPATH + path + "/" + uploadFileList.get(i).getFullFileName());

                            FileOutputStream fop = null;
                            String content = uploadFileList.get(i).getContent();

                            try {

                                fop = new FileOutputStream(file);
                                // get the content in bytes
                                byte[] contentInBytes = content.getBytes();

                                fop.write(contentInBytes);
                                fop.flush();
                                fop.close();

                                System.out.println("Done");

                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                try {
                                    if (fop != null) {
                                        fop.close();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
            else if (method.matches("POST"))

            {
                String path = RequestMethod.getRequestUri(request);

                if(path.startsWith("/folder1/"))
                {
                    {
                        ResourceLoader.fileExisted1(path);
                        List<UploadedFile> uploadFileList = RequestMethod.getUploadedFileInfo(request);


                        properties.load(inputStream);
                        String TOPIC = properties.getProperty("TOPIC");
                        for (int i = 0; i < uploadFileList.size(); i++) {
                            String FILEPATH = PROJECT_DIR + TOPIC;
                            File file = new File(FILEPATH + path + "/" + uploadFileList.get(i).getFullFileName());

                            FileOutputStream fop = null;
                            String content = uploadFileList.get(i).getContent();

                            try {

                                fop = new FileOutputStream(file, true);
//
                                // get the content in bytes
                                byte[] contentInBytes = content.getBytes();

                                fop.write(contentInBytes);
                                fop.flush();
                                fop.close();

                                System.out.println("Done");

                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                try {
                                    if (fop != null) {
                                        fop.close();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }


                        }
                    }
                }
            }
            else if (method.matches("DELETE")) {
                String path = RequestMethod.getRequestUri(request);
                properties.load(inputStream);
                String TOPIC = properties.getProperty("TOPIC");
                String delete = PROJECT_DIR +TOPIC+ path;
                System.out.println(delete);

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
            else if (method.matches("Head")){


            }



        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
