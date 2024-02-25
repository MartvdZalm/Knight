package knight.compiler.parser;

import java.io.*;
import static org.junit.Assert.*;
import org.junit.Test;
import java.util.*;
import java.lang.reflect.Method;

import knight.compiler.ast.declarations.*;
import knight.compiler.ast.expressions.*;
import knight.compiler.ast.expressions.operations.*;
import knight.compiler.ast.statements.*;
import knight.compiler.ast.statements.conditionals.*;
import knight.compiler.ast.types.*;
import knight.compiler.ast.*;

import knight.compiler.parser.*;
import knight.compiler.lexer.*;
import knight.builder.code.*;

public class ParserTest
{
	@Test
	public void testParseIncludeValid() throws ParseException
	{
        Parser parser = getParser("include example");
        Lexer lexer = parser.lexer;
        parser.token = lexer.nextToken();        
        ASTInclude include = parser.parseInclude();
        
        assertNotNull(include);
        assertEquals("example", include.getId().toString());
	}

	@Test
	public void testParseIncludeInvalid() throws ParseException
	{
		Parser parser = getParser("include");
		Lexer lexer = parser.lexer;
		parser.token = lexer.nextToken();

		Exception exception = assertThrows(ParseException.class, () ->
			parser.parseInclude());
        assertEquals("0:0 Token is null, cannot perform operation.", exception.getMessage());
	}

	@Test
	public void testParseClassValid() throws ParseException
	{
		Parser parser = getParser("class HelloWorld {}");
		Lexer lexer = parser.lexer;
		parser.token = lexer.nextToken();
		ASTClass classDecl = parser.parseClass();

		assertNotNull(classDecl);
		assertEquals("HelloWorld", classDecl.getId().toString());
		assertEquals(0, classDecl.getFunctionListSize());
		assertEquals(0, classDecl.getVariableListSize());
	}

	@Test
	public void testParseClassInvalid() throws ParseException
	{
		Parser parser = getParser("class");
		Lexer lexer = parser.lexer;
		parser.token = lexer.nextToken();

		Exception exception = assertThrows(ParseException.class, () -> 
			parser.parseClass());
		assertEquals("0:0 Token is null, cannot perform operation.", exception.getMessage());
	}

 	@Test
    public void testParseClassWithFunctionsAndVariables() throws ParseException
    {
    	CodeBuilderClass codeBuilderClass = new CodeBuilderClass("MyClass");

    	Map<String, Integer> data = new HashMap<>();
		data.put("function", 2);
		data.put("variable", 3);
    	codeBuilderClass.mock(data);

    	Parser parser = getParser(codeBuilderClass.toString());
    	Lexer lexer = parser.lexer;
    	parser.token = lexer.nextToken();
    	ASTClass classDecl = parser.parseClass();

    	assertNotNull(classDecl);
    	assertEquals("MyClass", classDecl.getId().toString());
    	assertEquals(2, classDecl.getFunctionListSize());
    	assertEquals(3, classDecl.getVariableListSize());
    }

    @Test
    public void testParseFunctionReturnValid() throws ParseException
    {
    	CodeBuilderFunction codeBuilderFunction = new CodeBuilderFunction("MyFunction");
    	codeBuilderFunction.setReturnType(CodeBuilderType.INT);
    	codeBuilderFunction.mock();

    	Parser parser = getParser(codeBuilderFunction.toString());
    	Lexer lexer = parser.lexer;
    	parser.token = lexer.nextToken();
    	ASTFunction functionDecl = parser.parseFunction();

    	assertNotNull(functionDecl);
    	assertTrue("FunctionDecl is not instanceof FunctionReturn", functionDecl instanceof ASTFunctionReturn);
    	assertEquals("MyFunction", functionDecl.getId().toString());
    	assertTrue("ReturnType is not instanceof IntType", functionDecl.getReturnType() instanceof ASTIntType);
    	assertEquals(0, functionDecl.getVariableListSize());
    	assertEquals(0, functionDecl.getStatementListSize());

    	ASTFunctionReturn functionReturnDecl = (ASTFunctionReturn) functionDecl;
    	assertNotNull(functionReturnDecl.getReturnExpr());
    }

