package Http;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class RequestHandler {

	ResourceLoader resourceLoader = new ResourceLoader();
	
	public void handleRequest(Socket sock) {
		OutputStream out = null;
		int value;


		try {
			out = sock.getOutputStream();
			String request = HttpUtils.getRequest(sock);

			String uri = HttpUtils.getRequestUri(request);
			System.out.println("Received request for - " + uri);

			InputStream in = resourceLoader.getResource(uri);

			System.out.println("Sending response ");

			PrintWriter pw = new PrintWriter(out);
			pw.println("HTTP/1.0 200 OK");
			pw.println();
			pw.flush();
			while ((value = in.read()) != -1) {
				out.write(value);
			}
			out.close();

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
