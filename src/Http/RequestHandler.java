package Http;

import java.io.*;
import java.net.Socket;
import java.util.List;
public class RequestHandler {

	private static final String FILEBASE = "/resources";
	private static final String PROJECTROOT = "/Users/kimheng/Downloads/Source/HttpServer/src";
	static final String PROJECT_DIR = System.getProperty("user.dir");


	public void handleRequest(Socket sock) {
		OutputStream out = null;


		try {

			String request = RequestMethod.getRequest(sock);
			System.out.println(request);
			String method = RequestMethod.getMethod(request);


			if (method.matches("GET")) {
				boolean referer = RequestMethod.checkReferer1(request);
				System.out.println(referer);

				String path = RequestMethod.getRequestUri(request);

				if(path.matches("/index.html") || referer)
					{

						ResponeMethod responeMethod = new ResponeMethod();
						responeMethod.methodrespone(sock, path);
					}
				else
					{
						System.out.println("Bye");


					}

			} else if (method.matches("PUT")) {
				String path = RequestMethod.getRequestUri(request);
				boolean te = ResourceLoader.fileExisted1(path);
				System.out.println(te);

				if (te) {
					List<UploadedFile> uploadFileList = RequestMethod.getUploadedFileInfo(request);

					for (int i = 0; i < uploadFileList.size(); i++) {
						String FILEPATH = PROJECT_DIR +"/src"+FILEBASE;
						File file = new File(FILEPATH  + path+ "/"+uploadFileList.get(i).getFullFileName());

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
				{
					String path = RequestMethod.getRequestUri(request);
				ResourceLoader.fileExisted1(path);
					List<UploadedFile> uploadFileList = RequestMethod.getUploadedFileInfo(request);

					for (int i = 0; i < uploadFileList.size(); i++) {
						String FILEPATH = PROJECT_DIR +"/src" + FILEBASE;
						System.out.println(FILEPATH);
						File file = new File(FILEPATH  + path+ "/"+uploadFileList.get(i).getFullFileName());
						FileOutputStream fop = null;
						String content = uploadFileList.get(i).getContent();
						System.out.println("Content_________-" + content);

						try {

							fop = new FileOutputStream(file,true);
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
				String uri = RequestMethod.getRequestUri(request);
			}
			else if (method.matches("DELETE"))
			{
				String path = RequestMethod.getRequestUri(request);
				System.out.println(path);
				String delete = PROJECTROOT + FILEBASE+path;
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
