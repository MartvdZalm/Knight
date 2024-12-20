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

import knight.compiler.asm.declarations.*;
import knight.compiler.asm.expressions.*;
import knight.compiler.asm.expressions.operations.*;
import knight.compiler.asm.statements.*;
import knight.compiler.asm.statements.conditionals.*;
import knight.compiler.asm.types.*;
import knight.compiler.asm.*;

import knight.compiler.semantics.*;
import knight.compiler.symbol.SymbolClass;
import knight.compiler.symbol.SymbolFunction;
import knight.compiler.symbol.SymbolProgram;
import knight.compiler.symbol.SymbolVariable;

/*
 * File: CodeGen.java
 * @author: Mart van der Zalm
 * Date: 2024-08-06
 * Description:
 */
public class Codegen implements ASTVisitor<ASM>
{
	private final String PATH;
	private final String FILENAME;
	private ASMStatistics statistics;

	public Codegen(String progPath, String filename)
	{
		File f = new File(filename);
		String name = f.getName();

		FILENAME = name.substring(0, name.lastIndexOf("."));
		PATH = progPath;

		this.statistics = new ASMStatistics();
	}

	// NEED TO BE REMOVED 
	@Override
	public ASM visit(ASTFunctionType functionType)
	{
		return null;
	}

	@Override
	public ASMAssign visit(ASTAssign astAssign)
	{
		ASMAssign asmAssign = new ASMAssign();
		asmAssign.setId((ASMIdentifier)astAssign.getId().accept(this));
		asmAssign.setExpr((ASMExpression)astAssign.getExpr().accept(this));
		return asmAssign;
	}

	@Override
	public ASMBlock visit(ASTBlock astBlock)
	{
		ASMBlock asmBlock = new ASMBlock();
		for (int i = 0; i < astBlock.getStatementListSize(); i++) {
			asmBlock.addStatement((ASMStatement)astBlock.getStatementAt(i).accept(this));
		}
		return asmBlock;
	}

	@Override
	public ASMIfThenElse visit(ASTIfThenElse astIfThenElse)
	{
		ASMIfThenElse asmIfThenElse = new ASMIfThenElse();
		asmIfThenElse.setExpression((ASMExpression)astIfThenElse.getExpr().accept(this));
		asmIfThenElse.setThen((ASMStatement)astIfThenElse.getThen().accept(this));
		asmIfThenElse.setElze((ASMStatement)astIfThenElse.getElze().accept(this));
		return asmIfThenElse;
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
		asmWhile.setExpression((ASMExpression)astWhile.getExpr().accept(this));
		asmWhile.setBody((ASMStatement)astWhile.getBody().accept(this));
		return asmWhile;
	}

	@Override
	public ASMIntLiteral visit(ASTIntLiteral astIntLiteral)
	{
		ASMIntLiteral asmIntLiteral = new ASMIntLiteral();
		asmIntLiteral.setValue(astIntLiteral.getValue());
		return asmIntLiteral;
	}

	@Override
	public ASMPlus visit(ASTPlus astPlus)
	{
		ASMPlus asmPlus = new ASMPlus();
		asmPlus.setLhs((ASMExpression)astPlus.getLhs().accept(this));
		asmPlus.setRhs((ASMExpression)astPlus.getRhs().accept(this));
		return asmPlus;
	}

	@Override
	public ASMMinus visit(ASTMinus astMinus)
	{
		ASMMinus asmMinus = new ASMMinus();
		asmMinus.setLhs((ASMExpression)astMinus.getLhs().accept(this));
		asmMinus.setRhs((ASMExpression)astMinus.getRhs().accept(this));
		return asmMinus;
	}

	@Override
	public ASMTimes visit(ASTTimes astTimes)
	{
		ASMTimes asmTimes = new ASMTimes();

		asmTimes.setLhs((ASMExpression)astTimes.getLhs().accept(this));
		asmTimes.setRhs((ASMExpression)astTimes.getRhs().accept(this));
		
		return asmTimes;
	}

	@Override
	public ASMDivision visit(ASTDivision astDivision)
	{
		ASMDivision asmDivision = new ASMDivision();

		asmDivision.setLhs((ASMExpression)astDivision.getLhs().accept(this));
		asmDivision.setRhs((ASMExpression)astDivision.getRhs().accept(this));	

		return asmDivision;
	}

	@Override
	public ASMModulus visit(ASTModulus astModulus)
	{
		ASMModulus asmModulus = new ASMModulus();
		asmModulus.setLhs((ASMExpression)astModulus.getLhs().accept(this));
		asmModulus.setRhs((ASMExpression)astModulus.getRhs().accept(this));
		return asmModulus;
	}

	@Override
	public ASMEquals visit(ASTEquals astEquals)
	{
		ASMEquals asmEquals = new ASMEquals();

		asmEquals.setLhs((ASMExpression)astEquals.getLhs().accept(this));
		asmEquals.setRhs((ASMExpression)astEquals.getRhs().accept(this));

		return asmEquals;
	}

