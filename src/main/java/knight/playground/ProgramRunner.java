package knight.playground;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public final class ProgramRunner
{
	private static final int COMPILE_TIMEOUT_SECONDS = 10;
	private static final int RUN_TIMEOUT_SECONDS = 5;

	private ProgramRunner()
	{
	}

	public static RunResult run(String cppSource, Path workDir)
	{
		try {
			Path cppFile = workDir.resolve("program.cpp");
			Path executable = workDir.resolve("program");
			Files.writeString(cppFile, cppSource, StandardCharsets.UTF_8);

			ProcessBuilder compileProcess = new ProcessBuilder("g++", "-std=c++17", "-O2", "-o", executable.toString(),
					cppFile.toString());
			compileProcess.directory(workDir.toFile());
			compileProcess.redirectErrorStream(false);

			Process compile = compileProcess.start();
			String compileStderr = readStream(compile.getErrorStream());
			if (!compile.waitFor(COMPILE_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
				compile.destroyForcibly();
				return RunResult.failure("C++ compilation timed out after " + COMPILE_TIMEOUT_SECONDS + " seconds");
			}

			if (compile.exitValue() != 0) {
				String message = compileStderr.isBlank() ? "C++ compilation failed" : compileStderr.trim();
				return RunResult.failure(message);
			}

			ProcessBuilder runProcess = new ProcessBuilder(executable.toString());
			runProcess.directory(workDir.toFile());
			runProcess.redirectErrorStream(false);

			Process run = runProcess.start();
			String stdout = readStream(run.getInputStream());
			String stderr = readStream(run.getErrorStream());

			if (!run.waitFor(RUN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
				run.destroyForcibly();
				return RunResult.failure("Program execution timed out after " + RUN_TIMEOUT_SECONDS + " seconds");
			}

			return RunResult.success(stdout, stderr, run.exitValue());
		} catch (IOException e) {
			if (isGppMissing(e)) {
				return RunResult.failure("g++ is not installed or not available on PATH");
			}
			return RunResult.failure("Failed to compile or run program: " + e.getMessage());
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return RunResult.failure("Program execution was interrupted");
		}
	}

	private static boolean isGppMissing(IOException e)
	{
		String message = e.getMessage();
		return message != null && (message.contains("Cannot run program \"g++\"") || message.contains("error=2"));
	}

	private static String readStream(java.io.InputStream inputStream) throws IOException
	{
		return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
	}
}
