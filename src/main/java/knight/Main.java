package knight;

import knight.compiler.Compiler;
import knight.compiler.ast.program.ASTProgram;
import knight.compiler.codegen.CodeGenerator;
import knight.compiler.optimizations.ConstantFolding;
import knight.compiler.preprocessor.PreProcessor;
import knight.compiler.semantics.diagnostics.Diagnostic;
import knight.compiler.semantics.diagnostics.DiagnosticReporter;
import knight.compiler.semantics.model.SymbolProgram;

import java.io.File;
import java.util.List;

public class Main
{
	public static void main(String[] args)
	{
		if (args.length < 1) {
			System.err.println("Usage: java Main <filename>.knight");
			System.exit(1);
		}

		Main main = new Main();
		main.compile(args);
	}

	public void compile(String[] args)
	{
		String filename = args[0];

		try {
			if (!FileHelper.isFileValid(filename)) {
				return;
			}

			PreProcessor preProcessor = new PreProcessor();
			List<File> sourceFiles = preProcessor.process(filename);

			Compiler compiler = new Compiler();
			List<ASTProgram> astPrograms = compiler.parseFiles(sourceFiles);

			if (!astPrograms.isEmpty()) {
				SymbolProgram symbolProgram = compiler.buildSymbolProgram(astPrograms);
				compiler.semantics(astPrograms, symbolProgram);

				if (!DiagnosticReporter.hasErrors()) {

					DiagnosticReporter.sort();
					for (Diagnostic diagnostic : DiagnosticReporter.getDiagnostics()) {
						System.err.println(diagnostic);
					}

					String path = FileHelper.getFileDirPath(filename);
					CodeGenerator codeGenerator = new CodeGenerator(path, filename);

					for (ASTProgram astProgram : astPrograms) {
						ConstantFolding.optimize(astProgram);
						codeGenerator.visit(astProgram);
					}

					String code = String
							.valueOf(codeGenerator.generateHeaders().append(codeGenerator.getGeneratedCode()));
					FileHelper.write(code, path, filename);
//					 this.compileCPPFile(args, path, filename);
				}
			}

			// PreProcessor preProcessor = new PreProcessor();
			// BufferedReader bufferedReader = preProcessor.process(filename);
			// Lexer lexer = new Lexer(bufferedReader);
			// Parser parser = new Parser(lexer);
			// AST tree = parser.parse();
			//
			// if (containsFlag(args, "-ast")) {
			// ASTPrinter printer = new ASTPrinter();
			// System.out.println(printer.visit((ASTProgram) tree));
			// System.exit(0);
			// }
			//
			// if (tree != null) {
			// BuildSymbolTree buildSymbolTree = new BuildSymbolTree();
			// buildSymbolTree.visit((ASTProgram) tree);
			//
			// SymbolProgram symbolProgram = buildSymbolTree.getSymbolProgram();
			//
			// NameAnalyser nameAnalyser = new NameAnalyser(symbolProgram);
			// nameAnalyser.visit((ASTProgram) tree);
			//
			// TypeAnalyser typeAnalyser = new TypeAnalyser(symbolProgram);
			// typeAnalyser.visit((ASTProgram) tree);
			//
			// if (!DiagnosticReporter.hasErrors()) {
			//
			// DiagnosticReporter.sort();
			// for (Diagnostic diagnostic : DiagnosticReporter.getDiagnostics()) {
			// System.err.println(diagnostic);
			// }
			//
			// String path = getFileDirPath(filename);
			// ConstantFolding.optimize(tree);
			//
			// CodeGenerator cg = new CodeGenerator(path, filename);
			// cg.visit((ASTProgram) tree);
			//
			// this.compileCPPFile(args, path, filename);
			// }
			// }
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		} finally {
			if (DiagnosticReporter.hasErrors()) {
				DiagnosticReporter.sort();
				for (Diagnostic diagnostic : DiagnosticReporter.getDiagnostics()) {
					System.err.println(diagnostic);
				}
			}
		}
	}

	// private boolean containsFlag(String[] args, String flag)
	// {
	// for (String arg : args) {
	// if (arg.equals(flag)) {
	// return true;
	// }
	// }
	// return false;
	// }
	//
	// private void compileCPPFile(String[] args, String path, String fileName)
	// {
	// String filename = FileHelper.removeFileExtension(fileName);
	//
	// try {
	// ProcessBuilder compileProcessBuilder;
	// if (containsFlag(args, "-debug")) {
	// compileProcessBuilder = new ProcessBuilder("g++", "-g", "-o", path +
	// filename,
	// path + filename + ".cpp");
	// } else {
	// compileProcessBuilder = new ProcessBuilder("g++", "-o", path + filename, path
	// + filename + ".cpp");
	// }
	//
	// Process compileProcess = compileProcessBuilder.start();
	// int compileExitCode = compileProcess.waitFor();
	//
	// if (compileExitCode != 0) {
	// System.err.println("Compilation failed.");
	// System.exit(1);
	// }
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// System.exit(1);
	// }
	// }
}
