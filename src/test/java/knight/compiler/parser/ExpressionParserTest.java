package knight.compiler.parser;

import knight.compiler.ast.expressions.*;
import knight.compiler.lexer.Tokens;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.when;

public class ExpressionParserTest extends ParserTest
{
	@Test
	public void parseExpression_IntegerLiteral() throws Exception
	{
		this.parser.token = this.createToken("123", Tokens.INTEGER);
		when(mockLexer.nextToken()).thenReturn(createToken(";", Tokens.SEMICOLON)).thenReturn(null);

		ASTExpression expr = parser.parseExpression();
		assertInstanceOf(ASTIntLiteral.class, expr);
	}

	@Test
	public void parseExpression_StringLiteral() throws Exception
	{
		this.parser.token = this.createToken("\"abc\"", Tokens.STRING);
		when(mockLexer.nextToken()).thenReturn(createToken(";", Tokens.SEMICOLON));

		ASTExpression expr = parser.parseExpression();
		assertInstanceOf(ASTStringLiteral.class, expr);
		assertEquals("\"abc\"", ((ASTStringLiteral) expr).getValue());
	}

	@Test
	public void parseExpression_BooleanLiteral() throws Exception
	{
		this.parser.token = this.createToken("true", Tokens.TRUE);
		when(mockLexer.nextToken()).thenReturn(createToken(";", Tokens.SEMICOLON));
		assertInstanceOf(ASTTrue.class, parser.parseExpression());

		this.parser.token = this.createToken("false", Tokens.FALSE);
		when(mockLexer.nextToken()).thenReturn(createToken(";", Tokens.SEMICOLON));
		assertInstanceOf(ASTFalse.class, parser.parseExpression());
	}

	@Test
	public void parseExpression_Identifier() throws Exception
	{
		this.parser.token = this.createToken("age", Tokens.IDENTIFIER);
		when(mockLexer.nextToken()).thenReturn(createToken(";", Tokens.SEMICOLON));
		assertInstanceOf(ASTIdentifierExpr.class, parser.parseExpression());
	}

	@Test
	public void parseExpression_FunctionCall() throws Exception
	{
		this.parser.token = this.createToken("functionName", Tokens.IDENTIFIER);
		when(mockLexer.nextToken()).thenReturn(createToken("(", Tokens.LEFTPAREN))
				.thenReturn(createToken(")", Tokens.RIGHTPAREN));

		ASTCallFunctionExpr astCallFunctionExpr = assertInstanceOf(ASTCallFunctionExpr.class, parser.parseExpression());
		assertEquals("functionName", astCallFunctionExpr.getFunctionName().toString());
	}

	@Test
	public void parseExpression_Lambda() throws Exception
	{
		this.parser.token = this.createToken("fn", Tokens.FUNCTION);
		when(mockLexer.nextToken()).thenReturn(createToken("(", Tokens.LEFTPAREN))
				.thenReturn(createToken(")", Tokens.RIGHTPAREN)).thenReturn(createToken(":", Tokens.COLON))
				.thenReturn(createToken("int", Tokens.INTEGER)).thenReturn(createToken("{", Tokens.LEFTBRACE))
				.thenReturn(createToken("}", Tokens.RIGHTBRACE));

		assertInstanceOf(ASTLambda.class, parser.parseExpression());
	}

	@Test
	public void parseExpression_Array() throws Exception
	{
		this.parser.token = this.createToken("{", Tokens.LEFTBRACE);
		when(mockLexer.nextToken()).thenReturn(createToken("1", Tokens.INTEGER))
				.thenReturn(createToken(",", Tokens.COMMA)).thenReturn(createToken("2", Tokens.INTEGER))
				.thenReturn(createToken("}", Tokens.RIGHTBRACE)).thenReturn(createToken(";", Tokens.SEMICOLON));

		assertInstanceOf(ASTArrayLiteral.class, parser.parseExpression());
	}

