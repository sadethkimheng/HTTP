package Http;

import java.io.*;

public class ResourceLoader {

	private static final String FILEBASE = "/resources";
	static final String PROJECT_DIR = System.getProperty("user.dir");


	public InputStream getResource(String uri) {
		return ResourceLoader.class.getResourceAsStream(FILEBASE + uri);
	}

	public static boolean fileExisted1(String path) throws IOException {

		String FILEPATH = PROJECT_DIR+"/src"+FILEBASE + path;
			boolean logdir = new File(FILEPATH).exists();
			if (!logdir) {
				File DirFile = new File(FILEPATH);
				DirFile.mkdirs();
			}
		return logdir;

	}


}
