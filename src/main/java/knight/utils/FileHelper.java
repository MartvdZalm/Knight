package knight.utils;

import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class FileHelper
{
	public static boolean isFileValid(String filename)
	{
		File file = new File(filename);

		if (!file.exists()) {
			System.err.println(filename + ": No such file!");
			return false;
		}

		String fileExtension = FileHelper.getFileExtension(file);
		if (!"knight".equals(fileExtension)) {
			System.err.println(filename + ": Invalid file extension!");
			return false;
		}

		return true;
	}

	public static String getFileExtension(File file)
	{
		String name = file.getName();
		int lastIndex = name.lastIndexOf(".");
		if (lastIndex == -1 || lastIndex == name.length() - 1) {
			return "";
		}

		return name.substring(lastIndex + 1);
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
			File file = new File(filename);
			String path = file.getParent();
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
			File file = new File(path + updatedFilename + ".cpp");
			PrintWriter writer = new PrintWriter(file, StandardCharsets.UTF_8);
			writer.println(code);
			writer.close();
			return file;
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

		return null;
	}
}
