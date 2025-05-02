package knight.utils;

import java.io.IOException;
import java.io.InputStream;

public class FileContentLoader
{
	public static String loadFromResources(String relativePath)
	{
		try {
			InputStream inputStream = FileContentLoader.class.getClassLoader().getResourceAsStream(relativePath);
			if (inputStream == null) {
				throw new IOException("Resource not found: " + relativePath);
			}
			String content = new String(inputStream.readAllBytes());
			inputStream.close();
			return content;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
