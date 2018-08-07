package Http;

import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.net.Socket;
import java.util.Properties;

import static com.sun.org.apache.xerces.internal.utils.SecuritySupport.getResourceAsStream;

public class ResponseHandler {
    Properties properties = new Properties();
    private final static String producerPropsFile = "output.properties";

    InputStream inputStream = ResourceLoader.class.getClassLoader().getResourceAsStream(producerPropsFile);

    ResourceLoader resourceLoader = new ResourceLoader();
    InputStream input;
    InputStreamReader inet;
    BufferedReader in;
    PrintWriter pw;
    OutputStream out = null;

   public void response(Socket sock, String path ) throws IOException {

       System.out.println(path);
       properties.load(inputStream);
       String Root = properties.getProperty("RootServer");
       System.out.println(Root);
       System.out.println("Sending response ");

        out = sock.getOutputStream();
       System.out.println("++++++++++++++++"+path);
       input = resourceLoader.getResource(path);
       inet = new InputStreamReader(input);
       in = new BufferedReader(inet);


         pw = new PrintWriter(out);
				pw.println("HTTP/1.0 200 OK");
                pw.println("Content-Type:"+guessContentType(path));
                 pw.println("");




       String line;
       while ((line = in.readLine()) != null) {
//           System.out.println("str => " +line);
           if(path.endsWith(".txt/")  ) {
           pw.println(line);
           }
           else {
               pw.println("<a href='" + line + "/'" + "> " + line + "</a><br>");

           }

       }

       in.close();
       pw.close();
   }


    private static String guessContentType(String path)
    {
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
        else if (path.endsWith("/") )
            return "text/html";
        else
            return "text/html";
    }
    }

