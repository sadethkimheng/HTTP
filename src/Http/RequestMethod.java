package Http;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class RequestMethod {



    public static String getRequest(Socket sock) throws Exception {

        StringBuilder requestString = new StringBuilder();

        byte [] requestBytes = new byte[40000];

        InputStream in = sock.getInputStream();

        //test


        //test

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

    public static boolean fileExisted (String request) {

        String[] splitted = request.split("\n");

        int index = 1;
        List<UploadedFile> uploadedFiles = new ArrayList<>();

        UploadedFile uploadedFile = new UploadedFile();

        for (int i = 0; i < splitted.length; i++) {
            if(splitted[i].startsWith("Content-Type:")){
                uploadedFile.setExtension(splitted[i].substring(14));
                for (int j = i+2; j < splitted.length-1; j++) {
                    if(splitted[j].startsWith("Content-Type:")){
                        index++;
                        break;
                    }
                    uploadedFile.setContent(uploadedFile.getContent().concat(splitted[j] + "\n"));
                }
                uploadedFiles.add(uploadedFile);
            }
        }

        System.out.println("Number of file uploaded:  "+ index);

        for (int i = 0; i < uploadedFiles.size() ; i++) {
            UploadedFile uf = uploadedFiles.get(i);
            System.out.println("==================");
            System.out.println(uf.getExtension());
            System.out.println(uf.getContent());
            System.out.println("===================== \n");
        }

        return false;
    }





}

//fileName ,extension, path, content
