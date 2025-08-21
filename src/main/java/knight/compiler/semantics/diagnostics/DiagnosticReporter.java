package knight.compiler.semantics.diagnostics;

import knight.compiler.lexer.Token;
import knight.compiler.ast.AST;

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
		report(new Diagnostic(token.getRow(), token.getCol(), message, DiagnosticSeverity.ERROR, null));
	}

	public static void error(Token token, String message, String sourceFile)
	{
		report(new Diagnostic(token.getRow(), token.getCol(), message, DiagnosticSeverity.ERROR, sourceFile));
	}

	public static void error(AST ast, String message)
	{
		String sourceFile = ast.getSourceFile();
		report(new Diagnostic(ast.getToken().getRow(), ast.getToken().getCol(), message, DiagnosticSeverity.ERROR,
				sourceFile));
	}

	public static void warning(Token token, String message)
	{
		if (warningsEnabled) {
			report(new Diagnostic(token.getRow(), token.getCol(), message, DiagnosticSeverity.WARNING, null));
		}
	}

	public static void warning(Token token, String message, String sourceFile)
	{
		if (warningsEnabled) {
			report(new Diagnostic(token.getRow(), token.getCol(), message, DiagnosticSeverity.WARNING, sourceFile));
		}
	}

	public static void warning(AST ast, String message)
	{
		if (warningsEnabled) {
			String sourceFile = ast.getSourceFile();
			report(new Diagnostic(ast.getToken().getRow(), ast.getToken().getCol(), message, DiagnosticSeverity.WARNING,
					sourceFile));
		}
	}

	public static void info(Token token, String message)
	{
		report(new Diagnostic(token.getRow(), token.getCol(), message, DiagnosticSeverity.INFO, null));
	}

	public static void info(Token token, String message, String sourceFile)
	{
		report(new Diagnostic(token.getRow(), token.getCol(), message, DiagnosticSeverity.INFO, sourceFile));
	}

	public static void info(AST ast, String message)
	{
		String sourceFile = ast.getSourceFile();
		report(new Diagnostic(ast.getToken().getRow(), ast.getToken().getCol(), message, DiagnosticSeverity.INFO,
				sourceFile));
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
			// First sort by source file
			if (a.getSourceFile() != null && b.getSourceFile() != null) {
				int fileCompare = a.getSourceFile().compareTo(b.getSourceFile());
				if (fileCompare != 0)
					return fileCompare;
			}

			// Then by line
			if (a.getLine() != b.getLine())
				return Integer.compare(a.getLine(), b.getLine());

			// Then by column
			if (a.getColumn() != b.getColumn())
				return Integer.compare(a.getColumn(), b.getColumn());

			// Finally by severity
			return a.getSeverity().compareTo(b.getSeverity());
		});
	}
}
