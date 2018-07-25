package Http;

import java.io.*;

public class ResourceLoader {

	private static final String FILEBASE = "/resources";

	public InputStream getResource(String uri) {
		return ResourceLoader.class.getResourceAsStream(FILEBASE + uri);
	}

	public static String  fileExisted1(String path){

		String FILEPATH = FILEBASE + path;
			boolean logdir = new File(FILEPATH).exists();
			while (!logdir) {
				File DirFile = new File("/Users/kimheng/Downloads/Source/HttpServer/src/" +FILEPATH);
				DirFile.mkdirs();
			break;
			}
		return "";
	}


}