    @Test
    public void testParseFunctionValid() throws ParseException
    {
    	CodeBuilderFunction codeBuilderFunction = new CodeBuilderFunction("MyFunction");

		Map<String, Integer> data = new HashMap<>();
		data.put("statement", 2);
    	data.put("variable", 3);
    	codeBuilderFunction.mock(data);

    	Parser parser = this.getParser(codeBuilderFunction.toString());
    	Lexer lexer = parser.lexer;
    	parser.token = lexer.nextToken();
    	ASTFunction functionDecl = parser.parseFunction();

    	assertNotNull(functionDecl);
    	assertTrue("FunctionDecl is not instanceof Function", functionDecl instanceof ASTFunction);
    	assertEquals("MyFunction", functionDecl.getId().toString());
    	assertEquals(2, functionDecl.getStatementListSize());
    	assertEquals(3, functionDecl.getVariableListSize());
    }

    @Test
    public void testParseVariableValid() throws ParseException
    {
    	CodeBuilderVariable codeBuilderVariable = new CodeBuilderVariable("myVariable");
    	codeBuilderVariable.mock();

    	Parser parser = this.getParser(codeBuilderVariable.toString());
    	Lexer lexer = parser.lexer;
    	parser.token = lexer.nextToken();
    	ASTVariable variableDecl = parser.parseVariable();

    	assertNotNull(variableDecl);
    	assertTrue("Variable is not instanceof Variable", variableDecl instanceof ASTVariable);
    	assertEquals("myVariable", variableDecl.getId().toString());
    }

    @Test
    public void testParseVariableInitValid() throws ParseException
    {
    	CodeBuilderVariableInit codeBuilderVariableInit = new CodeBuilderVariableInit("myVariable");
    	codeBuilderVariableInit.mock();

    	Parser parser = this.getParser(codeBuilderVariableInit.toString());
    	Lexer lexer = parser.lexer;
    	parser.token = lexer.nextToken();
    	ASTVariable variableDecl = parser.parseVariable();

    	assertNotNull(variableDecl);
    	assertTrue("VariableDecl is not instanceof VariableInit", variableDecl instanceof ASTVariableInit);
    	assertEquals("myVariable", variableDecl.getId().toString());

    	ASTVariableInit variableInitDecl = (ASTVariableInit) variableDecl;
    	assertNotNull(variableInitDecl.getExpr());
    }

    @Test
    public void testParseReturnExprValid() throws ParseException
    {
    	CodeBuilderReturnStatement codeBuilderReturnStatement = new CodeBuilderReturnStatement();
    	codeBuilderReturnStatement.setExpr("10 * 10");
    	codeBuilderReturnStatement.mock();

    	Parser parser = this.getParser(codeBuilderReturnStatement.toString());
    	Lexer lexer = parser.lexer;
    	parser.token = lexer.nextToken();
    	ASTExpression expression = parser.parseReturnExpr();

    	assertNotNull(expression);
    	assertTrue("Expression is not instanceof Times", expression instanceof ASTTimes);
    }

    @Test
	public void testParseExpression() throws ParseException
	{
	    assertParseExpression("5 * 6;", ASTTimes.class, 5, 6);
	    assertParseExpression("34 + 23;", ASTPlus.class, 34, 23);
	    assertParseExpression("10 / 2;", ASTDivision.class, 10, 2);
	    assertParseExpression("100 - 23;", ASTMinus.class, 100, 23);
	}

	private void assertParseExpression(String input, Class<? extends ASTExpression> expectedClass, int lhsValue, int rhsValue) throws ParseException
    {
	    Parser parser = this.getParser(input);
	    Lexer lexer = parser.lexer;
	    parser.token = lexer.nextToken();
	    ASTExpression expression = parser.parseExpression();

	    assertNotNull(expression);
	    assertTrue("Expression is not an instance of " + expectedClass.getSimpleName(), expectedClass.isInstance(expression));

	    try {
	        Method getLhsMethod = expectedClass.getMethod("getLhs");
	        Method getRhsMethod = expectedClass.getMethod("getRhs");

	        ASTIntLiteral lhs = (ASTIntLiteral) getLhsMethod.invoke(expression);
	        ASTIntLiteral rhs = (ASTIntLiteral) getRhsMethod.invoke(expression);

	        assertEquals(lhsValue, lhs.getValue());
	        assertEquals(rhsValue, rhs.getValue());
	    } catch (Exception e) {
	        e.getStackTrace();
	    }
	}

    private Parser getParser(String input)
	{
	    if (input == null || input.isEmpty()) {
	        throw new IllegalArgumentException("Input must not be empty");
	    }

		StringReader stringReader = new StringReader(input);
		BufferedReader bufferedReader = new BufferedReader(stringReader);
		Parser parser = new Parser(bufferedReader);

		return parser;
	}
}