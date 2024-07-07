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


import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;


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

import knight.builder.code.declarations.*;
import knight.builder.code.expressions.*;
import knight.builder.code.expressions.operations.*;
import knight.builder.code.statements.*;
import knight.builder.code.types.*;
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

		StringReader stringReader = new StringReader(codeBuilderProgram.toString());
		BufferedReader bufferedReader = new BufferedReader(stringReader);
		Parser parser = new Parser(bufferedReader);

		AST ast = parser.parse();
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
				// new CodeBuilderForLoop(),
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

		assertEquals(3, function.getStatementListSize());
		expectsClass(ASTWhile.class, function.getStatementDeclAt(0).getClass());
		// expectsClass(ASTForLoop.class, function.getStatementDeclAt(1).getClass());
		expectsClass(ASTIfThenElse.class, function.getStatementDeclAt(1).getClass());
		expectsClass(ASTAssign.class, function.getStatementDeclAt(2).getClass());

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
    	assertEquals("\"Hello World\"", stringLiteral.getValue());
    }

    @Test
	public void testParseExpression() throws ParseException
	{
		ASTExpression exprInt = parse("10;").parseExpression();
		ASTIntLiteral intLiteral = castExpectClass(ASTIntLiteral.class, exprInt);
		assertEquals(10, intLiteral.getValue());

		ASTExpression exprTimes = parse("5 * 6;").parseExpression();
		ASTTimes times = castExpectClass(ASTTimes.class, exprTimes);
		ASTIntLiteral lhsTimes = (ASTIntLiteral) times.getLhs();
		ASTIntLiteral rhsTimes = (ASTIntLiteral) times.getRhs();
		assertEquals(5, lhsTimes.getValue());
		assertEquals(6, rhsTimes.getValue());

		ASTExpression exprPlus = parse("34 + 23;").parseExpression();
		ASTPlus plus = castExpectClass(ASTPlus.class, exprPlus);
		ASTIntLiteral lhsPlus = (ASTIntLiteral) plus.getLhs();
		ASTIntLiteral rhsPlus = (ASTIntLiteral) plus.getRhs();
		assertEquals(34, lhsPlus.getValue());
		assertEquals(23, rhsPlus.getValue());

		ASTExpression exprDivision = parse("10 / 2;").parseExpression();
		ASTDivision division = castExpectClass(ASTDivision.class, exprDivision);
		ASTIntLiteral lhsDivision = (ASTIntLiteral) division.getLhs();
		ASTIntLiteral rhsDivision = (ASTIntLiteral) division.getRhs();
		assertEquals(10, lhsDivision.getValue());
		assertEquals(2, rhsDivision.getValue());

		ASTExpression exprMinus = parse("100 - 23;").parseExpression();
		ASTMinus minus = castExpectClass(ASTMinus.class, exprMinus);
		ASTIntLiteral lhsMinus = (ASTIntLiteral) minus.getLhs();
		ASTIntLiteral rhsMinus = (ASTIntLiteral) minus.getRhs();
		assertEquals(100, lhsMinus.getValue());
		assertEquals(23, rhsMinus.getValue());

		ASTExpression exprString = parse("\"Hello World\";").parseExpression();
		ASTStringLiteral stringLiteral = castExpectClass(ASTStringLiteral.class, exprString);
		assertEquals("\"Hello World\"", stringLiteral.getValue());

		ASTExpression exprTrue = parse("true;").parseExpression();
		ASTTrue astTrue = castExpectClass(ASTTrue.class, exprTrue);
		ASTExpression exprFalse = parse("false;").parseExpression();
		ASTFalse astFalse = castExpectClass(ASTFalse.class, exprFalse);

		ASTExpression exprIdentifier = parse("age;").parseExpression();
		ASTIdentifierExpr identifierExpr = castExpectClass(ASTIdentifierExpr.class, exprIdentifier);
		assertEquals("age", identifierExpr.getId().toString());

		ASTExpression exprCallFunction = parse("calculate();").parseExpression();
		ASTCallFunctionExpr callFunctionExpr = castExpectClass(ASTCallFunctionExpr.class, exprCallFunction);
		assertEquals("calculate", callFunctionExpr.getMethodId().toString());

		ASTExpression exprNewInstance = parse("new Person();").parseExpression();
		ASTNewInstance newInstance = castExpectClass(ASTNewInstance.class, exprNewInstance);
		assertEquals("Person", newInstance.getClassName().toString());

		ASTExpression exprNewArray = parse("new int[52];").parseExpression();
		ASTNewArray newArray = castExpectClass(ASTNewArray.class, exprNewArray);
		ASTIntLiteral arrayLengthIntLiteral = castExpectClass(ASTIntLiteral.class, newArray.getArrayLength());
		assertEquals(52, arrayLengthIntLiteral.getValue());
	}

    @Test
    public void testParseArguments() throws ParseException
    {
    	String arguments = "(";
    	for (int i = 0; i < 10; i++) {
    		if (i < 9) {
    			arguments += new CodeBuilderArgument().toString() +  ", ";
    		} else {
    			arguments += new CodeBuilderArgument().toString();
    		}
    	}
    	arguments += ")";

    	List<ASTArgument> argumentList = parse(arguments).parseArguments();
    	assertEquals(10, argumentList.size());
    }

    @Test
    public void testParseArgument() throws ParseException
    {
    	CodeBuilderArgument codeBuilderArgumentInt = new CodeBuilderArgument()
    		.setId("MyArgument")
    		.setType(new CodeBuilderIntType());

    	ASTArgument argumentInt = parse(codeBuilderArgumentInt.toString()).parseArgument();
    	assertEquals("MyArgument", argumentInt.getId().toString());
    	expectsClass(ASTIntType.class, argumentInt.getType().getClass());

    	CodeBuilderArgument codeBuilderArgumentString = new CodeBuilderArgument()
    		.setId("MyArgument")
    		.setType(new CodeBuilderStringType());

    	ASTArgument argumentString = parse(codeBuilderArgumentString.toString()).parseArgument();
    	assertEquals("MyArgument", argumentString.getId().toString());
    	expectsClass(ASTStringType.class, argumentString.getType().getClass());

    	CodeBuilderArgument codeBuilderArgumentBoolean = new CodeBuilderArgument()
    		.setId("MyArgument")
    		.setType(new CodeBuilderBooleanType());

    	ASTArgument argumentBoolean = parse(codeBuilderArgumentBoolean.toString()).parseArgument();
    	assertEquals("MyArgument", argumentBoolean.getId().toString());
    	expectsClass(ASTBooleanType.class, argumentBoolean.getType().getClass());
    }

   	@Test
    public void testParseStatement() throws ParseException
    {
    	CodeBuilderIfThenElse codeBuilderIfThenElse = new CodeBuilderIfThenElse();
    	ASTStatement ifThenElse = parse(codeBuilderIfThenElse.toString()).parseStatement();
    	expectsClass(ASTIfThenElse.class, ifThenElse.getClass());

    	CodeBuilderWhile codeBuilderWhile = new CodeBuilderWhile();
    	ASTStatement astWhile = parse(codeBuilderWhile.toString()).parseStatement();
    	expectsClass(ASTWhile.class, astWhile.getClass());

    	// The for loop will be added later on
    	// CodeBuilderForLoop codeBuilderForLoop = new CodeBuilderForLoop();
    	// ASTStatement forLoop = parse(codeBuilderForLoop.toString()).parseStatement();
    	// expectsClass(ASTForLoop.class, forLoop.getClass());
    }

   	@Test
    public void testParseIdentifier() throws ParseException
    {
    	CodeBuilderIdentifier codeBuilderIdentifier = new CodeBuilderIdentifier()
    		.setId("MyIdentifier");

    	ASTIdentifier identifier = parse(codeBuilderIdentifier.toString()).parseIdentifier();
    	assertEquals("MyIdentifier", identifier.getId().toString());
    }

    @Test
    public void testParseType() throws ParseException
    {
    	CodeBuilderVariable codeBuilderVariableIntType = new CodeBuilderVariable()
    		.setType(new CodeBuilderIntType());
    	ASTType intType = parse(codeBuilderVariableIntType.toString()).parseType();
    	expectsClass(ASTIntType.class, intType.getClass());

    	CodeBuilderStringType codeBuilderStringType = new CodeBuilderStringType();
    	ASTType stringType = parse(codeBuilderStringType.toString()).parseType();
    	expectsClass(ASTStringType.class, stringType.getClass());

    	CodeBuilderBooleanType codeBuilderBooleanType = new CodeBuilderBooleanType();
    	ASTType booleanType = parse(codeBuilderBooleanType.toString()).parseType();
    	expectsClass(ASTBooleanType.class, booleanType.getClass());

    	CodeBuilderIdentifierType codeBuilderIdentifierType = new CodeBuilderIdentifierType();
    	ASTType identifierType = parse(codeBuilderIdentifierType.toString()).parseType();
    	expectsClass(ASTIdentifierType.class, identifierType.getClass());

    	CodeBuilderVoidType codeBuilderVoidType = new CodeBuilderVoidType();
    	ASTType voidType = parse(codeBuilderVoidType.toString()).parseType();
    	expectsClass(ASTVoidType.class, voidType.getClass());

    	CodeBuilderIntArrayType codeBuilderIntArrayType = new CodeBuilderIntArrayType();
    	ASTType intArrayType = parse(codeBuilderIntArrayType.toString()).parseType();
    	expectsClass(ASTIntArrayType.class, intArrayType.getClass());
    }

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