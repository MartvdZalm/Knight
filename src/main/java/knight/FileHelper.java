package knight;

import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class FileHelper
{
	public static boolean isFileValid(String filename)
	{
		File f = new File(filename);

		if (!f.exists()) {
			System.err.println(filename + ": No such file!");
			return false;
		}

		String fileExtension = FileHelper.getFileExtension(f);
		if (!"knight".equals(fileExtension)) {
			System.err.println(filename + ": Invalid file extension!");
			return false;
		}

		return true;
	}

	public static String getFileExtension(File file)
	{
		String name = file.getName();
		try {
			return name.substring(name.lastIndexOf(".") + 1);
		} catch (Exception e) {
			return "";
		}
	}

	public static String removeFileExtension(String fileName)
	{
		int lastIndex = fileName.lastIndexOf(".");

		if (lastIndex != -1) {
			return fileName.substring(0, lastIndex);
		}

		return fileName;
	}

	public static String getFileDirPath(String filename)
	{
		try {
			File f = new File(filename);
			String path = f.getParent();
			if (path == null) {
				return "";
			}
			return path + File.separator;
		} catch (Exception e) {
			return "";
		}
	}

	public static File write(String code, String path, String filename)
	{
		String updatedFilename = filename.substring(0, filename.lastIndexOf("."));
		try {
			File f = new File(path + updatedFilename + ".cpp");
			PrintWriter writer = new PrintWriter(f, StandardCharsets.UTF_8);
			writer.println(code);
			writer.close();
			return f;
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

		return null;
	}
}
