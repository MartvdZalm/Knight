package knight.compiler.semantics.diagnostics;

public class Diagnostic
{
	private final int line;
	private final int column;
	private final String message;
	private final DiagnosticSeverity severity;

	public Diagnostic(int line, int column, String message, DiagnosticSeverity severity)
	{
		this.line = line;
		this.column = column;
		this.message = message;
		this.severity = severity;
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

	@Override
	public String toString()
	{
		return String.format("%s:%d:%d: %s: %s", severity.toString().toLowerCase(), line, column, severity, message);
	}
}