	@Override
	public ASMLessThan visit(ASTLessThan astLessThan)
	{
		ASMLessThan asmLessThan = new ASMLessThan();

		asmLessThan.setLhs((ASMExpression)astLessThan.getLhs().accept(this));
		asmLessThan.setRhs((ASMExpression)astLessThan.getRhs().accept(this));

		return asmLessThan;
	}

	@Override
	public ASMLessThanOrEqual visit(ASTLessThanOrEqual astLessThanOrEqual)
	{
		ASMLessThanOrEqual asmLessThanOrEqual = new ASMLessThanOrEqual();

		asmLessThanOrEqual.setLhs((ASMExpression)astLessThanOrEqual.getLhs().accept(this));
		asmLessThanOrEqual.setRhs((ASMExpression)astLessThanOrEqual.getRhs().accept(this));

		return asmLessThanOrEqual;
	}

	@Override
	public ASMGreaterThan visit(ASTGreaterThan astGreaterThan)
	{
		ASMGreaterThan asmGreaterThan = new ASMGreaterThan();

		asmGreaterThan.setLhs((ASMExpression)astGreaterThan.getLhs().accept(this));
		asmGreaterThan.setRhs((ASMExpression)astGreaterThan.getRhs().accept(this));

		return asmGreaterThan;
	}

	@Override
	public ASMGreaterThanOrEqual visit(ASTGreaterThanOrEqual astGreaterThanOrEqual)
	{
		ASMGreaterThanOrEqual asmGreaterThanOrEqual = new ASMGreaterThanOrEqual();

		asmGreaterThanOrEqual.setLhs((ASMExpression)astGreaterThanOrEqual.getLhs().accept(this));
		asmGreaterThanOrEqual.setRhs((ASMExpression)astGreaterThanOrEqual.getRhs().accept(this));

		return asmGreaterThanOrEqual;
	}

	@Override
	public ASMAnd visit(ASTAnd astAnd)
	{
		ASMAnd asmAnd = new ASMAnd();

		asmAnd.setLhs((ASMExpression)astAnd.getLhs().accept(this));
		asmAnd.setRhs((ASMExpression)astAnd.getRhs().accept(this));

		return asmAnd;
	}

	@Override
	public ASMOr visit(ASTOr astOr)
	{
		ASMOr asmOr = new ASMOr();

		asmOr.setLhs((ASMExpression)astOr.getLhs().accept(this));
		asmOr.setRhs((ASMExpression)astOr.getRhs().accept(this));

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
		asmIdentifierExpr.setStatistics(this.statistics);
		asmIdentifierExpr.setId(astIdentifierExpr.getId().toString());
		asmIdentifierExpr.setB(astIdentifierExpr.getB());
		return asmIdentifierExpr;
	}

	@Override
	public ASMNewArray visit(ASTNewArray astNewArray)
	{
		ASMNewArray asmNewArray = new ASMNewArray();
		asmNewArray.setArrayLength((ASMExpression)astNewArray.getArrayLength().accept(this));
		return asmNewArray;
	}

	@Override
	public ASMNewInstance visit(ASTNewInstance astNewInstance)
	{
		ASMNewInstance asmNewInstance = new ASMNewInstance();
		asmNewInstance.setClassName((ASMIdentifierExpr)astNewInstance.getClassName().accept(this));
		return asmNewInstance;
	}

	@Override
	public ASMCallFunctionExpr visit(ASTCallFunctionExpr astCallFunctionExpr)
	{
		ASMCallFunctionExpr asmCallFunctionExpr = new ASMCallFunctionExpr();

		asmCallFunctionExpr.setInstanceName((ASMExpression)astCallFunctionExpr.getInstanceName().accept(this));
		asmCallFunctionExpr.setFunctionId((ASMIdentifierExpr)astCallFunctionExpr.getFunctionId().accept(this));

		for (int i = 0; i < astCallFunctionExpr.getArgExprListSize(); i++) {
			asmCallFunctionExpr.addArgExpr((ASMExpression)astCallFunctionExpr.getArgExprAt(i).accept(this));
		}

		return asmCallFunctionExpr;
	}

	@Override
	public ASMCallFunctionStat visit(ASTCallFunctionStat astCallFunctionStat)
	{
		ASMCallFunctionStat asmCallFunctionStat = new ASMCallFunctionStat();

		if (astCallFunctionStat.getInstanceName() != null) {
			asmCallFunctionStat.setInstanceName((ASMExpression)astCallFunctionStat.getInstanceName().accept(this));
		}
		asmCallFunctionStat.setFunctionName((ASMIdentifierExpr)astCallFunctionStat.getFunctionId().accept(this));

		for (int i = 0; i < astCallFunctionStat.getArgExprListSize(); i++) {
			asmCallFunctionStat.addArgExpr((ASMExpression)astCallFunctionStat.getArgExprAt(i).accept(this));
		}

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
		asmArgument.setType((ASMType)astArgument.getType().accept(this));
		asmArgument.setId((ASMIdentifier)astArgument.getId().accept(this));
		return asmArgument;
	}