	@Test
	public void parseExpression_NewIntArray() throws Exception
	{
		this.parser.token = this.createToken("new", Tokens.NEW);
		when(mockLexer.nextToken()).thenReturn(createToken("int", Tokens.INTEGER))
				.thenReturn(createToken("[", Tokens.LEFTBRACKET)).thenReturn(createToken("5", Tokens.INTEGER))
				.thenReturn(createToken("]", Tokens.RIGHTBRACKET));

		ASTNewArray astNewArray = assertInstanceOf(ASTNewArray.class, parser.parseExpression());
		ASTIntLiteral astIntLiteral = assertInstanceOf(ASTIntLiteral.class, astNewArray.getArrayLength());
		assertEquals(5, astIntLiteral.getValue());
	}

	@Test
	public void parseExpression_NewStringArray() throws Exception
	{
		this.parser.token = this.createToken("new", Tokens.NEW);
		when(mockLexer.nextToken()).thenReturn(createToken("string", Tokens.INTEGER))
				.thenReturn(createToken("[", Tokens.LEFTBRACKET)).thenReturn(createToken("12", Tokens.INTEGER))
				.thenReturn(createToken("]", Tokens.RIGHTBRACKET));

		ASTNewArray astNewArray = assertInstanceOf(ASTNewArray.class, parser.parseExpression());
		ASTIntLiteral astIntLiteral = assertInstanceOf(ASTIntLiteral.class, astNewArray.getArrayLength());
		assertEquals(12, astIntLiteral.getValue());
	}

	@Test
	public void parseExpression_NewIdentifier() throws Exception
	{
		this.parser.token = this.createToken("new", Tokens.NEW);
		when(mockLexer.nextToken()).thenReturn(createToken("ClassName", Tokens.IDENTIFIER))
				.thenReturn(createToken("(", Tokens.LEFTPAREN)).thenReturn(createToken(")", Tokens.RIGHTPAREN));

		ASTNewInstance astNewInstance = assertInstanceOf(ASTNewInstance.class, parser.parseExpression());
		ASTIdentifierExpr astIdentifierExpr = assertInstanceOf(ASTIdentifierExpr.class, astNewInstance.getClassName());
		assertEquals("ClassName", astIdentifierExpr.getId());
	}

	@Test
	public void parseExpression_And() throws Exception
	{
		this.parser.token = this.createToken("true", Tokens.TRUE);
		when(mockLexer.nextToken()).thenReturn(createToken("and", Tokens.AND))
				.thenReturn(createToken("false", Tokens.FALSE)).thenReturn(createToken(";", Tokens.SEMICOLON));

		assertInstanceOf(ASTAnd.class, parser.parseExpression());
	}

	@Test
	public void parseExpression_Or() throws Exception
	{
		this.parser.token = this.createToken("true", Tokens.TRUE);
		when(mockLexer.nextToken()).thenReturn(createToken("or", Tokens.OR))
				.thenReturn(createToken("false", Tokens.FALSE)).thenReturn(createToken(";", Tokens.SEMICOLON));

		assertInstanceOf(ASTOr.class, parser.parseExpression());
	}

	@Test
	public void parseExpression_Equals() throws Exception
	{
		this.parser.token = this.createToken("true", Tokens.TRUE);
		when(mockLexer.nextToken()).thenReturn(createToken("==", Tokens.EQUALS))
				.thenReturn(createToken("false", Tokens.FALSE)).thenReturn(createToken(";", Tokens.SEMICOLON));

		assertInstanceOf(ASTEquals.class, parser.parseExpression());
	}

	@Test
	public void parseExpression_NotEquals() throws Exception
	{
		this.parser.token = this.createToken("true", Tokens.TRUE);
		when(mockLexer.nextToken()).thenReturn(createToken("!=", Tokens.NOTEQUALS))
				.thenReturn(createToken("false", Tokens.FALSE)).thenReturn(createToken(";", Tokens.SEMICOLON));

		assertInstanceOf(ASTNotEquals.class, parser.parseExpression());
	}

	@Test
	public void parseExpression_LessThan() throws Exception
	{
		this.parser.token = this.createToken("1", Tokens.INTEGER);
		when(mockLexer.nextToken()).thenReturn(createToken("<", Tokens.LESSTHAN))
				.thenReturn(createToken("2", Tokens.INTEGER)).thenReturn(createToken(";", Tokens.SEMICOLON));

		assertInstanceOf(ASTLessThan.class, parser.parseExpression());
	}

