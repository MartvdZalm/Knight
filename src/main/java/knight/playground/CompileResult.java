package knight.playground;

import java.util.Collections;
import java.util.List;

public class CompileResult
{
	private final boolean success;
	private final List<String> errors;
	private final List<String> warnings;
	private final String generatedCpp;

	public CompileResult(boolean success, List<String> errors, List<String> warnings, String generatedCpp)
	{
		this.success = success;
		this.errors = errors;
		this.warnings = warnings;
		this.generatedCpp = generatedCpp != null ? generatedCpp : "";
	}

	public static CompileResult success(String generatedCpp, List<String> warnings)
	{
		return new CompileResult(true, List.of(), warnings, generatedCpp);
	}

	public static CompileResult failure(List<String> errors, List<String> warnings, String generatedCpp)
	{
		return new CompileResult(false, errors, warnings, generatedCpp);
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
}
