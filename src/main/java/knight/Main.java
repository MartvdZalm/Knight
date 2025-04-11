package knight;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;

import knight.compiler.ast.AST;
import knight.compiler.ast.ASTPrinter;
import knight.compiler.ast.ASTProgram;
import knight.compiler.parser.Parser;
import knight.compiler.passes.codegen.CodeGenerator;
import knight.compiler.passes.symbol.diagnostics.NameError;
import knight.compiler.passes.symbol.diagnostics.SemanticErrors;
import knight.preprocessor.PreProcessor;

/*
 * File: Main.java
 * @author: Mart van der Zalm
 * Date: 2025-04-10
 * Description:
 */
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
		String platformString = System.getProperty("os.name").toLowerCase();
		String filename = args[0];

		try {
			if (!isFileValid(filename)) {
				return;
			}

			PreProcessor preProcessor = new PreProcessor(filename);
			BufferedReader bufferedReader = preProcessor.process();

			Parser p = new Parser(bufferedReader);
			AST tree = p.parse();

			// if (containsFlag(args, "-ast")) {
			ASTPrinter printer = new ASTPrinter();
			System.out.println(printer.visit((ASTProgram) tree));
			// System.exit(0);
			// }

			// if (tree != null) {
			// BuildSymbolTree bspv = new BuildSymbolTree();
			// bspv.visit((ASTProgram) tree);

			// SymbolProgram symbolProgram = bspv.getSymbolProgram();

			// NameAnalyserTree natv = new NameAnalyserTree(symbolProgram);
			// natv.visit((ASTProgram) tree);

			// TypeAnalyser ta = new TypeAnalyser(symbolProgram);
			// ta.visit((ASTProgram) tree);

			// if (SemanticErrors.errorList.size() == 0) {
			String path = getFileDirPath(filename);
			// ConstantFolding.optimize(tree);
			CodeGenerator cg = new CodeGenerator(path, filename);
			cg.visit((ASTProgram) tree);
			this.compileCPPFile(args, path, filename);
			// }
			// }
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

	private File write(String code, String path, String filename)
	{
		try {
			File f = new File(path + filename + ".s");
			PrintWriter writer = new PrintWriter(f, "UTF-8");
			writer.println(code);
			writer.close();
			return f;
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

		return null;
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

		System.out.println(filename);

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

	private void compileAssemblyFile(String[] args, String path, String fileName)
	{
		String filename = removeFileExtension(fileName);

		try {
			ProcessBuilder assemblerProcessBuilder = null;
			if (containsFlag(args, "-debug")) {
				assemblerProcessBuilder = new ProcessBuilder("as", "-o", path + filename + ".o", "-gstabs",
						path + filename + ".s");
			} else {
				assemblerProcessBuilder = new ProcessBuilder("as", "-o", path + filename + ".o",
						path + filename + ".s");
			}
			Process assemblerProcess = assemblerProcessBuilder.start();
			int assemblerExitCode = assemblerProcess.waitFor();

			if (assemblerExitCode == 0) {
				ProcessBuilder linkerProcessBuilder = new ProcessBuilder("ld", "-o", path + filename,
						path + filename + ".o", "-e", "main");
				Process linkerProcess = linkerProcessBuilder.start();
				int linkerExitCode = linkerProcess.waitFor();
			}

			if (!containsFlag(args, "-debug")) {
				ProcessBuilder removeAssemblyFileBuilder = new ProcessBuilder("rm", path + filename + ".s");
				removeAssemblyFileBuilder.start();
			}

			ProcessBuilder removeObjectFileBuilder = new ProcessBuilder("rm", path + filename + ".o");
			removeObjectFileBuilder.start();

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
