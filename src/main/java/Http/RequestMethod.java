package Http;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class RequestMethod {



    public static String getRequest(Socket sock) throws Exception {

        StringBuilder requestString = new StringBuilder();

        byte [] requestBytes = new byte[40000];

        InputStream in = sock.getInputStream();


        int n = in.read(requestBytes);

        String requestPart1 = null;

        if (n != -1) {
            requestPart1 = new String(requestBytes, 0, n);
            requestString.append(requestPart1);
        }

        if (requestPart1 != null && requestPart1.contains("multipart/form-data")) {
            n = in.read(requestBytes);
            if (n != -1) {
                requestString.append( new String(requestBytes, 0, n));
            }
        }

        return requestString.toString();
    }

    public static String getMethod(String request) {
        StringTokenizer stk = new StringTokenizer(request);
        String method = stk.hasMoreTokens() ? stk.nextToken() : null;

        return method;
    }


    public static String getRequestUri
            (String request) {
        StringTokenizer stk = new StringTokenizer(request);
        String method = stk.hasMoreTokens() ? stk.nextToken() : null;
        String uri = stk.hasMoreTokens() ? stk.nextToken() : null;


        return uri;

    }


    public static StringTokenizer checkReferer(String request) {
        StringTokenizer stk = new StringTokenizer(request);
        while (stk.hasMoreTokens())
        {
//            System.out.println(stk);
        }
        return stk;
    }

    public static List<UploadedFile> getUploadedFileInfo (String request) {

        String[] splitted = request.split("\n");

        int index = 1;
        List<UploadedFile> uploadedFiles = new ArrayList<>();



        for (int i = 0; i < splitted.length; i++) {
            UploadedFile uploadedFile = new UploadedFile();

            if(splitted[i].startsWith("Content-Disposition:")){
                String str[] = splitted[i].split(";");
                String fullFileName = String.valueOf(str[str.length-1].substring(10)) ;
                fullFileName = fullFileName.substring(1, fullFileName.length()-2);

                uploadedFile.setFullFileName(fullFileName);
                uploadedFile.setFileName(fullFileName.split("[.]")[0]);
                uploadedFile.setExtension(fullFileName.split("[.]")[1]);

                for (int j = i+3; j < splitted.length-1; j++) {
                    if(splitted[j].startsWith("-----------")){
                        index++;
                        break;
                    }
                    uploadedFile.setContent(uploadedFile.getContent().concat(splitted[j] + "\n"));
                }
                uploadedFiles.add(uploadedFile);
            }
        }

        return uploadedFiles;
    }



    public static Boolean checkReferer1(String request) {
        StringTokenizer stk = new StringTokenizer(request);
        while (stk.hasMoreTokens()) {
            if (stk.nextToken().equals("Referer:")) {
                return true;
            }

        }
        return false;


    }
}

//fileName ,extension, path, content
