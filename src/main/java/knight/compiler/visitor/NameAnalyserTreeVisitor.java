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

package knight.compiler.visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import knight.compiler.ast.declarations.*;
import knight.compiler.ast.expressions.*;
import knight.compiler.ast.expressions.operations.*;
import knight.compiler.ast.statements.*;
import knight.compiler.ast.statements.conditionals.*;
import knight.compiler.ast.types.*;
import knight.compiler.ast.*;

import knight.compiler.lexer.*;
import knight.compiler.semantics.*;
import knight.compiler.symbol.SymbolClass;
import knight.compiler.symbol.SymbolFunction;
import knight.compiler.symbol.SymbolProgram;
import knight.compiler.symbol.SymbolVariable;

/*
 * File: NameAnalyserTreeVisitor.java
 * @author: Mart van der Zalm
 * Date: 2024-01-06
 * Description:
 */
public class NameAnalyserTreeVisitor implements ASTVisitor<ASTType>
{
	private SymbolProgram symbolProgram;
	private SymbolClass symbolClass;
	private SymbolFunction symbolFunction;

	private Set<String> hsymbolClass = new HashSet<>();
	private Set<String> hsymbolFunction = new HashSet<>();

	public NameAnalyserTreeVisitor(SymbolProgram symbolProgram)
	{
		this.symbolProgram = symbolProgram;
	}

