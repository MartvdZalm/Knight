package src;
import java.io.File;

import src.ast.Program;
import src.ast.Tree;
import src.parser.Parser;
import src.semantics.NameError;
import src.semantics.SemanticErrors;
import src.symbol.SymbolTable;
import src.visitor.BuildSymbolTableVisitor;
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
				BuildSymbolTableVisitor bstv = new BuildSymbolTableVisitor(); // Build the symbol table.
				bstv.visit((Program) tree); // This will build the symbol table and will check if there are duplicated variables or functions in the code.

				SymbolTable st = bstv.getSymTab(); // Get the symbol table that was build in the previous step and give this to the variable 'st'.
				
				NameAnalyserTreeVisitor natv = new NameAnalyserTreeVisitor(st); // Use the variable 'st' that contains the whole symbol table and give this to the name analyser class. 
				/*
				 * This will put bindings on all the names. For example the variable declaration here 'int a = 5' has a variable named 'a' with type of 'int' and the value '5'. 
				 * When some other variable will use the variable 'a' like this 'int b = a', then we need to check what kind of type the variable 'a' is. We do this by binding the 
				 * value '5' to the variable 'a'. With this we can easily help the programmer by giving an error if there is something wrong in the code.
				 */
				natv.visit((Program) tree);  

				TypeAnalyser ta = new TypeAnalyser(st); // Use the variable 'st' that contains the whole symbol table and give this to the name analyser class. 
				/*
				 * This will use the bindings that were set on the previous step and check if every type is correct in the code.
				 */
				ta.visit((Program) tree);

				/*
				 * This will check if there were any error while compiling. If there were errors while compiling the compiler prints them. Else the abstract syntax tree that has been made by the parser
				 * will be put in the code generator and generate the assembly.
				 */
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