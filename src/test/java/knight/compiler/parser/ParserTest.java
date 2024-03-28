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
		parser.token = parser.lexer.nextToken();
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

		Exception exception = assertThrows(ParseException.class, () -> parser.parseClass());
		assertEquals("0:0 Token is null, cannot perform operation.", exception.getMessage());
	}

    @Test
	public void testParseClassWithFunctions() throws ParseException
	{
		CodeBuilderClass codeBuilderClass = new CodeBuilderClass()
			.addFunction(new CodeBuilderFunction().setId("MyFunction"));

		Parser parser = getParser(codeBuilderClass.toString());
		parser.token = parser.lexer.nextToken();
		ASTClass classDecl = parser.parseClass();

		assertNotNull(classDecl);
		assertEquals(1, classDecl.getFunctionListSize());
		
		ASTFunction function = classDecl.getFunctionDeclAt(0);
		assertEquals("MyFunction", function.getId().toString());
	}

	@Test
	public void testParseClassWithVariables() throws ParseException
	{
		CodeBuilderClass codeBuilderClass = new CodeBuilderClass()
			.addVariable(new CodeBuilderVariable().setId("MyVariable"));

		Parser parser = getParser(codeBuilderClass.toString());
		parser.token = parser.lexer.nextToken();
		ASTClass classDecl = parser.parseClass();

		assertNotNull(classDecl);
		assertEquals(1, classDecl.getVariableListSize());

		ASTVariable variable = classDecl.getVariableDeclAt(0);
		assertEquals("MyVariable", variable.getId().toString());
	}

	@Test
    public void testParseClassWithAll() throws ParseException
    {
    	CodeBuilderClass codeBuilderClass = new CodeBuilderClass()
    		.setId("MyClass")
    		.mockFunction(2)
    		.mockVariable(3);

    	Parser parser = getParser(codeBuilderClass.toString());
    	parser.token = parser.lexer.nextToken();
    	ASTClass classDecl = parser.parseClass();

    	assertNotNull(classDecl);
    	assertEquals("MyClass", classDecl.getId().toString());
    	assertEquals(2, classDecl.getFunctionListSize());
    	assertEquals(3, classDecl.getVariableListSize());
    }

	@Test
	public void testParseFunctionValid() throws ParseException
	{
		CodeBuilderFunction codeBuilderFunction = new CodeBuilderFunction()
			.setId("MyFunction");

		Parser parser = getParser(codeBuilderFunction.toString());
		parser.token = parser.lexer.nextToken();
		ASTFunction function = parser.parseFunction();

		assertNotNull(function);
		assertEquals("MyFunction", function.getId().toString());
		assertEquals(0, function.getArgumentListSize());
		assertEquals(0, function.getVariableListSize());
		assertEquals(0, function.getStatementListSize());
		assertEquals(0, function.getInlineASMListSize());
	}

	@Test
	public void testParseFunctionReturnValid() throws ParseException
	{
		CodeBuilderFunctionReturn codeBuilderFunctionReturn = new CodeBuilderFunctionReturn()
			.setReturnType(CodeBuilderType.INT)
			.setId("MyFunction");

		Parser parser = getParser(codeBuilderFunctionReturn.toString());
		parser.token = parser.lexer.nextToken();
		ASTFunction function = parser.parseFunction();

		assertNotNull(function);
		assertEquals("MyFunction", function.getId().toString());
    	assertTrue("Function is not instanceof FunctionReturn", function instanceof ASTFunctionReturn);
    	assertTrue("ReturnType is not instanceof IntType", function.getReturnType() instanceof ASTIntType);

    	ASTFunctionReturn functionReturn = (ASTFunctionReturn) function;
    	assertNotNull(functionReturn.getReturnExpr());
	}

	@Test
	public void testParseFunctionWithArguments() throws ParseException
	{
		CodeBuilderFunction codeBuilderFunction = new CodeBuilderFunction()
			.addArgument(new CodeBuilderArgument().setId("MyArgument"));

		Parser parser = getParser(codeBuilderFunction.toString());
		parser.token = parser.lexer.nextToken();
		ASTFunction function = parser.parseFunction();

		assertNotNull(function);
		assertEquals(1, function.getArgumentListSize());

		ASTArgument argument = function.getArgumentDeclAt(0);
		assertEquals("MyArgument", argument.getId().toString());
	}

	@Test
	public void testParseFunctionWithVariables() throws ParseException
	{
		CodeBuilderFunction codeBuilderFunction = new CodeBuilderFunction()
			.addVariable(new CodeBuilderVariable().setId("MyVariable"));

		Parser parser = getParser(codeBuilderFunction.toString());
		parser.token = parser.lexer.nextToken();
		ASTFunction function = parser.parseFunction();

		assertNotNull(function);
		assertEquals(1, function.getVariableListSize());

		ASTVariable variable = function.getVariableDeclAt(0);
		assertEquals("MyVariable", variable.getId().toString());
	}

	@Test
	public void testParseFunctionWithStatements() throws ParseException
	{
		CodeBuilderFunction codeBuilderFunction = new CodeBuilderFunction()
			.setId("MyFunction")
			.addStatements(
				new CodeBuilderAssign()
					.setId("MyAssign")
					.setExpr(new CodeBuilderIntLiteral()),
				new CodeBuilderWhile()
					.setExpr(new CodeBuilderIntLiteral().setValue(1))
			);

		Parser parser = getParser(codeBuilderFunction.toString());
		parser.token = parser.lexer.nextToken();
		ASTFunction function = parser.parseFunction();

		assertNotNull(function);
		assertEquals(2, function.getStatementListSize());

		ASTStatement assignStatement = function.getStatementDeclAt(0);
		assertTrue("Statement is not instanceof ASTStatement", assignStatement instanceof ASTStatement);
		ASTAssign assign = (ASTAssign) assignStatement;
		assertEquals("MyAssign", assign.getId().toString());

		ASTStatement whileStatement = function.getStatementDeclAt(1);
		assertTrue("Statement is not instanceof ASTStatement", whileStatement instanceof ASTStatement);
		ASTWhile whileStat = (ASTWhile) whileStatement;
		assertTrue("Expression in while is not instanceof ASTIntLiteral", whileStat.getExpr() instanceof ASTIntLiteral);
	}

	// @Test
	// public void testParseFunctionWithAll() throws ParseException
	// {
	// 	CodeBuilderFunction codeBuilderFunction = new CodeBuilderFunction("MyFunction");
	// 	codeBuilderFunction.mockArgument(2);
	// 	codeBuilderFunction.mockVariable(3);
	// 	codeBuilderFunction.mockStatement(1);

	// 	Parser parser = getParser(codeBuilderFunction.toString());
	// 	parser.token = parser.lexer.nextToken();
	// 	ASTFunction function = parser.parseFunction();

	// 	assertNotNull(function);
	// 	assertEquals("MyFunction", function.getId().toString());
	// 	assertEquals(2, function.getArgumentListSize());
	// 	assertEquals(3, function.getVariableListSize());
	// 	assertEquals(1, function.getStatementListSize());
	// }

    // @Test
    // public void testParseVariableValid() throws ParseException
    // {
    // 	CodeBuilderVariable codeBuilderVariable = new CodeBuilderVariable("MyVariable");

    // 	Parser parser = this.getParser(codeBuilderVariable.toString());
    // 	parser.token = parser.lexer.nextToken();
    // 	ASTVariable variable = parser.parseVariable();

    // 	assertNotNull(variable);
    // 	assertTrue("Variable is not instanceof ASTVariable", variable instanceof ASTVariable);
    // 	assertEquals("MyVariable", variable.getId().toString());
    // }

    // @Test
    // public void testParseVariableInitValid() throws ParseException
    // {
    // 	CodeBuilderVariableInit codeBuilderVariableInit = new CodeBuilderVariableInit("MyVariable");

    // 	Parser parser = this.getParser(codeBuilderVariableInit.toString());
    // 	parser.token = parser.lexer.nextToken();
    // 	ASTVariable variable = parser.parseVariable();

    // 	assertNotNull(variable);
    // 	assertTrue("Variable is not instanceof ASTVariableInit", variable instanceof ASTVariableInit);
    // 	assertEquals("MyVariable", variable.getId().toString());

    // 	ASTVariableInit variableInitDecl = (ASTVariableInit) variable;
    // 	assertNotNull(variableInitDecl.getExpr());
    // }

    // @Test
	// public void testParseExpression() throws ParseException
	// {
	//     assertParseExpression("5 * 6;", ASTTimes.class, 5, 6);
	//     assertParseExpression("34 + 23;", ASTPlus.class, 34, 23);
	//     assertParseExpression("10 / 2;", ASTDivision.class, 10, 2);
	//     assertParseExpression("100 - 23;", ASTMinus.class, 100, 23);
	// }

	// private void assertParseExpression(String input, Class<? extends ASTExpression> expectedClass, int lhsValue, int rhsValue) throws ParseException
    // {
	//     Parser parser = this.getParser(input);
	//     Lexer lexer = parser.lexer;
	//     parser.token = lexer.nextToken();
	//     ASTExpression expression = parser.parseExpression();

	//     assertNotNull(expression);
	//     assertTrue("Expression is not an instance of " + expectedClass.getSimpleName(), expectedClass.isInstance(expression));

	//     try {
	//         Method getLhsMethod = expectedClass.getMethod("getLhs");
	//         Method getRhsMethod = expectedClass.getMethod("getRhs");

	//         ASTIntLiteral lhs = (ASTIntLiteral) getLhsMethod.invoke(expression);
	//         ASTIntLiteral rhs = (ASTIntLiteral) getRhsMethod.invoke(expression);

	//         assertEquals(lhsValue, lhs.getValue());
	//         assertEquals(rhsValue, rhs.getValue());
	//     } catch (Exception e) {
	//         e.getStackTrace();
	//     }
	// }

	// @Test
	// public void testParseCallFunction() throws ParseException
	// {
		
	// }

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