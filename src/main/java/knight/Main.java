package knight;

import knight.compiler.Compiler;
import knight.compiler.ast.program.ASTProgram;
import knight.compiler.codegen.CodeGenerator;
import knight.compiler.optimizations.ConstantFolding;
import knight.compiler.preprocessor.PreProcessor;
import knight.compiler.semantics.diagnostics.Diagnostic;
import knight.compiler.semantics.diagnostics.DiagnosticReporter;
import knight.compiler.semantics.model.SymbolProgram;
import knight.utils.FileHelper;

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

			System.out.println("Compiling: " + filename);

			// Step 1: Preprocess and collect all source files (including libraries)
			PreProcessor preProcessor = new PreProcessor();
			List<File> sourceFiles = preProcessor.process(filename);

			System.out.println("Found " + sourceFiles.size() + " source files:");
			for (File file : sourceFiles) {
				System.out.println("  - " + file.getPath());
			}

			// Step 2: Parse all files into ASTs
			Compiler compiler = new Compiler();
			List<ASTProgram> astPrograms = compiler.parseFiles(sourceFiles);

			if (astPrograms.isEmpty()) {
				System.err.println("No valid ASTs generated from source files");
				return;
			}

			System.out.println("Successfully parsed " + astPrograms.size() + " files");

			// Step 3: Build unified symbol program (including libraries)
			SymbolProgram symbolProgram = compiler.buildSymbolProgram(astPrograms);
			System.out.println("Built unified symbol program");

			for (String funcName : symbolProgram.getFunctions().keySet()) {
				System.out.println(funcName);
			}

			// Step 4: Run semantics analysis on all programs
			compiler.semantics(astPrograms, symbolProgram);

			// Step 5: Report any diagnostics
			if (DiagnosticReporter.hasErrors()) {
				System.err.println("\nCompilation failed with errors:");
				DiagnosticReporter.sort();
				for (Diagnostic diagnostic : DiagnosticReporter.getDiagnostics()) {
					System.err.println(diagnostic);
				}
				System.exit(1);
			} else {
				System.out.println("Semantics analysis completed successfully");

				// Show warnings if any
				List<Diagnostic> diagnostics = DiagnosticReporter.getDiagnostics();
				if (!diagnostics.isEmpty()) {
					System.out.println("\nWarnings:");
					DiagnosticReporter.sort();
					for (Diagnostic diagnostic : diagnostics) {
						System.out.println(diagnostic);
					}
				}
			}

			// Step 6: Generate code
			String path = FileHelper.getFileDirPath(filename);
			CodeGenerator codeGenerator = new CodeGenerator(path, filename);

			for (ASTProgram astProgram : astPrograms) {
				ConstantFolding.optimize(astProgram);
				codeGenerator.visit(astProgram);
			}

			// String code =
			// String.valueOf(codeGenerator.generateHeaders().append(codeGenerator.getGeneratedCode()));
			String code = String.valueOf(codeGenerator.getGeneratedCode());
			FileHelper.write(code, path, filename);

			System.out.println("Code generation completed successfully");
			System.out.println("Output written to: " + path + "/" + FileHelper.removeFileExtension(filename) + ".cpp");

		} catch (Exception e) {
			System.err.println("Compilation failed with exception: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		} finally {
			// Always report any diagnostics that might have been generated
			if (DiagnosticReporter.hasErrors()) {
				System.err.println("\nCompilation failed with errors:");
				DiagnosticReporter.sort();
				for (Diagnostic diagnostic : DiagnosticReporter.getDiagnostics()) {
					System.err.println(diagnostic);
				}
			}
		}
	}
}
