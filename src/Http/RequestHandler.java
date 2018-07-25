package Http;

import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;

public class RequestHandler {

	private static final int BUFFER_SIZE = 4096;


	public void handleRequest(Socket sock) {
		OutputStream out = null;
		int value;


		try {

			String request = RequestMethod.getRequest(sock);
			System.out.println(request);
			String method = RequestMethod.getMethod(request);
//			StringTokenizer st = new StringTokenizer(request);
//			while (st.hasMoreTokens()) {
//				System.out.println(st.nextToken());
//			}
			if (method.matches("GET")) {
				String uri = RequestMethod.getRequestUri(request);
//				System.out.println("Received request for - " + uri);
//				System.out.println("GET");

				ResponeMethod responeMethod = new ResponeMethod();
				responeMethod.methodrespone(sock, uri);
			} else if (method.matches("POST")) {
				String path = RequestMethod.getRequestUri(request);
				boolean tes = RequestMethod.fileExisted(request);
				ResourceLoader.fileExisted1(path);

			}

			else if (method.matches("PUT"))
			{
				System.out.println("!!!!!!!!asdfasdfasdfadsf!!!!!!!!!");

				String uri = RequestMethod.getRequestUri(request);
			}
			else if (method.matches("DELETE"))
			{
//				System.out.println("Delete");
			}




		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
