package knight.compiler.semantics.diagnostics;

public class Diagnostic
{
	private final int line;
	private final int column;
	private final String message;
	private final DiagnosticSeverity severity;
	private final String sourceFile;

	public Diagnostic(int line, int column, String message, DiagnosticSeverity severity)
	{
		this.line = line;
		this.column = column;
		this.message = message;
		this.severity = severity;
		this.sourceFile = null;
	}

	public Diagnostic(int line, int column, String message, DiagnosticSeverity severity, String sourceFile)
	{
		this.line = line;
		this.column = column;
		this.message = message;
		this.severity = severity;
		this.sourceFile = sourceFile;
	}

	public int getLine()
	{
		return this.line;
	}

	public int getColumn()
	{
		return this.column;
	}

	public String getMessage()
	{
		return this.message;
	}

	public DiagnosticSeverity getSeverity()
	{
		return this.severity;
	}

	public String getSourceFile()
	{
		return this.sourceFile;
	}

	@Override
	public String toString()
	{
		if (sourceFile != null) {
			return String.format("%s:%d:%d: %s: %s", sourceFile, line, column, severity, message);
		} else {
			return String.format("%d:%d: %s: %s", line, column, severity, message);
		}
	}
}
