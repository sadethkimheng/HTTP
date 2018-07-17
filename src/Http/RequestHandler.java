package Http;

import java.io.*;
import java.net.Socket;

public class RequestHandler {

	ResourceLoader resourceLoader = new ResourceLoader();
	private static final int BUFFER_SIZE = 4096;


	public void handleRequest(Socket sock) {
		OutputStream out = null;
		int value;


		try {
			out = sock.getOutputStream();
			String request = HttpUtils.getRequest(sock);
			System.out.println(request);

			String uri = HttpUtils.getRequestUri(request);
			System.out.println("Received request for - " + uri);
			boolean referer = HttpUtils.checkReferer(request);

//			 uri.matches("index.html");
				if(uri.matches("/index.html") || referer) {
					InputStream in = resourceLoader.getResource(uri);

					System.out.println("Sending response ");

					PrintWriter pw = new PrintWriter(out);
					pw.println("HTTP/1.0 200 OK");
					pw.println("<h1> Hello world </h1>");
					pw.println();
					pw.flush();
					while ((value = in.read()) != -1) {
						out.write(value);
					}
//			if (uri.matches("/test/a.txt"))
//
//			{
//				String link = "http://localhost:8000/test/a.txt";
//				File out1 = new File("/Users/kimheng/Downloads/a.txt");
//				new Thread(new DownloadFile(link,out1)).start();
//
//			}
					in.close();
					out.close();

				}


		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				sock.close();
			} catch (Exception e) {
			}
		}
	}
}
