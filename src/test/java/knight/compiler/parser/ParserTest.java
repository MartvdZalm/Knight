/*
 * MIT License
 * 
 * Copyright (c) 2023, Mart van der Zalm
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package knight.compiler.parser;

import java.io.*;
import static org.junit.Assert.*;
import org.junit.Test;
import java.util.*;
import java.lang.reflect.Method;
import java.util.function.Supplier;

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

import knight.helper.TestUtils;

/*
 * File: ParserTest.java
 * @author: Mart van der Zalm
 * Date: 2024-01-07
 * Description:
 */
public class ParserTest extends TestUtils
{
	@Test
	public void testParseWithAll() throws ParseException
	{
		CodeBuilderProgram codeBuilderProgram = new CodeBuilderProgram()
			.addClasses(
				new CodeBuilderClass()
			)
			.addFunctions(
				new CodeBuilderFunction(),
				new CodeBuilderFunctionReturn()
			)
			.addVariables(
				new CodeBuilderVariable(),
				new CodeBuilderVariableInit()
			)
			.addInlineASM(
				new CodeBuilderInlineASM()
			);

		System.out.println(codeBuilderProgram.toString());

		AST ast = parse(codeBuilderProgram.toString()).parse();
		ASTProgram program  = castExpectClass(ASTProgram.class, ast);

		assertEquals(1, program.getClassListSize());
		expectsClass(ASTClass.class, program.getClassDeclAt(0).getClass());

		assertEquals(2, program.getFunctionListSize());
		expectsClass(ASTFunction.class, program.getFunctionDeclAt(0).getClass());
		expectsClass(ASTFunctionReturn.class, program.getFunctionDeclAt(1).getClass());

		assertEquals(2, program.getVariableListSize());
		expectsClass(ASTVariable.class, program.getVariableDeclAt(0).getClass());
		expectsClass(ASTVariableInit.class, program.getVariableDeclAt(1).getClass());

		assertEquals(1, program.getInlineASMListSize());
		expectsClass(ASTInlineASM.class, program.getInlineASMDeclAt(0).getClass());
	}

	@Test
	public void testParseClassValid() throws ParseException
	{
		CodeBuilderClass codeBuilderClass = new CodeBuilderClass()
			.setId("MyClass");

		ASTClass classDecl = parse(codeBuilderClass.toString()).parseClass();

		assertEquals("MyClass", classDecl.getId().toString());
		assertEquals(0, classDecl.getFunctionListSize());
		assertEquals(0, classDecl.getVariableListSize());
	}

    @Test
	public void testParseClassWithFunctions() throws ParseException
	{
		CodeBuilderClass codeBuilderClass = new CodeBuilderClass()
			.addFunctions(
				new CodeBuilderFunction().setId("MyFunction"),
				new CodeBuilderFunctionReturn().setReturnType(new CodeBuilderIntType())
			);

		ASTClass classDecl = parse(codeBuilderClass.toString()).parseClass();

		assertEquals(2, classDecl.getFunctionListSize());
		ASTFunction function = castExpectClass(ASTFunction.class, classDecl.getFunctionDeclAt(0));
		assertEquals("MyFunction", function.getId().toString());
		ASTFunctionReturn functionReturn = castExpectClass(ASTFunctionReturn.class, classDecl.getFunctionDeclAt(1));
		expectsClass(ASTIntType.class, functionReturn.getReturnType().getClass());
	}

	@Test
	public void testParseClassWithVariables() throws ParseException
	{
		CodeBuilderClass codeBuilderClass = new CodeBuilderClass()
			.addVariables(
				new CodeBuilderVariable().setId("MyVariable"),
				new CodeBuilderVariableInit().setType(new CodeBuilderIntType())
			);

		ASTClass classDecl = parse(codeBuilderClass.toString()).parseClass();

		assertEquals(2, classDecl.getVariableListSize());
		ASTVariable variable = castExpectClass(ASTVariable.class, classDecl.getVariableDeclAt(0));
		assertEquals("MyVariable", variable.getId().toString());
		ASTVariableInit variableInit = castExpectClass(ASTVariableInit.class, classDecl.getVariableDeclAt(1));
		expectsClass(ASTIntType.class, variableInit.getType().getClass());
	}

