package knight.playground;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import knight.compiler.Compiler;
import knight.compiler.ast.program.ASTProgram;
import knight.compiler.codegen.CodeGenerator;
import knight.compiler.optimizations.ConstantFolding;
import knight.compiler.preprocessor.PreProcessor;
import knight.compiler.semantics.diagnostics.Diagnostic;
import knight.compiler.semantics.diagnostics.DiagnosticReporter;
import knight.compiler.semantics.diagnostics.DiagnosticSeverity;
import knight.compiler.semantics.model.SymbolProgram;
import knight.utils.FileHelper;

public final class CompilerFacade
{
	public static final int MAX_SOURCE_SIZE = 64 * 1024;

	private static final Object COMPILE_LOCK = new Object();

	private CompilerFacade()
	{
	}

	public static CompileResult compileFromSource(String source)
	{
		if (source == null) {
			return CompileResult.failure(List.of("Source must not be null"), List.of(), "");
		}

		if (source.getBytes(StandardCharsets.UTF_8).length > MAX_SOURCE_SIZE) {
			return CompileResult.failure(List.of("Source exceeds maximum size of 64 KB"), List.of(), "");
		}

		try {
			Path tempDir = Files.createTempDirectory("knight-playground-");
			try {
				Path sourceFile = tempDir.resolve("playground.knight");
				Files.writeString(sourceFile, source, StandardCharsets.UTF_8);
				return compileFromPath(sourceFile.toString());
			} finally {
				deleteDirectory(tempDir);
			}
		} catch (Exception e) {
			return CompileResult.failure(List.of("Failed to prepare source: " + e.getMessage()), List.of(), "");
		}
	}

	public static CompileResult compileFromPath(String filename)
	{
		synchronized (COMPILE_LOCK) {
			DiagnosticReporter.clear();

			try {
				File file = new File(filename);
				if (!file.exists()) {
					return CompileResult.failure(List.of(filename + ": No such file!"), List.of(), "");
				}

				if (!"knight".equals(FileHelper.getFileExtension(file))) {
					return CompileResult.failure(List.of(filename + ": Invalid file extension!"), List.of(), "");
				}

				PreProcessor preProcessor = new PreProcessor();
				List<File> sourceFiles = preProcessor.process(filename);

				Compiler compiler = new Compiler();
				List<ASTProgram> astPrograms = compiler.parseFiles(sourceFiles);

				if (astPrograms.isEmpty()) {
					return buildFailureResult("");
				}

				SymbolProgram symbolProgram = compiler.buildSymbolProgram(astPrograms);
				compiler.semantics(astPrograms, symbolProgram);

				if (DiagnosticReporter.hasErrors()) {
					return buildFailureResult("");
				}

				String path = FileHelper.getFileDirPath(filename);
				CodeGenerator codeGenerator = new CodeGenerator(path, filename);

				for (ASTProgram astProgram : astPrograms) {
					ConstantFolding.optimize(astProgram);
					codeGenerator.visit(astProgram);
				}

				String generatedCpp = codeGenerator.getGeneratedCode();
				return CompileResult.success(generatedCpp, collectDiagnostics(DiagnosticSeverity.WARNING));
			} catch (Exception e) {
				return CompileResult.failure(List.of("Compilation failed: " + e.getMessage()),
						collectDiagnostics(DiagnosticSeverity.WARNING), "");
			}
		}
	}

	private static CompileResult buildFailureResult(String generatedCpp)
	{
		DiagnosticReporter.sort();
		return CompileResult.failure(collectDiagnostics(DiagnosticSeverity.ERROR),
				collectDiagnostics(DiagnosticSeverity.WARNING), generatedCpp);
	}

	private static List<String> collectDiagnostics(DiagnosticSeverity severity)
	{
		List<String> messages = new ArrayList<>();
		for (Diagnostic diagnostic : DiagnosticReporter.getDiagnostics()) {
			if (diagnostic.getSeverity() == severity) {
				messages.add(diagnostic.toString());
			}
		}
		return messages;
	}

	private static void deleteDirectory(Path directory)
	{
		try {
			if (!Files.exists(directory)) {
				return;
			}

			Files.walk(directory).sorted((a, b) -> b.compareTo(a)).forEach(path -> {
				try {
					Files.deleteIfExists(path);
				} catch (Exception ignored) {
				}
			});
		} catch (Exception ignored) {
		}
	}
}
