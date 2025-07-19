package knight.compiler.semantics;

import knight.compiler.ast.expressions.ASTIdentifierExpr;
import knight.compiler.ast.types.ASTIntType;
import knight.compiler.lexer.Token;
import knight.compiler.semantics.diagnostics.DiagnosticReporter;
import knight.compiler.semantics.model.SymbolProgram;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NameAnalyserTest
{
	private SymbolProgram symbolProgram;
	private NameAnalyser nameAnalyser;

	@BeforeEach
	public void setUp()
	{
		DiagnosticReporter.clear();
		symbolProgram = new SymbolProgram();
		nameAnalyser = new NameAnalyser(symbolProgram);
	}

	@Test
	public void visitASTIdentifierExpr_withDefinedVariable()
	{
		symbolProgram.addVariable("x", new ASTIntType(mock(Token.class)));

		ASTIdentifierExpr expr = mock(ASTIdentifierExpr.class);
		when(expr.getId()).thenReturn("x");
		when(expr.getToken()).thenReturn(mock(Token.class));

		assertNull(nameAnalyser.visit(expr));
		assertFalse(DiagnosticReporter.hasErrors());
	}

	@Test
	public void visitASTIdentifierExpr_withUndefinedVariable()
	{
		ASTIdentifierExpr expr = mock(ASTIdentifierExpr.class);
		when(expr.getId()).thenReturn("y");
		when(expr.getToken()).thenReturn(mock(Token.class));

		assertNull(nameAnalyser.visit(expr));
		assertTrue(DiagnosticReporter.hasErrors());
	}
}