	@Override
	public ASMIdentifier visit(ASTIdentifier astIdentifier)
	{
		ASMIdentifier asmIdentifier = new ASMIdentifier();
		asmIdentifier.setId(astIdentifier.getId().toString());
		asmIdentifier.setB(astIdentifier.getB());
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
		asmArrayAssign.setIdentifier((ASMIdentifier)astArrayAssign.getId().accept(this));
		asmArrayAssign.setExpression1((ASMExpression)astArrayAssign.getExpression1().accept(this));
		asmArrayAssign.setExpression2((ASMExpression)astArrayAssign.getExpression2().accept(this));
		return asmArrayAssign;
	}

	@Override
	public ASMStringLiteral visit(ASTStringLiteral astStringLiteral)
	{
		ASMStringLiteral asmStringLiteral = new ASMStringLiteral();

		return asmStringLiteral;
	}

	@Override
	public ASMVariable visit(ASTVariable astVariable)
	{
		ASMVariable asmVariable = new ASMVariable();
		asmVariable.setStatistics(this.statistics);
		asmVariable.setId((ASMIdentifier)astVariable.getId().accept(this));
		asmVariable.setType((ASMType)astVariable.getType().accept(this));
		return asmVariable;
	}

	@Override
	public ASMVariableInit visit(ASTVariableInit astVariableInit)
	{
		ASMVariableInit asmVariableInit = new ASMVariableInit();
		asmVariableInit.setStatistics(this.statistics);
		asmVariableInit.setId((ASMIdentifier)astVariableInit.getId().accept(this));
		asmVariableInit.setType((ASMType)astVariableInit.getType().accept(this));
		asmVariableInit.setExpr((ASMExpression)astVariableInit.getExpr().accept(this));
		return asmVariableInit;
	}

	@Override
	public ASMFunction visit(ASTFunction astFunction)
	{
		ASMFunction asmFunction = new ASMFunction();
		asmFunction.setStatistics(this.statistics);
		asmFunction.setId((ASMIdentifier)astFunction.getId().accept(this));

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
		asmFunctionReturn.setStatistics(this.statistics);
		asmFunctionReturn.setId((ASMIdentifier)astFunctionReturn.getId().accept(this));

		for (int i = 0; i < astFunctionReturn.getArgumentListSize(); i++) {
			asmFunctionReturn.addArgument((ASMArgument)astFunctionReturn.getArgumentDeclAt(i).accept(this));
		}

		for (int i = 0; i < astFunctionReturn.getVariableListSize(); i++) {
			asmFunctionReturn.addVariable((ASMVariable)astFunctionReturn.getVariableDeclAt(i).accept(this));
		}

		for (int i = 0; i < astFunctionReturn.getStatementListSize(); i++) {
			asmFunctionReturn.addStatement((ASMStatement)astFunctionReturn.getStatementDeclAt(i).accept(this));
		}

		asmFunctionReturn.setReturnExpr((ASMExpression)astFunctionReturn.getReturnExpr().accept(this));

		return asmFunctionReturn;
	}

	@Override
	public ASMClass visit(ASTClass astClass)
	{
		ASMClass asmClass = new ASMClass();
		asmClass.setId((ASMIdentifier)astClass.getId().accept(this));

		for (int i = 0; i < astClass.getPropertyListSize(); i++) {
			asmClass.addProperty((ASMProperty)astClass.getPropertyDeclAt(i).accept(this));
		}

		for (int i = 0; i < astClass.getFunctionListSize(); i++) {
			asmClass.addFunction((ASMFunction)astClass.getFunctionDeclAt(i).accept(this));
		}

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

		asmProgram.setFileName(FILENAME);
		asmProgram.setStatistics(this.statistics);


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
	public ASMForLoop visit(ASTForLoop astForLoop)
	{
		ASMForLoop asmForLoop = new ASMForLoop();
		return asmForLoop;
	}

	@Override
	public ASMPointerAssign visit(ASTPointerAssign astPointerAssign)
	{
		ASMPointerAssign asmPointerAssign = new ASMPointerAssign();
		asmPointerAssign.setPointer((ASMPointer)astPointerAssign.getPointer().accept(this));
		asmPointerAssign.setVariable((ASMIdentifier)astPointerAssign.getVariable().accept(this));
		asmPointerAssign.setExpression((ASMExpression)astPointerAssign.getExpression().accept(this));
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
		asmProperty.setType((ASMType)astProperty.getType().accept(this));
		asmProperty.setId((ASMIdentifier)astProperty.getId().accept(this));
		return asmProperty;
	}
}