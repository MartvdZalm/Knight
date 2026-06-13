package knight.playground;

import java.util.Collections;
import java.util.List;

public class PlaygroundResponse
{
	private final boolean success;
	private final List<String> errors;
	private final List<String> warnings;
	private final String generatedCpp;
	private final String programOutput;
	private final String programError;
	private final int exitCode;

	public PlaygroundResponse(boolean success, List<String> errors, List<String> warnings, String generatedCpp,
			String programOutput, String programError, int exitCode)
	{
		this.success = success;
		this.errors = errors;
		this.warnings = warnings;
		this.generatedCpp = generatedCpp != null ? generatedCpp : "";
		this.programOutput = programOutput != null ? programOutput : "";
		this.programError = programError != null ? programError : "";
		this.exitCode = exitCode;
	}

	public static PlaygroundResponse fromCompileFailure(CompileResult compileResult)
	{
		return new PlaygroundResponse(false, compileResult.getErrors(), compileResult.getWarnings(),
				compileResult.getGeneratedCpp(), "", "", -1);
	}

	public static PlaygroundResponse fromCompileAndRun(CompileResult compileResult, RunResult runResult)
	{
		boolean success = compileResult.isSuccess() && runResult.isSuccess() && runResult.getExitCode() == 0;
		List<String> errors = compileResult.getErrors();
		if (compileResult.isSuccess() && !runResult.isSuccess()) {
			errors = List.of(runResult.getProgramError());
		}
		return new PlaygroundResponse(success, errors, compileResult.getWarnings(), compileResult.getGeneratedCpp(),
				runResult.getProgramOutput(), runResult.getProgramError(), runResult.getExitCode());
	}

	public boolean isSuccess()
	{
		return success;
	}

	public List<String> getErrors()
	{
		return Collections.unmodifiableList(errors);
	}

	public List<String> getWarnings()
	{
		return Collections.unmodifiableList(warnings);
	}

	public String getGeneratedCpp()
	{
		return generatedCpp;
	}

	public String getProgramOutput()
	{
		return programOutput;
	}

	public String getProgramError()
	{
		return programError;
	}

	public int getExitCode()
	{
		return exitCode;
	}
}
