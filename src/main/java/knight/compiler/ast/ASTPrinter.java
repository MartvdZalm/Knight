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

package knight.compiler.ast;

import java.io.*;

import knight.compiler.lexer.Lexer;
import knight.compiler.ast.declarations.*;
import knight.compiler.ast.expressions.*;
import knight.compiler.ast.expressions.operations.*;
import knight.compiler.ast.statements.*;
import knight.compiler.ast.statements.conditionals.*;
import knight.compiler.ast.types.*;
import knight.compiler.parser.*;

/*
 * File: ASTPrinter.java
 * @author: Mart van der Zalm
 * Date: 2024-03-17
 * Description:
 */
public class ASTPrinter implements ASTVisitor<String>
{
	int level = 0;

	private void incLevel()
	{
		level = level + 1;
	}

	private void decLevel()
	{
		level = level - 1;
	}

	private String printInc()
	{
		char[] chars = new char[level];
		java.util.Arrays.fill(chars, '\t');
		return new String(chars);
	}

	@Override
	public String visit(ASTAssign assign)
	{
		return printInc() + "(EQSIGN " + assign.getId().accept(this) + " " + assign.getExpr().accept(this) + ")";
	}

	@Override
	public String visit(ASTSkip skip)
	{
		return "";
	}

	@Override
	public String visit(ASTBlock block)
	{
		StringBuilder strBuilder = new StringBuilder();

		for (ASTStatement statement : block.getStatementList()) {
			System.out.println(statement);
			strBuilder.append(statement.accept(this) + "\n");
		}

		return strBuilder.toString();
	}

	@Override
	public String visit(ASTIfThenElse ifThenElse)
	{
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(printInc() + "(IF " + ifThenElse.getExpr().accept(this) + "\n");

		incLevel();
		strBuilder.append(ifThenElse.getThen().accept(this) + "\n");
		String elze = ifThenElse.getElze().accept(this);
		if (elze != null && elze.trim().length() > 0) {
			strBuilder.append(elze + "\n");
		}
		decLevel();

		strBuilder.append(printInc() + ")\n");
		return strBuilder.toString();
	}

	@Override
	public String visit(ASTWhile w)
	{
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(printInc() + "(WHILE " + w.getExpr().accept(this) + "\n");

		incLevel();
		strBuilder.append(w.getBody().accept(this));
		decLevel();

		strBuilder.append(printInc() + ")\n");
		return strBuilder.toString();
	}

	@Override
	public String visit(ASTIntLiteral intLiteral)
	{
		return "(INTLIT " + intLiteral.getValue() + ")";
	}

	@Override
	public String visit(ASTPlus plus)
	{
		return "(PLUS " + plus.getLhs().accept(this) + " " + plus.getRhs().accept(this) + ")";
	}

	@Override
	public String visit(ASTMinus minus)
	{
		return "(MINUS " + minus.getLhs().accept(this) + " " + minus.getRhs().accept(this) + ")";
	}

	@Override
	public String visit(ASTTimes times)
	{
		return "(TIMES " + times.getLhs().accept(this) + " " + times.getRhs().accept(this) + ")";
	}

	@Override
	public String visit(ASTDivision division)
	{
		return "(DIV " + division.getLhs().accept(this) + " " + division.getRhs().accept(this) + ")";
	}

	@Override
	public String visit(ASTEquals equals)
	{
		return "(EQUALS " + equals.getLhs().accept(this) + " " + equals.getRhs().accept(this) + ")";
	}

	@Override
	public String visit(ASTLessThan lessThan)
	{
		return "(< " + lessThan.getLhs().accept(this) + " " + lessThan.getRhs().accept(this) + ")";
	}

	@Override
	public String visit(ASTAnd and)
	{
		return "(&& " + and.getLhs().accept(this) + " " + and.getRhs().accept(this) + ")";
	}

	@Override
	public String visit(ASTOr or)
	{
		return "(|| " + or.getLhs().accept(this) + " " + or.getRhs().accept(this) + ")";
	}

