package Http;

import java.io.*;
import java.util.Properties;

public class ResourceLoader {


	private final static String producerPropsFile = "output.properties";


	public static boolean fileExisted1(String path) throws IOException {

		Properties properties = new Properties();

		InputStream inputStream = ResourceLoader.class.getClassLoader().getResourceAsStream(producerPropsFile);

		properties.load(inputStream);
		String Root = properties.getProperty("RootServer");
		String FILEPATH = Root + path;
			boolean logdir = new File(FILEPATH).exists();
			if (!logdir) {
				File DirFile = new File(FILEPATH);
				DirFile.mkdirs();
			}
		return logdir;
	}


}
