package knight;

import knight.compiler.ast.AST;
import knight.compiler.ast.ASTPrinter;
import knight.compiler.ast.ASTProgram;
import knight.compiler.codegen.CodeGenerator;
import knight.compiler.lexer.Lexer;
import knight.compiler.parser.Parser;
import knight.compiler.semantics.BuildSymbolTree;
import knight.compiler.semantics.diagnostics.NameError;
import knight.compiler.semantics.diagnostics.SemanticErrors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Main
{
	public static void main(String[] args)
	{
		if (args.length < 1) {
			System.err.println("Usage: java Main <filename>.knight");
			System.exit(1);
		}

		Main main = new Main();
		main.codeGen(args);
	}

	public void codeGen(String[] args)
	{
		String filename = args[0];

		try {
			if (!isFileValid(filename)) {
				return;
			}

			BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
			Lexer lexer = new Lexer(bufferedReader);
			Parser parser = new Parser(lexer);
			AST tree = parser.parse();

			if (containsFlag(args, "-ast")) {
				ASTPrinter printer = new ASTPrinter();
				System.out.println(printer.visit((ASTProgram) tree));
				System.exit(0);
			}

			if (tree != null) {
				BuildSymbolTree buildSymbolTree = new BuildSymbolTree();
				buildSymbolTree.visit((ASTProgram) tree);

				// SymbolProgram symbolProgram = buildSymbolTree.getSymbolProgram();

				// NameAnalyser nameAnalyser = new NameAnalyser(symbolProgram);
				// nameAnalyser.visit((ASTProgram) tree);

				// TypeAnalyser typeAnalyser = new TypeAnalyser(symbolProgram);
				// typeAnalyser.visit((ASTProgram) tree);

				if (SemanticErrors.errorList.isEmpty()) {
					String path = getFileDirPath(filename);
					// ConstantFolding.optimize(tree);

					CodeGenerator cg = new CodeGenerator(path, filename);
					cg.visit((ASTProgram) tree);

					this.compileCPPFile(args, path, filename);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		} finally {
			if (!SemanticErrors.errorList.isEmpty()) {
				SemanticErrors.sort();
				for (NameError e : SemanticErrors.errorList) {
					System.err.println(e);
				}
			}
		}
	}

	private boolean containsFlag(String[] args, String flag)
	{
		for (String arg : args) {
			if (arg.equals(flag)) {
				return true;
			}
		}
		return false;
	}

	private void compileCPPFile(String[] args, String path, String fileName)
	{
		String filename = removeFileExtension(fileName);

		try {
			ProcessBuilder compileProcessBuilder;
			if (containsFlag(args, "-debug")) {
				compileProcessBuilder = new ProcessBuilder("g++", "-g", "-o", path + filename,
						path + filename + ".cpp");
			} else {
				compileProcessBuilder = new ProcessBuilder("g++", "-o", path + filename, path + filename + ".cpp");
			}

			Process compileProcess = compileProcessBuilder.start();
			int compileExitCode = compileProcess.waitFor();

			if (compileExitCode != 0) {
				System.err.println("Compilation failed.");
				System.exit(1);
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

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

	private String getFileExtension(File file)
	{
		String name = file.getName();
		try {
			return name.substring(name.lastIndexOf(".") + 1);
		} catch (Exception e) {
			return "";
		}
	}

	private String removeFileExtension(String fileName)
	{
		int lastIndex = fileName.lastIndexOf(".");

		if (lastIndex != -1) {
			return fileName.substring(0, lastIndex);
		}

		return fileName;
	}

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
