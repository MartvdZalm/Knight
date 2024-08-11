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

import java.io.File;
import java.io.PrintWriter;
import java.io.*;

import knight.compiler.ast.declarations.*;
import knight.compiler.ast.expressions.*;
import knight.compiler.ast.expressions.operations.*;
import knight.compiler.ast.statements.*;
import knight.compiler.ast.statements.conditionals.*;
import knight.compiler.ast.types.*;
import knight.compiler.ast.*;

import knight.compiler.semantics.*;
import knight.compiler.symbol.SymbolClass;
import knight.compiler.symbol.SymbolFunction;
import knight.compiler.symbol.SymbolProgram;
import knight.compiler.symbol.SymbolVariable;

import knight.compiler.asm.ASM;
import knight.compiler.asm.declarations.*;

/*
 * File: CodeGen.java
 * @author: Mart van der Zalm
 * Date: 2024-08-06
 * Description:
 */
public class Codegen implements ASTVisitor<ASM>
{

	@Override
	public ASMAssign visit(ASTAssign astAssign)
	{
		ASMAssign asmAssign = new ASMAssign();

		return asmAssign;
	}

	@Override
	public ASMBlock visit(ASTBlock astBlock)
	{
		ASMBlock asmBlock = new ASMBlock();

		return asmBlock;
	}

	@Override
	public ASMIfThenElse visit(ASTIfThenElse astIfThenElse)
	{
		ASMIfThenElse asmIfThenElse = new ASMIfThenElse();

		return new ASMIfThenElse();
	}

	@Override
	public ASMSkip visit(ASTSkip astSkip)
	{
		ASMSkip asmSkip = new ASMSkip();

		return asmSkip;
	}

	@Override
	public ASMWhile visit(ASTWhile astWhile)
	{
		ASMWhile asmWhile = new ASMWhile();

		return asmWhile;
	}

	@Override
	public ASMIntLiteral visit(ASTIntLiteral astIntLiteral)
	{
		ASMIntLiteral asmIntLiteral = new ASMIntLiteral();

		return asmIntLiteral;
	}

	@Override
	public ASMPlus visit(ASTPlus astPlus)
	{
		ASMPlus asmPlus = new ASMPlus();

		return asmPlus;
	}

	@Override
	public ASMMinus visit(ASTMinus astMinus)
	{
		ASMMinus asmMinus = new ASMMinus();

		return asmMinus;
	}

	@Override
	public ASMTimes visit(ASTTimes astTimes)
	{
		ASMTimes asmTimes = new ASMTimes();

		return asmTimes;
	}

	@Override
	public ASMDivision visit(ASTDivision astDivision)
	{
		ASMDivision asmDivision = new ASMDivision();

		return asmDivision;
	}

	@Override
	public ASMEquals visit(ASTEquals astEquals)
	{
		ASMEquals asmEquals = new ASMEquals();

		return asmEquals;
	}

	@Override
	public ASMLessThan visit(ASTLessThan astLessThan)
	{
		ASMLessThan asmLessThan = new ASMLessThan();

		return asmLessThan;
	}

	@Override
	public ASMLessThanOrEqual visit(ASTLessThanOrEqual astLessThanOrEqual)
	{
		ASMLessThanOrEqual asmLessThanOrEqual = new ASMLessThanOrEqual();

		return asmLessThanOrEqual;
	}

	@Override
	public ASMGreaterThan visit(ASTGreaterThan astGreaterThan)
	{
		ASMGreaterThan asmGreaterThan = new ASMGreaterThan();

		return asmGreaterThan;
	}

	@Override
	public ASMGreaterThanOrEqual visit(ASTGreaterThanOrEqual astGreaterThanOrEqual)
	{
		ASMGreaterThanOrEqual asmGreaterThanOrEqual = new ASMGreaterThanOrEqual();

		return asmGreaterThanOrEqual;
	}

	@Override
	public ASMAnd visit(ASTAnd astAnd)
	{
		ASMAnd asmAnd = new ASMAnd();

		return asmAnd;
	}

	@Override
	public ASMOr visit(ASTOr astOr)
	{
		ASMOr asmOr = new ASMOr();

		return asmOr;
	}

	@Override
	public ASMTrue visit(ASTTrue astTrue)
	{
		ASMTrue asmTrue = new ASMTrue();

		return asmTrue;
	}

	@Override
	public ASMFalse visit(ASTFalse astFalse)
	{
		ASMFalse asmFalse = new ASMFalse();

		return asmFalse;
	}

	@Override
	public ASMIdentifierExpr visit(ASTIdentifierExpr astIdentifierExpr)
	{
		ASMIdentifierExpr asmIdentifierExpr = new ASMIdentifierExpr();

		return asmIdentifierExpr;
	}

	@Override
	public ASMNewArray visit(ASTNewArray astNewArray)
	{
		ASMNewArray asmNewArray = new ASMNewArray();

		return asmNewArray;
	}

	@Override
	public ASMNewInstance visit(ASTNewInstance astNewInstance)
	{
		ASMNewInstance asmNewInstance = new ASMNewInstance();

		return asmNewInstance;
	}

	@Override
	public ASMCallFunctionExpr visit(ASTCallFunctionExpr astCallFunctionExpr)
	{
		ASMCallFunctionExpr asmCallFunctionExpr = new ASMCallFunctionExpr();

		return asmCallFunctionExpr;
	}

