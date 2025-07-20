package knight.compiler.preprocessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PreProcessor
{
	private static final String STD_LIB_PATH = "/usr/local/include/knight/";
	private final Set<String> processedImports = new HashSet<>();

	public List<File> process(String filename) throws IOException
	{
		processedImports.clear();
		List<File> result = new ArrayList<>();
		processFile(new File(filename), result);
		return result;
	}

	private void processFile(File file, List<File> collected) throws IOException
	{
		if (processedImports.contains(file.getCanonicalPath())) {
			return;
		}
		processedImports.add(file.getCanonicalPath());
		collected.add(file);

		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line;
		while ((line = reader.readLine()) != null) {
			line = line.trim();
			if (line.startsWith("import ")) {
				String importPath = extractImportPath(line);
				File importedFile = resolveImportPath(importPath, file.getParentFile());
				processFile(importedFile, collected);
			}
		}
		reader.close();
	}

	private String extractImportPath(String line)
	{
		String importPath = line.substring("import ".length()).trim();
		if (importPath.endsWith(";")) {
			importPath = importPath.substring(0, importPath.length() - 1).trim();
		}
		if (importPath.startsWith("\"") && importPath.endsWith("\"")) {
			return importPath.substring(1, importPath.length() - 1);
		}
		return STD_LIB_PATH + importPath + ".knight";
	}

	private File resolveImportPath(String importPath, File baseDir)
	{
		File importedFile = new File(importPath);
		if (!importedFile.isAbsolute()) {
			importedFile = new File(baseDir, importPath);
		}
		return importedFile;
	}
}