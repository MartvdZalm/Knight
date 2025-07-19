package knight.compiler.semantics.diagnostics;

import knight.compiler.lexer.Token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class DiagnosticReporter
{
	private static final List<Diagnostic> diagnostics = new ArrayList<>();
	private static boolean warningsEnabled = true;

	private DiagnosticReporter()
	{
	}

	public static void error(Token token, String message)
	{
		report(new Diagnostic(token.getRow(), token.getCol(), message, DiagnosticSeverity.ERROR));
	}

	public static void warning(Token token, String message)
	{
		if (warningsEnabled) {
			report(new Diagnostic(token.getRow(), token.getCol(), message, DiagnosticSeverity.WARNING));
		}
	}

	public static void info(Token token, String message)
	{
		report(new Diagnostic(token.getRow(), token.getCol(), message, DiagnosticSeverity.INFO));
	}

	private static synchronized void report(Diagnostic diagnostic)
	{
		diagnostics.add(diagnostic);
	}

	public static synchronized void setWarningsEnabled(boolean enabled)
	{
		warningsEnabled = enabled;
	}

	public static synchronized List<Diagnostic> getDiagnostics()
	{
		return Collections.unmodifiableList(new ArrayList<>(diagnostics));
	}

	public static synchronized boolean hasErrors()
	{
		return diagnostics.stream().anyMatch(d -> d.getSeverity() == DiagnosticSeverity.ERROR);
	}

	public static synchronized void clear()
	{
		diagnostics.clear();
	}

	public static synchronized void sort()
	{
		diagnostics.sort((a, b) -> {
			if (a.getLine() != b.getLine())
				return Integer.compare(a.getLine(), b.getLine());
			if (a.getColumn() != b.getColumn())
				return Integer.compare(a.getColumn(), b.getColumn());
			return a.getSeverity().compareTo(b.getSeverity());
		});
	}
}
