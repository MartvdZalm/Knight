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

import knight.compiler.ast.declarations.*;
import knight.compiler.ast.expressions.*;
import knight.compiler.ast.expressions.operations.*;
import knight.compiler.ast.statements.*;
import knight.compiler.ast.statements.conditionals.*;
import knight.compiler.ast.types.*;
import knight.compiler.ast.*;

import knight.compiler.lexer.*;
import knight.compiler.semantics.SemanticErrors;

import knight.compiler.symbol.SymbolClass;
import knight.compiler.symbol.SymbolFunction;
import knight.compiler.symbol.SymbolProgram;

/*
 * File: BuildSymbolProgramVisitor.java
 * @author: Mart van der Zalm
 * Date: 2024-01-06
 * Description:
 */
public class BuildSymbolProgramVisitor implements ASTVisitor<ASTType>
{
	private SymbolProgram symbolProgram;
	private SymbolClass symbolClass;
	private SymbolFunction symbolFunction;
	private String mKlassId;

	public BuildSymbolProgramVisitor()
	{
		symbolProgram = new SymbolProgram();
	}

	public SymbolProgram getSymbolProgram()
	{
		return symbolProgram;
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
	public ASTType visit(ASTClass classDecl)
	{
		String identifier = classDecl.getId().getId();

		if (!symbolProgram.addClass(identifier, null)) {
			Token sym = classDecl.getToken();
			addError(sym.getRow(), sym.getCol(), "Class " + identifier + " is already defined!");
			symbolClass = new SymbolClass(identifier, null);
		} else {
			symbolClass = symbolProgram.getClass(identifier);
		}

		for (int i = 0; i < classDecl.getVariableListSize(); i++) {
			classDecl.getVariableDeclAt(i).accept(this);
		}

		for (int i = 0; i < classDecl.getFunctionListSize(); i++) {
			classDecl.getFunctionDeclAt(i).accept(this);
		}

		symbolClass = null;
		return null;
	}

	public void checkFunction(ASTFunction funcDecl)
	{
		ASTType type = funcDecl.getReturnType().accept(this);
		String id = funcDecl.getId().getId();

		if (symbolClass == null) {
			if (!symbolProgram.addFunction(id, type)) {
				Token tok = funcDecl.getToken();
				addError(tok.getRow(), tok.getCol(), "Function " + id + " already defined");
			} else {
				symbolFunction = symbolProgram.getFunction(id);
			}
		} else {
			if (!symbolClass.addFunction(id, type)) {
				Token tok = funcDecl.getToken();
				addError(tok.getRow(), tok.getCol(), "Function " + id + " already defined in class " + symbolClass.getId());
			} else {
				symbolFunction = symbolClass.getFunction(id);
			}
		}	

		for (int i = 0; i < funcDecl.getArgumentListSize(); i++) {
			funcDecl.getArgumentDeclAt(i).accept(this);
		}

		for (int i = 0; i < funcDecl.getVariableListSize(); i++) {
			funcDecl.getVariableDeclAt(i).accept(this);
		}

		for (int i = 0; i < funcDecl.getStatementListSize(); i++) {
			funcDecl.getStatementDeclAt(i).accept(this);
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
	public ASTType visit(ASTInclude include)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTSkip skip)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTAssign assign)
	{
		return null;
	}
	
	@Override
	public ASTType visit(ASTBlock block)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTIfThenElse ifThenElse)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTWhile while1)
	{
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
		return null;
	}

	@Override
	public ASTType visit(ASTMinus minus)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTTimes times)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTIncrement increment)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTModulus modulus)
	{
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
	public ASTType visit(ASTIdentifierExpr identifierExpr)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTNewArray newArray)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTNewInstance newInstance)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTCallFunctionExpr callFunctionExpr)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTCallFunctionStat callFunctionStat)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTReturnStatement returnStatement)
	{
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
		return intType;
	}

	@Override
	public ASTType visit(ASTStringType stringType)
	{
		return stringType;
	}

	@Override
	public ASTType visit(ASTVoidType voidType)
	{
		return voidType;		
	}

	@Override
	public ASTType visit(ASTBooleanType booleanType)
	{
		return booleanType;
	}

	@Override
	public ASTType visit(ASTIntArrayType intArrayType)
	{
		return intArrayType;
	}

	@Override
	public ASTType visit(ASTIdentifierType identifierType)
	{
		String id = identifierType.getId();

		if (id != null && id.equals(mKlassId)) {
			Token tok = identifierType.getToken();
			addError(tok.getRow(), tok.getCol(), "Class " + id + " cannot be used as a type in class " + symbolClass.getId());
		}

		return identifierType;
	}

	public void checkIfVariableExist(ASTVariable varDecl)
	{
		ASTType t = varDecl.getType().accept(this);
		String id = varDecl.getId().getId();

		if (symbolFunction != null) {
			if (!symbolFunction.addVariable(id, t)) {
				Token tok = varDecl.getId().getToken();
				addError(tok.getRow(), tok.getCol(), "Variable " + id + " already defined in method " + symbolFunction.getId() + " in class " + symbolClass.getId());
			}
		} else if (symbolClass != null) {
			if (!symbolClass.addVariable(id, t)) {
				Token sym = varDecl.getId().getToken();
				addError(sym.getRow(), sym.getCol(), "Variable " + id + " already defined in class " + symbolClass.getId());
			}
		} else {
			if (!symbolProgram.addVariable(id, t)) {
				Token sym = varDecl.getId().getToken();
				addError(sym.getRow(), sym.getCol(), "Variable " + id + " already defined");
			}
		}
	}

	@Override
	public ASTType visit(ASTVariable varDecl)
	{
		checkIfVariableExist(varDecl);
		return null;
	}

	@Override
	public ASTType visit(ASTVariableInit varDeclInit)
	{
		checkIfVariableExist(varDeclInit);
		return null;
	}

	@Override
	public ASTType visit(ASTArgument argDecl)
	{
		ASTType t = argDecl.getType().accept(this);
		String id = argDecl.getId().getId();

		if (!symbolFunction.addParam(id, t)) {
			Token sym = argDecl.getId().getToken();
			addError(sym.getRow(), sym.getCol(), "Argument " + id + " already defined in method " + symbolFunction.getId() + " in class " + symbolClass.getId());
		}
		return null;
	}

	@Override
	public ASTType visit(ASTIdentifier identifier)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTArrayIndexExpr indexArray)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTArrayAssign arrayAssign)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTStringLiteral stringLiteral)
	{
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
}