	@Override
	public String visit(ASTTrue true1)
	{
		return "TRUE";
	}

	@Override
	public String visit(ASTFalse false1)
	{
		return "FALSE";
	}

	@Override
	public String visit(ASTIdentifierExpr identifier)
	{
		return "(" + identifier.getId() + ")";
	}

	@Override
	public String visit(ASTNewArray newArray)
	{
		return "(NEW-INT-ARRAY " + newArray.getArrayLength().accept(this) + ")";
	}

	@Override
	public String visit(ASTNewInstance newInstance)
	{
		return "(NEW) " + newInstance.getClassName().accept(this);
	}

	@Override
	public String visit(ASTCallFunctionExpr callFunctionExpr)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("(DOT " + callFunctionExpr.getInstanceName().accept(this) + " (FUN-CALL " + callFunctionExpr.getFunctionId().accept(this));
		for (ASTExpression expr : callFunctionExpr.getArgExprList()) {
			sb.append(expr.accept(this));
		}
		sb.append("))");

		return sb.toString();
	}

	@Override
	public String visit(ASTCallFunctionStat callFunctionStat)
	{
		StringBuilder sb = new StringBuilder();
		// sb.append("(DOT " + callFunctionStat.getInstanceName().accept(this) + " (FUN-CALL " + callFunctionStat.getFunctionId().accept(this));
		sb.append("(DOT (FUN-CALL " + callFunctionStat.getFunctionId().accept(this));
		for (ASTExpression expr : callFunctionStat.getArgExprList()) {
			sb.append(expr.accept(this));
		}
		sb.append("))");

		return sb.toString();
	}

	@Override
	public String visit(ASTIntType intType)
	{
		return "(INT)";
	}

	@Override
	public String visit(ASTStringType stringType)
	{
		return "(STRING)";
	}

	@Override
	public String visit(ASTBooleanType booleanType)
	{
		return "(BOOLEAN)";
	}

	@Override
	public String visit(ASTFunctionType functionType)
	{
		return "FUNCTION";
	}

	@Override
	public String visit(ASTIntArrayType intArrayType)
	{
		return "(INT-ARRAY)";
	}

	@Override
	public String visit(ASTVoidType voitType)
	{
		return "(VOID)";
	}

	@Override
	public String visit(ASTIdentifierType identifierType)
	{
		return "(IDENTIFIER)";
	}

	@Override
	public String visit(ASTVariable varDeclaration)
	{
		return printInc() + "(VARIABLE) " + varDeclaration.getType().accept(this) + " " + varDeclaration.getId().accept(this);
	}

	@Override
	public String visit(ASTVariableInit varDeclarationInit)
	{
		return printInc() + "(VARIABLE) " + varDeclarationInit.getType().accept(this) + " " + varDeclarationInit.getId().accept(this) + " = " + varDeclarationInit.getExpr().accept(this);
	}

	@Override
	public String visit(ASTArgument argument)
	{
		return " " + argument.getType().accept(this) + " " + argument.getId().accept(this);
	}

	@Override
	public String visit(ASTFunction function)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(printInc() + "(FUNCTION) " + function.getId().accept(this) + " ");

		sb.append("(PARAMETERS)");
		for (ASTArgument arg : function.getArgumentList()) {
			sb.append(arg.accept(this));
		}

		sb.append(" : " + function.getReturnType().accept(this) + "\n");

		incLevel();

		for (ASTVariable variable : function.getVariableList()) {
			sb.append(variable.accept(this) + "\n");
		}

		for (ASTStatement statement : function.getStatementList()) {
			sb.append(statement.accept(this) + "\n");
		}

		decLevel();

		return sb.toString();
	}

	@Override
	public String visit(ASTFunctionReturn functionReturn)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(printInc() + "(MTD-DECL " + functionReturn.getReturnType().accept(this) + " " + functionReturn.getId().accept(this) + " ");

		sb.append("(TY-ID-LIST ");
		for (ASTArgument arg : functionReturn.getArgumentList()) {
			sb.append(arg.accept(this));
		}
		sb.append(")\n");
		sb.append(printInc() + "(BLOCK\n");

		incLevel();
		for (ASTVariable variable : functionReturn.getVariableList()) {
			sb.append(variable.accept(this) + "\n");
		}
		for (ASTStatement stat : functionReturn.getStatementList()) {
			sb.append(stat.accept(this) + "\n");
		}
		sb.append(printInc() + "(RETURN " + functionReturn.getReturnExpr().accept(this) + ")\n");
		decLevel();

		sb.append(printInc() + ")\n");
		sb.append(printInc() + ")");
		return sb.toString();
	}

	@Override
	public String visit(ASTProgram program)
	{
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < program.getVariableListSize(); i++) {
			sb.append(program.getVariableDeclAt(i).accept(this) + "\n");
		}

		for (ASTFunction function : program.getFunctionList()) {
			sb.append(function.accept(this) + "\n");
		}

		for (ASTClass astClass : program.getClassList()) {
			sb.append(astClass.accept(this) + "\n");
		}

		return sb.toString();
	}

	@Override
	public String visit(ASTIdentifier identifier)
	{
		return "(" + identifier.getId() + ")";
	}

	@Override
	public String visit(ASTArrayIndexExpr arrayIndexExpr)
	{
		return "(ARRAY-LOOKUP " + arrayIndexExpr.getArray().accept(this) + arrayIndexExpr.getIndex().accept(this) + ")";
	}

	@Override
	public String visit(ASTArrayAssign arrayAssign)
	{
		return printInc() + "(EQSIGN " + "(ARRAY-ASSIGN " + arrayAssign.getId().accept(this) + arrayAssign.getExpression1().accept(this) + ") " + arrayAssign.getExpression2().accept(this) + ")";
	}

	@Override
	public String visit(ASTStringLiteral stringLiteral)
	{
		return "(STRINGLIT " + stringLiteral.getValue() + ")";
	}

	@Override
	public String visit(ASTClass classDecl)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("(CLASS) " + classDecl.getId().accept(this) + "\n");

		incLevel();

		for (ASTProperty property : classDecl.getPropertyList()) {
			sb.append(property.accept(this) + "\n");
		}

		for (ASTFunction function : classDecl.getFunctionList()) {
			sb.append(function.accept(this) + "\n");
		}
		
		decLevel();
		sb.append(")");
		
		return sb.toString();
	}

	@Override
	public String visit(ASTForLoop forLoop)
	{
		return null;
	}

	@Override
	public String visit(ASTInlineASM assembly)
	{
		return null;
	}

	@Override
	public String visit(ASTModulus modulus)
	{
		return null;
	}

	@Override
	public String visit(ASTReturnStatement returnStatement)
	{
		return null;
	}

	@Override
	public String visit(ASTGreaterThanOrEqual greaterThanOrEqual)
	{
		return null;
	}

	@Override
	public String visit(ASTGreaterThan greaterThan)
	{
		return null;
	}

	@Override
	public String visit(ASTLessThanOrEqual lessThanOrEqual)
	{
		return null;
	}

	@Override
	public String visit(ASTPointerAssign pointerAssign)
	{
		StringBuilder sb = new StringBuilder();

		sb.append(printInc() + pointerAssign.getPointer().accept(this) + " -> ");
		sb.append(pointerAssign.getVariable().accept(this) + " = ");
		sb.append(pointerAssign.getExpression().accept(this));

		return sb.toString();
	}

	@Override
	public String visit(ASTThis astThis)
	{
		return "(THIS)";
	}

	@Override
	public String visit(ASTProperty property)
	{
		return null;
	}

	public static String printFileAst(String filename) throws FileNotFoundException, ParseException
	{
		ASTPrinter printer = new ASTPrinter();

		BufferedReader br = new BufferedReader(new FileReader(filename));
		Parser p = new Parser(br);
		AST tree = p.parse();

		return printer.visit((ASTProgram) tree);
	}
}