	@Test
    public void testParseClassWithAll() throws ParseException
    {
    	CodeBuilderClass codeBuilderClass = new CodeBuilderClass()
    		.setId("MyClass")
    		.addFunctions(
    			new CodeBuilderFunction(),
    			new CodeBuilderFunctionReturn()
    		)
    		.addVariables(
    			new CodeBuilderVariable(),
    			new CodeBuilderVariableInit()
    		);

    	ASTClass classDecl = parse(codeBuilderClass.toString()).parseClass();

    	assertEquals("MyClass", classDecl.getId().toString());

    	assertEquals(2, classDecl.getFunctionListSize());
    	expectsClass(ASTFunction.class, classDecl.getFunctionDeclAt(0).getClass());
    	expectsClass(ASTFunctionReturn.class, classDecl.getFunctionDeclAt(1).getClass());

    	assertEquals(2, classDecl.getVariableListSize());
    	expectsClass(ASTVariable.class, classDecl.getVariableDeclAt(0).getClass());
    	expectsClass(ASTVariableInit.class, classDecl.getVariableDeclAt(1).getClass());
    }

	@Test
	public void testParseFunctionValid() throws ParseException
	{
		CodeBuilderFunction codeBuilderFunction = new CodeBuilderFunction()
			.setId("MyFunction");

		ASTFunction function = parse(codeBuilderFunction.toString()).parseFunction();

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
			.setReturnType(new CodeBuilderIntType())
			.setId("MyFunction");

		ASTFunction function = parse(codeBuilderFunctionReturn.toString()).parseFunction();

		assertEquals("MyFunction", function.getId().toString());
		expectsClass(ASTIntType.class, function.getReturnType().getClass());
		ASTFunctionReturn functionReturn = castExpectClass(ASTFunctionReturn.class, function);
    	assertNotNull(functionReturn.getReturnExpr());
	}

	@Test
	public void testParseFunctionWithArguments() throws ParseException
	{
		CodeBuilderFunction codeBuilderFunction = new CodeBuilderFunction()
			.addArguments(
				new CodeBuilderArgument().setId("MyArgument")
			);

		ASTFunction function = parse(codeBuilderFunction.toString()).parseFunction();

		assertEquals(1, function.getArgumentListSize());
		ASTArgument argument = castExpectClass(ASTArgument.class, function.getArgumentDeclAt(0));
		assertEquals("MyArgument", argument.getId().toString());
	}

	@Test
	public void testParseFunctionWithVariables() throws ParseException
	{
		CodeBuilderFunction codeBuilderFunction = new CodeBuilderFunction()
			.addVariables(
				new CodeBuilderVariable().setId("MyVariable")
			);

		ASTFunction function = parse(codeBuilderFunction.toString()).parseFunction();

		assertEquals(1, function.getVariableListSize());
		ASTVariable variable = castExpectClass(ASTVariable.class, function.getVariableDeclAt(0));
		assertEquals("MyVariable", variable.getId().toString());
	}

	@Test
	public void testParseFunctionWithStatements() throws ParseException
	{
		CodeBuilderFunction codeBuilderFunction = new CodeBuilderFunction()
			.addStatements(
				new CodeBuilderAssign().setId("MyStatement")
			);

		ASTFunction function = parse(codeBuilderFunction.toString()).parseFunction();

		assertEquals(1, function.getStatementListSize());
		ASTAssign statement = castExpectClass(ASTAssign.class, function.getStatementDeclAt(0));
		assertEquals("MyStatement", statement.getId().toString());
	}