	@Override
	public ASMCallFunctionStat visit(ASTCallFunctionStat astCallFunctionStat)
	{
		ASMCallFunctionStat asmCallFunctionStat = new ASMCallFunctionStat();

		return asmCallFunctionStat;
	}


	@Override
	public ASMIntType visit(ASTIntType intType)
	{

		return new ASMIntType();
	}

	@Override
	public ASMStringType visit(ASTStringType stringType)
	{

		return new ASMStringType();
	}

	@Override
	public ASMVoidType visit(ASTVoidType voidType)
	{

		return new ASMVoidType();
	}

	@Override
	public ASMBooleanType visit(ASTBooleanType booleanType)
	{

		return new ASMBooleanType();
	}

	@Override
	public ASMIntArrayType visit(ASTIntArrayType intArrayType)
	{

		return new ASMIntArrayType();
	}

	@Override
	public ASMIdentifierType visit(ASTIdentifierType refT)
	{

		return new ASMIdentifierType();
	}

	@Override
	public ASMArgument visit(ASTArgument astArgument)
	{
		ASMArgument asmArgument = new ASMArgument();

		return asmArgument;
	}

	@Override
	public ASMIdentifier visit(ASTIdentifier astIdentifier)
	{
		ASMIdentifier asmIdentifier = new ASMIdentifier();

		return asmIdentifier;
	}

	@Override
	public ASMArrayIndexExpr visit(ASTArrayIndexExpr astArrayIndexExpr)
	{
		ASMArrayIndexExpr asmArrayIndexExpr = new ASMArrayIndexExpr();

		return asmArrayIndexExpr;
	}

	@Override
	public ASMArrayAssign visit(ASTArrayAssign astArrayAssign)
	{
		ASMArrayAssign asmArrayAssign = new ASMArrayAssign();

		return asmArrayAssign;
	}

	@Override
	public ASMStringLiteral visit(ASTStringLiteral astStringLiteral)
	{
		ASMStringLiteral asmStringLiteral = new ASMStringLiteral();

		return asmStringLiteral;
	}

	@Override
	public ASMVariable visit(ASTVariable variable)
	{
		ASMVariable asmVariable = new ASMVariable();

		asmVariable.setId(variable.getId());
		asmVariable.setType(variable.getType());

		return asmVariable;
	}

	@Override
	public ASMVariableInit visit(ASTVariableInit astVariableInit)
	{
		ASMVariableInit asmVariableInit = new ASMVariableInit();

		return asmVariableInit;
	}

	@Override
	public ASMFunction visit(ASTFunction astFunction)
	{
		ASMFunction asmFunction = new ASMFunction();

		asmFunction.setId(astFunction.getId());

		for (int i = 0; i < astFunction.getArgumentListSize(); i++) {
			asmFunction.addArgument((ASMArgument)astFunction.getArgumentDeclAt(i).accept(this));
		}

		for (int i = 0; i < astFunction.getVariableListSize(); i++) {
			asmFunction.addVariable((ASMVariable)astFunction.getVariableDeclAt(i).accept(this));
		}

		for (int i = 0; i < astFunction.getStatementListSize(); i++) {
			asmFunction.addStatement((ASMStatement)astFunction.getStatementDeclAt(i).accept(this));
		}

		return asmFunction;
	}

	@Override
	public ASMFunctionReturn visit(ASTFunctionReturn astFunctionReturn)
	{
		ASMFunctionReturn asmFunctionReturn = new ASMFunctionReturn();

		return asmFunctionReturn;
	}

	@Override
	public ASMClass visit(ASTClass astClass)
	{
		ASMClass asmClass = new ASMClass();

		return asmClass;
	}

	@Override
	public ASMInlineASM visit(ASTInlineASM astInlineASM)
	{
		ASMInlineASM asmInlineASM = new ASMInlineASM();

		return asmInlineASM;
	}

	@Override
	public ASM visit(ASTProgram astProgram)
	{
		ASMProgram asmProgram = new ASMProgram();

		for (int i = 0; i < astProgram.getVariableListSize(); i++) {
			asmProgram.addVariable((ASMVariable)astProgram.getVariableDeclAt(i).accept(this));
		}

		for (int i = 0; i < astProgram.getFunctionListSize(); i++) {
			asmProgram.addFunction((ASMFunction)astProgram.getFunctionDeclAt(i).accept(this));
		}

		return asmProgram;
	}

	@Override
	public ASMReturnStatement visit(ASTReturnStatement astReturnStatement)
	{
		ASMReturnStatement asmReturnStatement = new ASMReturnStatement();

		return asmReturnStatement;
	}

	@Override
	public ASMModulus visit(ASTModulus astModulus)
	{
		ASMModulus asmModulus = new ASMModulus();

		return asmModulus;
	}

	@Override
	public ASMForLoop visit(ASTForLoop astForLoop)
	{
		ASMForLoop asmForLoop = new ASMForLoop();

		return asmForLoop;
	}

	@Override
	public ASMPointerAssign visit(ASTPointerAssign astPointerAssign)
	{
		ASMPointerAssign asmPointerAssign = new ASMPointerAssign();

		return asmPointerAssign;
	}

	@Override
	public ASMThis visit(ASTThis astThis)
	{
		ASMThis asmThis = new ASMThis();

		return asmThis;
	}

	@Override
	public ASM visit(ASTProperty astProperty)
	{
		ASMProperty asmProperty = new ASMProperty();

		asmProperty.setType(astProperty.getType().accept(this));
		asmProperty.setId(astProperty.getId());

		return asmProperty;
	}
}