	@Override
	public ASTType visit(ASTInclude include)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTAssign assign)
	{
		assign.getId().accept(this);
		assign.getExpr().accept(this);
		return null;
	}
	
	@Override
	public ASTType visit(ASTBlock block)
	{
		for (int i = 0; i < block.getStatListSize(); i++) {
			ASTStatement st = block.getStatAt(i);
			st.accept(this);
		}
		return null;
	}

	@Override
	public ASTType visit(ASTIfThenElse ifThenElse)
	{
		ifThenElse.getExpr().accept(this);
		ifThenElse.getThen().accept(this);
		ifThenElse.getElze().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTSkip skip)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTWhile while1)
	{
		while1.getExpr().accept(this);
		while1.getBody().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTIntLiteral intLiteral)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTPlus plus)
	{
		plus.getLhs().accept(this);
		plus.getRhs().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTMinus minus)
	{
		minus.getLhs().accept(this);
		minus.getRhs().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTTimes times)
	{
		times.getLhs().accept(this);
		times.getRhs().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTIncrement increment)
	{
		increment.getExpr().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTModulus modulus)
	{
		modulus.getLhs().accept(this);
		modulus.getRhs().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTDivision division)
	{
		division.getLhs().accept(this);
		division.getRhs().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTEquals equals)
	{
		equals.getLhs().accept(this);
		equals.getRhs().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTLessThan lessThan)
	{
		lessThan.getLhs().accept(this);
		lessThan.getRhs().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTLessThanOrEqual lessThanOrEqual)
	{
		lessThanOrEqual.getLhs().accept(this);
		lessThanOrEqual.getRhs().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTGreaterThan greaterThan)
	{
		greaterThan.getLhs().accept(this);
		greaterThan.getRhs().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTGreaterThanOrEqual greaterThanOrEqual)
	{
		greaterThanOrEqual.getLhs().accept(this);
		greaterThanOrEqual.getRhs().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTAnd and)
	{
		and.getLhs().accept(this);
		and.getRhs().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTOr or)
	{
		or.getLhs().accept(this);
		or.getRhs().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTTrue true1)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTFalse false1)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTNewArray newArray)
	{
		newArray.getArrayLength().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTNewInstance ni)
	{
		String id = ni.getClassName().getId();
		SymbolClass klass = symbolProgram.getClass(id);
		if (klass == null) {
			Token sym = ni.getClassName().getToken();
			addError(sym.getRow(), sym.getCol(), "class " + id + " is not declared");
		}

		ni.getClassName().setB(klass);
		return null;
	}

	@Override
	public ASTType visit(ASTCallFunctionExpr cm)
	{
		/*
		 * The call function expression can be written like this 'object.functionName()'. Here is object the instancename,
		 * but if there is no instancename 'functionName()', the instancename will be null. So This check needs to be done.
		 */
		if (cm.getInstanceName() != null) {
			cm.getInstanceName().accept(this);
		}

		for (int i = 0; i < cm.getArgExprListSize(); i++) {
			ASTExpression e = cm.getArgExprAt(i);
			e.accept(this);
		}

		return null;
	}

	@Override
	public ASTType visit(ASTCallFunctionStat cm)
	{
		for (int i = 0; i < cm.getArgExprListSize(); i++) {
			ASTExpression e = cm.getArgExprAt(i);
			e.accept(this);
		}
		return null;
	}

	@Override
	public ASTType visit(ASTFunctionType functionType)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTIntType intType)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTStringType stringType)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTVoidType voidType)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTBooleanType booleanType)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTIntArrayType intArrayType)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTIdentifier identifier)
	{
		String id = identifier.getId();
		SymbolVariable var = symbolProgram.getVariable(id, symbolClass, symbolFunction);

		if (var == null) {
			Token sym = identifier.getToken();
			addError(sym.getRow(), sym.getCol(), "variable " + id + " is not declared");
		}

		identifier.setB(var);
		return null;
	}

	@Override
	public ASTType visit(ASTIdentifierType identifierType)
	{
		String id = identifierType.getId();
		SymbolClass klass = symbolProgram.getClass(id);
		if (klass == null) {
			Token sym = identifierType.getToken();
			addError(sym.getRow(), sym.getCol(), "class " + id + " is not declared");
		}

		identifierType.setB(klass);
		return null;
	}

	@Override
	public ASTType visit(ASTIdentifierExpr identifierExpr)
	{
		String id = identifierExpr.getId();
		SymbolVariable var = symbolProgram.getVariable(id, symbolClass, symbolFunction);
		if (var == null) {
			Token sym = identifierExpr.getToken();
			addError(sym.getRow(), sym.getCol(), "variable " + id + " is not declared");
		}
		
		identifierExpr.setB(var);
		return null;
	}

	public void checkVariable(ASTVariable varDecl)
	{
		String id = varDecl.getId().getId();
		
		varDecl.getType().accept(this);
		varDecl.getId().accept(this);
	}

	@Override
	public ASTType visit(ASTVariable varDeclNoInit)
	{	
		checkVariable(varDeclNoInit);
		return null;
	}
	
	@Override
	public ASTType visit(ASTVariableInit varDeclInit)
	{
		checkVariable(varDeclInit);
		varDeclInit.getExpr().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTArgument ad)
	{
		ad.getId().accept(this);
		return null;
	}

	public void checkFunction(ASTFunction functionDecl)
	{
		String functionName = functionDecl.getId().getId();

		if (hsymbolFunction.contains(functionName)) {
			return;
		} else {
			hsymbolFunction.add(functionName);
		}

		functionDecl.getReturnType().accept(this);

		if (symbolClass == null) {
			symbolFunction = symbolProgram.getFunction(functionName);
		} else {
			symbolFunction = symbolClass.getFunction(functionName);
		}

		functionDecl.getId().setB(symbolFunction);

		for (int i = 0; i < functionDecl.getArgumentListSize(); i++) {
			functionDecl.getArgumentDeclAt(i).accept(this);
		}

		for (int i = 0; i < functionDecl.getVariableListSize(); i++) {
			functionDecl.getVariableDeclAt(i).accept(this);
		}

		for (int i = 0; i < functionDecl.getStatementListSize(); i++) {
			functionDecl.getStatementDeclAt(i).accept(this);
		}
	}

	@Override
	public ASTType visit(ASTFunction functionDecl)
	{
		checkFunction(functionDecl);
		symbolFunction = null;
		return null;
	}

	@Override
	public ASTType visit(ASTFunctionReturn functionReturn)
	{
		checkFunction(functionReturn);
		functionReturn.getReturnExpr().accept(this);
		symbolFunction = null;
		return null;
	}

	@Override
	public ASTType visit(ASTProgram program)
	{
		for (int i = 0; i < program.getIncludeListSize(); i++) {
			program.getIncludeDeclAt(i).accept(this);
		}

		for (int i = 0; i < program.getEnumListSize(); i++) {
			program.getEnumDeclAt(i).accept(this);
		}

		for (int i = 0; i < program.getInterListSize(); i++) {
			program.getInterDeclAt(i).accept(this);
		}

		for (int i = 0; i < program.getClassListSize(); i++) {
			program.getClassDeclAt(i).accept(this);
		}

		for (int i = 0; i < program.getFunctionListSize(); i++) {
			program.getFunctionDeclAt(i).accept(this);
		}

		for (int i = 0; i < program.getVariableListSize(); i++) {
			program.getVariableDeclAt(i).accept(this);
		}

		return null;
	}

	@Override
	public ASTType visit(ASTReturnStatement returnStatement)
	{
		returnStatement.getReturnExpr().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTArrayIndexExpr ia)
	{
		ia.getArray().accept(this);
		ia.getIndex().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTArrayAssign aa)
	{
		aa.getIdentifier().accept(this);
		aa.getE1().accept(this);
		aa.getE2().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTStringLiteral stringLiteral)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTClass cd)
	{
		String id = cd.getId().getId();
		if (hsymbolClass.contains(id)) { 
			return null;
		} else {
			hsymbolClass.add(id);
		}

		symbolClass = symbolProgram.getClass(id);
		cd.getId().setB(symbolClass);

		for (int i = 0; i < cd.getVariableListSize(); i++) {
			cd.getVariableDeclAt(i).accept(this);;
		}

		for (int i = 0; i < cd.getFunctionListSize(); i++) {
			cd.getFunctionDeclAt(i).accept(this);;
		}

		hsymbolFunction.clear();

		symbolClass = null;
		return null;
	}

	public static void addError(int line, int col, String errorText)
	{
		SemanticErrors.addError(line, col, errorText);
	}
	
	@Override
	public ASTType visit(ASTEnumeration enumDecl)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTInterface interDecl)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTForLoop forLoop)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTInlineASM assembly)
	{
		return null;
	}
}