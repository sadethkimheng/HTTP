package Http;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * A utility that downloads a file from a URL.
 * @author www.codejava.net
 *
 */
public class DownloadFile implements Runnable {

String link;
File out;
    public DownloadFile(String link, File out)
    {

        this.link = link;
        this.out = out;
    }


    @Override
    public void run() {
        try {
            URL url = new URL(link);
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            double fileSize = (double)http.getContentLengthLong();
            BufferedInputStream in = new BufferedInputStream(http.getInputStream());
            FileOutputStream fos = new FileOutputStream(this.out);
            BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);
            byte[] buffer = new byte[1024];
            double downloaded = 0.00;
            int read = 0;
            double percentDownloaded = 0.00;
            while ((read = in.read(buffer, 0, 1024)) >= 0){
            bout.write(buffer,0,read);
            downloaded += read;
//            percentDownloaded = (downloaded*100)/fileSize;
//            String percent = String .format("%.4f",percentDownloaded);
//            System.out.println("Download" + percent + "% of file");
            }
            bout.close();
            in.close();
            System.out.println("Download Done");


        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}