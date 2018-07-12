package Http;

import java.io.InputStream;

public class ResourceLoader {

	private static final String FILEBASE = "/resources";

	public InputStream getResource(String uri) {
		return ResourceLoader.class.getResourceAsStream(FILEBASE + uri);
	}
}
