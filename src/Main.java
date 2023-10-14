package src;
import java.io.File;

import src.ast.Program;
import src.ast.Tree;
import src.parser.Parser;
import src.semantics.NameError;
import src.semantics.SemanticErrors;
import src.symbol.SymbolProgram;
import src.visitor.BuildSymbolProgramVisitor;
import src.visitor.CodeGenerator;
import src.visitor.NameAnalyserTreeVisitor;
import src.visitor.TypeAnalyser;

/**
 * This is the main class/file of the whole compilers source code that has the main method.
 */
public class Main
{
	/**
	 * The entry point of the compiler.
	 * @param args Command-line arguments. Expects a single argument: the filename of the Knight source code.
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("Usage: java Main <filename>.knight");
			System.exit(1);
		}
	
		Main main = new Main();
		main.codeGen(args[0]);
	}

	/**
	 * Generates code based on the provided input string.
	 * @param str The input string containing the file name.
	 */
	public void codeGen(String str)
	{
		try {
			if (!isFileValid(str)) {
				return;
			}

			Parser p = new Parser(str);
			Tree tree = p.parse();

			if (tree != null) {
				BuildSymbolProgramVisitor bspv = new BuildSymbolProgramVisitor();
				bspv.visit((Program) tree);

				SymbolProgram symbolProgram = bspv.getSymbolProgram();
				
				NameAnalyserTreeVisitor natv = new NameAnalyserTreeVisitor(symbolProgram);
				natv.visit((Program) tree);  

				TypeAnalyser ta = new TypeAnalyser(symbolProgram);
				ta.visit((Program) tree);

				if (SemanticErrors.errorList.size() == 0) {
					String path = getFileDirPath(str);
					CodeGenerator cg = new CodeGenerator(path);
					cg.visit((Program) tree);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		} finally {
			if (SemanticErrors.errorList.size() != 0) {
				SemanticErrors.sort();
				for (NameError e : SemanticErrors.errorList) {
					System.err.println(e);
				}
			}
		}
	}

	/**
	 * Checks whether a file is valid based on specific criteria.
	 * @param filename The name of the file to be checked.
	 * @return true if the file is valid, false otherwise.
	 */
    private boolean isFileValid(String filename)
	{
		File f = new File(filename);

		if (!f.exists()) {
			System.err.println(filename + ": No such file!");
			return false;
		}

		String fileExtension = getFileExtension(f);
		if (!"knight".equals(fileExtension)) {
			System.err.println(filename + ": Invalid file extension!");
			return false;
		}

		return true;
	}

	/**
	 * Retrieves the file extension from the given File object.
	 * @param file
	 * @return The file extension as a String.
	 */
    private String getFileExtension(File file)
	{
		String name = file.getName();
		try {
			return name.substring(name.lastIndexOf(".") + 1);
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Retrieves the directory path from the given filename.
	 * @param filename The name of the file from which to extract the directory path.
	 * @return The directory path as a String, including the file separator at the end.
	 */
	private String getFileDirPath(String filename)
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
}
