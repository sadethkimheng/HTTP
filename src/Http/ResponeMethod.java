package Http;

import java.io.*;
import java.net.Socket;

public class ResponeMethod {

    int value;
    ResourceLoader resourceLoader = new ResourceLoader();
    InputStream input;
    InputStreamReader inet;
    BufferedReader in;
    PrintWriter pw;

    OutputStream out = null;

   public void methodrespone(Socket sock, String path) throws IOException {

       System.out.println(path);

       System.out.println("Sending response ");

        out = sock.getOutputStream();
       input = resourceLoader.getResource(path);
      inet = new InputStreamReader(input);
       in = new BufferedReader(inet);


         pw = new PrintWriter(out);
				pw.println("HTTP/1.0 200 OK");
                pw.println("Content-Type: text/html");
                pw.println("");


       pw.println();
       pw.flush();


       String line;
       while ((line = in.readLine()) != null) {
           if (line.length() == 0)
               break;
           pw.print(line + "\r\n");
       }

       in.close();
       pw.close();
   }

    }