	@Test
	public void parseExpression_LessThanOrEqual() throws Exception
	{
		this.parser.token = this.createToken("2", Tokens.INTEGER);
		when(mockLexer.nextToken()).thenReturn(createToken("<=", Tokens.LESSTHANOREQUAL))
				.thenReturn(createToken("2", Tokens.INTEGER)).thenReturn(createToken(";", Tokens.SEMICOLON));

		assertInstanceOf(ASTLessThanOrEqual.class, parser.parseExpression());
	}

	@Test
	public void parseExpression_GreaterThan() throws Exception
	{
		this.parser.token = this.createToken("2", Tokens.INTEGER);
		when(mockLexer.nextToken()).thenReturn(createToken(">", Tokens.GREATERTHAN))
				.thenReturn(createToken("2", Tokens.INTEGER)).thenReturn(createToken(";", Tokens.SEMICOLON));

		assertInstanceOf(ASTGreaterThan.class, parser.parseExpression());
	}

	@Test
	public void parseExpression_GreaterThanOrEqual() throws Exception
	{
		this.parser.token = this.createToken("2", Tokens.INTEGER);
		when(mockLexer.nextToken()).thenReturn(createToken(">=", Tokens.GREATERTHANOREQUAL))
				.thenReturn(createToken("2", Tokens.INTEGER)).thenReturn(createToken(";", Tokens.SEMICOLON));

		assertInstanceOf(ASTGreaterThanOrEqual.class, parser.parseExpression());
	}

	@Test
	public void parseExpression_Plus() throws Exception
	{
		this.parser.token = createToken("1", Tokens.INTEGER);
		when(mockLexer.nextToken()).thenReturn(createToken("+", Tokens.PLUS))
				.thenReturn(createToken("2", Tokens.INTEGER)).thenReturn(createToken(";", Tokens.SEMICOLON));

		assertInstanceOf(ASTPlus.class, parser.parseExpression());
	}

	@Test
	public void parseExpression_Minus() throws Exception
	{
		this.parser.token = createToken("1", Tokens.INTEGER);
		when(mockLexer.nextToken()).thenReturn(createToken("-", Tokens.MINUS))
				.thenReturn(createToken("2", Tokens.INTEGER)).thenReturn(createToken(";", Tokens.SEMICOLON));

		assertInstanceOf(ASTMinus.class, parser.parseExpression());
	}

	@Test
	public void parseExpression_Times() throws Exception
	{
		this.parser.token = createToken("1", Tokens.INTEGER);
		when(mockLexer.nextToken()).thenReturn(createToken("*", Tokens.TIMES))
				.thenReturn(createToken("2", Tokens.INTEGER)).thenReturn(createToken(";", Tokens.SEMICOLON));

		assertInstanceOf(ASTTimes.class, parser.parseExpression());
	}

	@Test
	public void parseExpression_Division() throws Exception
	{
		this.parser.token = createToken("1", Tokens.INTEGER);
		when(mockLexer.nextToken()).thenReturn(createToken("/", Tokens.DIV))
				.thenReturn(createToken("2", Tokens.INTEGER)).thenReturn(createToken(";", Tokens.SEMICOLON));

		assertInstanceOf(ASTDivision.class, parser.parseExpression());
	}

	@Test
	public void parseExpression_Modulus() throws Exception
	{
		this.parser.token = createToken("1", Tokens.INTEGER);
		when(mockLexer.nextToken()).thenReturn(createToken("%", Tokens.MODULUS))
				.thenReturn(createToken("2", Tokens.INTEGER)).thenReturn(createToken(";", Tokens.SEMICOLON));

		assertInstanceOf(ASTModulus.class, parser.parseExpression());
	}

	@Test
	public void parseExpression_ArrayIndexExpr() throws Exception
	{
		this.parser.token = createToken("someArray", Tokens.IDENTIFIER);
		when(mockLexer.nextToken()).thenReturn(createToken("[", Tokens.LEFTBRACKET))
				.thenReturn(createToken("1", Tokens.INTEGER)).thenReturn(createToken("]", Tokens.RIGHTBRACKET));

		assertInstanceOf(ASTArrayIndexExpr.class, parser.parseExpression());
	}
}
