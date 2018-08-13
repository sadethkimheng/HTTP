package Http;

import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.List;
import java.util.Properties;

public class RequestHandler {
    private final static String producerPropsFile = "output.properties";
    InputStream inputStream = ResourceLoader.class.getClassLoader().getResourceAsStream(producerPropsFile);


    Properties properties = new Properties();

	public void handleRequest(Socket sock) {


		try {

			String request = RequestMethod.getRequest(sock);
			System.out.println(request);
			String method = RequestMethod.getMethod(request);


			if (method.matches("GET")) {
				String path = RequestMethod.getRequestUri(request);
				String decode = URLDecoder.decode(path,"UTF-8");
                System.out.println("++++++++++++++"+decode);
					ResponseHandler responseMethod = new ResponseHandler();
					responseMethod.response(sock, decode);

//

			}
            else if (method.matches("PUT")) {
                String path = RequestMethod.getRequestUri(request);

                properties.load(inputStream);
                String Root = properties.getProperty("RootServer");

                    boolean te = ResourceLoader.fileExisted1(path);
                    System.out.println(te);

                    if (te) {
                        List<UploadedFile> uploadFileList = RequestMethod.getUploadedFileInfo(request);


                        for (int i = 0; i < uploadFileList.size(); i++) {
                            File file = new File(Root+ path + "/" + uploadFileList.get(i).getFullFileName());

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
            else if (method.matches("POST"))
            {
                String path = RequestMethod.getRequestUri(request);


                {
                    ResourceLoader.fileExisted1(path);
                    List<UploadedFile> uploadFileList = RequestMethod.getUploadedFileInfo(request);


                    properties.load(inputStream);
                    String Root = properties.getProperty("RootServer");
                    for (int i = 0; i < uploadFileList.size(); i++) {
                        File file = new File(Root + path + "/" + uploadFileList.get(i).getFullFileName());

                        FileOutputStream fop = null;
                        String content = uploadFileList.get(i).getContent();

                        try {

                            fop = new FileOutputStream(file, true);
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
			else if (method.matches("DELETE")) {
                String path = RequestMethod.getRequestUri(request);
                properties.load(inputStream);
                String Root = properties.getProperty("RootServer");
                String delete = Root+ path;
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




		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
