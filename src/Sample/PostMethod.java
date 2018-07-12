package Sample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
public class PostMethod {
    public static void main(String[] args) {

                PostMethod n = new PostMethod();
                n.start();

    }

    protected void start() {
         final int PORT = 8080;
        ServerSocket server = null;
        try {
            server = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(true)
        try {

            Socket ne = server.accept();
            System.out.println("Connection, sending data.");
            InputStream is = ne.getInputStream();
            PrintWriter out = new PrintWriter(ne.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(ne.getInputStream()));


            String line;
            line = in.readLine();
            String request_method = line;
            System.out.println("HTTP-HEADER: " + line);
            line = "";
            // looks for post data
            int postDataI = -1;
            while ((line = in.readLine()) != null && (line.length() != 0)) {
                System.out.println("HTTP-HEADER: " + line);
                if (line.indexOf("Content-Length:") > -1) {
                    postDataI = new Integer(line.substring(line.indexOf("Content-Length:") + 16,line.length())).intValue();
                }

            }
            String postData = "";
            // read the post data
            if (postDataI > 0) {
                char[] charArray = new char[postDataI];
                in.read(charArray, 0, postDataI);
                postData = new String(charArray);
            }

            out.println("HTTP/1.0 200 OK");
            out.println("Content-Type: text/html; charset=utf-8");
            out.println("Server: MINISERVER");
            // this blank line signals the end of the headers
            out.println("");
            // Send the HTML page
            out.println("<H1>Welcome to the Mini Server</H1>");
            out.println("<H2>Request Method->" + request_method + "</H2>");
            out.println("<H2>Post->" + postData + "</H2>");
            out.println("<form name=\"input\" action=\"form_submited\" method=\"post\">");
            out.println("Username: <input type=\"text\" name=\"user\"><input type=\"submit\" value=\"Submit\"></form>");
            out.flush();
            ne.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



}




//class ThreadSocket extends Thread {
//    private Socket insocket;
//    ThreadSocket(Socket insocket) {
//        this.insocket = insocket;
//        this.start();
//    }
//    @Override
//    public void run() {
//        try {
//
//            InputStream is = insocket.getInputStream();
//            PrintWriter out = new PrintWriter(insocket.getOutputStream());
//            BufferedReader in = new BufferedReader(new InputStreamReader(is));
//            String line;
//            line = in.readLine();
//            String request_method = line;
//            System.out.println("HTTP-HEADER: " + line);
//            line = "";
//            // looks for post data
//            int postDataI = -1;
//            while ((line = in.readLine()) != null && (line.length() != 0)) {
//                System.out.println("HTTP-HEADER: " + line);
//                if (line.indexOf("Content-Length:") > -1) {
//                    postDataI = new Integer(line.substring(line.indexOf("Content-Length:") + 16,line.length())).intValue();
//                }
//
//
//
//            }
//            String postData = "";
//            // read the post data
//            if (postDataI > 0) {
//                char[] charArray = new char[postDataI];
//                in.read(charArray, 0, postDataI);
//                postData = new String(charArray);
//            }
//            out.println("HTTP/1.0 200 OK");
//            out.println("Content-Type: text/html; charset=utf-8");
//            out.println("Server: MINISERVER");
//            // this blank line signals the end of the headers
//            out.println("");
//            // Send the HTML page
//            out.println("<H1>Welcome to the Mini Server</H1>");
//            out.println("<H2>Request Method->" + request_method + "</H2>");
//            out.println("<H2>Post->" + postData + "</H2>");
//            out.println("<form name=\"input\" action=\"form_submited\" method=\"post\">");
//            out.println("Username: <input type=\"text\" name=\"user\"><input type=\"submit\" value=\"Submit\"></form>");
//            out.close();
//            insocket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}