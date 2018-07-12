package Sample;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class GetMethod {

     void start() {
        ServerSocket s;
        String gets = "";
        System.out.println("Start on port 8080");
        try {
            // create the main server socket
            s = new ServerSocket(8080);
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return;
        }

        System.out.println("Waiting for connection");
        for (;;) {
            try {
                // wait for a connection
                Socket remote = s.accept();
                // remote is now the connected socket
                System.out.println("Connection, sending data.");
                BufferedReader in = new BufferedReader(new InputStreamReader(remote.getInputStream()));
                PrintWriter out = new PrintWriter(remote.getOutputStream());

                String str = ".";
                while (!str.equals("")) {
                    str = in.readLine();
                    if (str.contains("Get")){
                        gets = str;
                        System.out.println(gets);

                        break;
                    }

                }

                out.println("HTTP/1.0 200 OK");
                out.println("Content-Type: text/html");
                out.println("");
                // Send the HTML page
                String method = "GET";
                out.print("<html><form method="+method+">");
                out.print("<input type=text name=a><input type=submit></form></html>");
                out.println(gets);
                out.flush();

                remote.close();
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
        }
    }

    public static void main(String args[]) {
        GetMethod ws = new GetMethod();
        ws.start();
    }
}