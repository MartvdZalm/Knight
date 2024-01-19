package knight;

import java.io.*;

import knight.ast.Program;
import knight.ast.Tree;
import knight.parser.Parser;
import knight.semantics.NameError;
import knight.semantics.SemanticErrors;
import knight.symbol.SymbolProgram;
import knight.visitor.BuildSymbolProgramVisitor;
import knight.visitor.CodeGenerator;
import knight.visitor.NameAnalyserTreeVisitor;
import knight.visitor.TypeAnalyser;

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
			Parser p = new Parser(bufferedReader);
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
					String path = getFileDirPath(filename);
					CodeGenerator cg = new CodeGenerator(path, filename);
					cg.visit((Program) tree);

					if (!containsFlag(args, "-s")) {
						compileAssemblyFile(path, filename);
	                }
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

	private boolean containsFlag(String[] args, String flag)
	{
	    for (String arg : args) {
	        if (arg.equals(flag)) {
	            return true;
	        }
	    }
	    return false;
	}

	private void compileAssemblyFile(String path, String fileName)
	{
		String filename = removeFileExtension(fileName);

	    try {
	        ProcessBuilder assemblerProcessBuilder = new ProcessBuilder("as", "-o", path + filename + ".o", path + filename + ".s");
	        Process assemblerProcess = assemblerProcessBuilder.start();
	        int assemblerExitCode = assemblerProcess.waitFor();

	        if (assemblerExitCode == 0) {
	            ProcessBuilder linkerProcessBuilder = new ProcessBuilder("ld", "-o", path + filename, path + filename + ".o", "-e", "main");
	            Process linkerProcess = linkerProcessBuilder.start();
	            int linkerExitCode = linkerProcess.waitFor();
	        }

        	ProcessBuilder removeAssemblyFileBuilder = new ProcessBuilder("rm", path + filename + ".s");
            removeAssemblyFileBuilder.start();

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
