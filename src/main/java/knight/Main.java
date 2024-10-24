/*
 * MIT License
 * 
 * Copyright (c) 2023, Mart van der Zalm
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package knight;

import java.io.*;

import knight.preprocessor.PreProcessor;
import knight.compiler.ast.declarations.ASTProgram;
import knight.compiler.ast.AST;
import knight.compiler.ast.ASTPrinter;
import knight.compiler.asm.ASM;
import knight.compiler.asm.ASMPlatform;
import knight.compiler.parser.Parser;
import knight.compiler.semantics.NameError;
import knight.compiler.semantics.SemanticErrors;
import knight.compiler.symbol.SymbolProgram;
import knight.compiler.visitor.BuildSymbolProgramVisitor;
import knight.compiler.visitor.CodeGenerator;
import knight.compiler.visitor.Codegen;
import knight.compiler.visitor.NameAnalyserTreeVisitor;
import knight.compiler.visitor.TypeAnalyser;
import knight.compiler.visitor.ConstantFolding;

/*
 * File: Main.java
 * @author: Mart van der Zalm
 * Date: 2024-01-06
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

			if (containsFlag(args, "-ast")) {
				ASTPrinter printer = new ASTPrinter();
				System.out.println(printer.visit((ASTProgram) tree));
				System.exit(0);
	        }

			if (tree != null) {
				BuildSymbolProgramVisitor bspv = new BuildSymbolProgramVisitor();
				bspv.visit((ASTProgram) tree);

				SymbolProgram symbolProgram = bspv.getSymbolProgram();
				
				NameAnalyserTreeVisitor natv = new NameAnalyserTreeVisitor(symbolProgram);
				natv.visit((ASTProgram) tree);  

				TypeAnalyser ta = new TypeAnalyser(symbolProgram);
				ta.visit((ASTProgram) tree);

				if (SemanticErrors.errorList.size() == 0) {
					String path = getFileDirPath(filename);
					ConstantFolding.optimize(tree);
					Codegen cg = new Codegen(path, filename);

					File f = new File(filename);
					String name = f.getName();
					String updated_filename = name.substring(0, name.lastIndexOf("."));

					ASM asmProgram = cg.visit((ASTProgram) tree);
					ASMPlatform platform = ASMPlatform.findOS(platformString);
					asmProgram.setPlatform(platform);
					write(asmProgram.toString(), path, updated_filename);
					
					if (!containsFlag(args, "-asm")) {
						compileAssemblyFile(args, path, filename);
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

	private void compileAssemblyFile(String[] args, String path, String fileName)
	{
		String filename = removeFileExtension(fileName);

	    try {
	    	ProcessBuilder assemblerProcessBuilder = null;
	    	if (containsFlag(args, "-debug")) {
	    		assemblerProcessBuilder = new ProcessBuilder("as", "-o", path + filename + ".o", "-gstabs", path + filename + ".s");
	    	} else {
	    		assemblerProcessBuilder = new ProcessBuilder("as", "-o", path + filename + ".o", path + filename + ".s");
	    	}
	        Process assemblerProcess = assemblerProcessBuilder.start();
	        int assemblerExitCode = assemblerProcess.waitFor();

	        if (assemblerExitCode == 0) {
	            ProcessBuilder linkerProcessBuilder = new ProcessBuilder("ld", "-o", path + filename, path + filename + ".o", "-e", "main");
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
