package Http;

import java.io.*;
import java.util.Properties;

public class ResourceLoader {

	private static final String FILEBASE = "resources";

	static final String PROJECT_DIR = System.getProperty("user.dir");
	private final static String producerPropsFile = "output.properties";
	public InputStream getResource(String uri) throws IOException {

		return  ResourceLoader.class.getClassLoader().getResourceAsStream(FILEBASE+uri);
	}


	public static boolean fileExisted1(String path) throws IOException {

		Properties properties = new Properties();

		InputStream inputStream = ResourceLoader.class.getClassLoader().getResourceAsStream(producerPropsFile);

		properties.load(inputStream);
		String TOPIC = properties.getProperty("TOPIC");

		String FILEPATH = PROJECT_DIR+ TOPIC + path;
			boolean logdir = new File(FILEPATH).exists();
			if (!logdir) {
				File DirFile = new File(FILEPATH);
				DirFile.mkdirs();
			}
		return logdir;
	}


}
