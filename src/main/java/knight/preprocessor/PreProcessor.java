package knight.preprocessor;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class PreProcessor
{
	private static final String STD_LIB_PATH = "/usr/local/include/knight/";
	private Set<String> processedImports = new HashSet<>();

	public BufferedReader process(String filename) throws IOException
	{
		processedImports.clear();
		String combinedContent = processFile(new File(filename));
		return new BufferedReader(new StringReader(combinedContent));
	}

	private String processFile(File file) throws IOException
	{
		StringBuilder content = new StringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line;

		while ((line = reader.readLine()) != null) {
			line = line.trim();
			if (line.startsWith("import ")) {
				handleImport(line, content, file.getParentFile());
			} else {
				content.append(line).append("\n");
			}
		}
		reader.close();

		return content.toString();
	}

	private void handleImport(String importLine, StringBuilder content, File baseDir) throws IOException
	{
		String importPath = importLine.substring("import ".length()).trim();

		if (importPath.endsWith(";")) {
			importPath = importPath.substring(0, importPath.length() - 1).trim();
		}

		if (processedImports.contains(importPath)) {
			return;
		}
		processedImports.add(importPath);

		String importedContent;
		if (importPath.startsWith("\"") && importPath.endsWith("\"")) {
			String relativePath = importPath.substring(1, importPath.length() - 1);
			File importedFile = new File(baseDir, relativePath);
			importedContent = processFile(importedFile);
		} else {
			File importedFile = new File(STD_LIB_PATH + importPath + ".knight");
			importedContent = processFile(importedFile);
		}

		content.append(importedContent).append("\n");
	}
}