package knight.playground;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class PlaygroundService
{
	private PlaygroundService()
	{
	}

	public static PlaygroundResponse compileAndRun(String source)
	{
		if (source == null) {
			return PlaygroundResponse.fromCompileFailure(
					CompileResult.failure(java.util.List.of("Source must not be null"), java.util.List.of(), ""));
		}

		if (source.getBytes(StandardCharsets.UTF_8).length > CompilerFacade.MAX_SOURCE_SIZE) {
			return PlaygroundResponse.fromCompileFailure(CompileResult
					.failure(java.util.List.of("Source exceeds maximum size of 64 KB"), java.util.List.of(), ""));
		}

		Path workDir = null;
		try {
			workDir = Files.createTempDirectory("knight-playground-");
			Path sourceFile = workDir.resolve("playground.knight");
			Files.writeString(sourceFile, source, StandardCharsets.UTF_8);

			CompileResult compileResult = CompilerFacade.compileFromPath(sourceFile.toString());
			if (!compileResult.isSuccess()) {
				return PlaygroundResponse.fromCompileFailure(compileResult);
			}

			RunResult runResult = ProgramRunner.run(compileResult.getGeneratedCpp(), workDir);
			return PlaygroundResponse.fromCompileAndRun(compileResult, runResult);
		} catch (Exception e) {
			return PlaygroundResponse.fromCompileFailure(CompileResult.failure(
					java.util.List.of("Playground request failed: " + e.getMessage()), java.util.List.of(), ""));
		} finally {
			if (workDir != null) {
				deleteDirectory(workDir);
			}
		}
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
					// Best-effort cleanup for temp directories.
				}
			});
		} catch (Exception ignored) {
			// Best-effort cleanup for temp directories.
		}
	}
}