	@Test
	public void testParseFunctionWithAll() throws ParseException
	{
		CodeBuilderFunction codeBuilderFunction = new CodeBuilderFunction()
			.addArguments(
				new CodeBuilderArgument().setType(new CodeBuilderIntType()),
				new CodeBuilderArgument().setType(new CodeBuilderStringType()),
				new CodeBuilderArgument().setType(new CodeBuilderBooleanType()),
				new CodeBuilderArgument().setType(new CodeBuilderIdentifierType())
			)
			.addVariables(
				new CodeBuilderVariable().setType(new CodeBuilderIntType()),
				new CodeBuilderVariable().setType(new CodeBuilderStringType()),
				new CodeBuilderVariable().setType(new CodeBuilderIdentifierType()),
				new CodeBuilderVariableInit().setType(new CodeBuilderBooleanType()),
				new CodeBuilderVariableInit().setType(new CodeBuilderIdentifierType())
			)
			.addStatements(
				new CodeBuilderWhile(),
				new CodeBuilderForLoop(),
				new CodeBuilderIfThenElse(),
				new CodeBuilderAssign()
			)
			.addInlineASM(
				new CodeBuilderInlineASM()
			);

		ASTFunction function = parse(codeBuilderFunction.toString()).parseFunction();

		assertEquals(5, function.getVariableListSize());
		expectsClass(ASTIntType.class, function.getVariableDeclAt(0).getType().getClass());
		expectsClass(ASTStringType.class, function.getVariableDeclAt(1).getType().getClass());
		expectsClass(ASTIdentifierType.class, function.getVariableDeclAt(2).getType().getClass());
		expectsClass(ASTBooleanType.class, function.getVariableDeclAt(3).getType().getClass());
		expectsClass(ASTIdentifierType.class, function.getVariableDeclAt(4).getType().getClass());

		assertEquals(4, function.getStatementListSize());
		expectsClass(ASTWhile.class, function.getStatementDeclAt(0).getClass());
		expectsClass(ASTForLoop.class, function.getStatementDeclAt(1).getClass());
		expectsClass(ASTIfThenElse.class, function.getStatementDeclAt(2).getClass());
		expectsClass(ASTAssign.class, function.getStatementDeclAt(3).getClass());

		assertEquals(1, function.getInlineASMListSize());
	}

    @Test
    public void testParseVariableValid() throws ParseException
    {
    	CodeBuilderVariable codeBuilderVariable = new CodeBuilderVariable()
    		.setId("MyVariable")
    		.setType(new CodeBuilderIntType());

    	ASTVariable variable = parse(codeBuilderVariable.toString()).parseVariable();

    	assertEquals("MyVariable", variable.getId().toString());
    	expectsClass(ASTIntType.class, variable.getType().getClass());
    }

    @Test
    public void testParseVariableInitValid() throws ParseException
    {
    	CodeBuilderVariableInit codeBuilderVariableInit = new CodeBuilderVariableInit()
    		.setId("MyVariable")
    		.setType(new CodeBuilderStringType())
    		.setExpr(new CodeBuilderStringLiteral().setId("Hello World"));

    	ASTVariable variable = parse(codeBuilderVariableInit.toString()).parseVariable();
    	ASTVariableInit variableInit = castExpectClass(ASTVariableInit.class, variable);

    	assertEquals("MyVariable", variableInit.getId().toString());
    	expectsClass(ASTStringType.class, variableInit.getType().getClass());
    	expectsClass(ASTStringLiteral.class, variableInit.getExpr().getClass());
    	ASTStringLiteral stringLiteral = castExpectClass(ASTStringLiteral.class, variableInit.getExpr());
    	assertEquals("Hello World", stringLiteral.getValue());
    }

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

    private Parser parse(String input)
	{
	    if (input == null || input.isEmpty()) {
	        throw new IllegalArgumentException("Input must not be empty");
	    }

		StringReader stringReader = new StringReader(input);
		BufferedReader bufferedReader = new BufferedReader(stringReader);
		Parser parser = new Parser(bufferedReader);
		parser.token = parser.lexer.nextToken();

		return parser;
	}
}