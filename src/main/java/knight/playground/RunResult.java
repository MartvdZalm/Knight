package knight.playground;

public class RunResult
{
	private final String programOutput;
	private final String programError;
	private final int exitCode;
	private final boolean success;

	public RunResult(String programOutput, String programError, int exitCode, boolean success)
	{
		this.programOutput = programOutput != null ? programOutput : "";
		this.programError = programError != null ? programError : "";
		this.exitCode = exitCode;
		this.success = success;
	}

	public static RunResult success(String programOutput, String programError, int exitCode)
	{
		return new RunResult(programOutput, programError, exitCode, true);
	}

	public static RunResult failure(String programError)
	{
		return new RunResult("", programError, -1, false);
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

	public boolean isSuccess()
	{
		return success;
	}
}
