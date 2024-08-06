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
	public ASM visit(ASTAssign assign)
	{

		return null;
	}

	@Override
	public ASM visit(ASTBlock block)
	{

		return null;
	}

	@Override
	public ASM visit(ASTIfThenElse ifThenElse)
	{

		return null;
	}

	@Override
	public ASM visit(ASTSkip skip)
	{

		return null;
	}

	@Override
	public ASM visit(ASTWhile w)
	{

		return null;
	}

	@Override
	public ASM visit(ASTIntLiteral intLiteral)
	{

		return null;
	}

	@Override
	public ASM visit(ASTPlus plus)
	{

		return null;
	}

	@Override
	public ASM visit(ASTMinus minus)
	{

		return null;
	}

	@Override
	public ASM visit(ASTTimes times)
	{

		return null;
	}

	@Override
	public ASM visit(ASTDivision division)
	{

		return null;
	}

	@Override
	public ASM visit(ASTEquals equals)
	{

		return null;
	}

	@Override
	public ASM visit(ASTLessThan lessThan)
	{

		return null;
	}

	@Override
	public ASM visit(ASTLessThanOrEqual lessThanOrEqual)
	{

				return null;
	}

	@Override
	public ASM visit(ASTGreaterThan greaterThan)
	{

		return null;
	}

	@Override
	public ASM visit(ASTGreaterThanOrEqual greaterThanOrEqual)
	{

		return null;
	}

	@Override
	public ASM visit(ASTAnd n)
	{

		return null;
	}

	@Override
	public ASM visit(ASTOr n)
	{

		return null;
	}

	@Override
	public ASM visit(ASTTrue true1)
	{

		return null;
	}

	@Override
	public ASM visit(ASTFalse false1)
	{

		return null;
	}

	@Override
	public ASM visit(ASTIdentifierExpr id)
	{

		return null;
	}

	@Override
	public ASM visit(ASTNewArray na)
	{

		return null;
	}

	@Override
	public ASM visit(ASTNewInstance ni)
	{

		return null;
	}

	@Override
	public ASM visit(ASTCallFunctionExpr callFunctionExpr)
	{

		return null;
	}

	@Override
	public ASM visit(ASTCallFunctionStat callFunctionStat)
	{

		return null;
	}


	@Override
	public ASM visit(ASTIntType intType)
	{

		return null;
	}

	@Override
	public ASM visit(ASTStringType stringType)
	{

		return null;
	}

	@Override
	public ASM visit(ASTVoidType voidType)
	{

		return null;
	}

	@Override
	public ASM visit(ASTBooleanType booleanType)
	{

		return null;
	}

	@Override
	public ASM visit(ASTIntArrayType intArrayType)
	{

		return null;
	}

	@Override
	public ASM visit(ASTIdentifierType refT)
	{

		return null;
	}

	@Override
	public ASM visit(ASTArgument argDecl)
	{

		return null;
	}

	@Override
	public ASM visit(ASTIdentifier identifier)
	{

		return null;
	}

	@Override
	public ASM visit(ASTArrayIndexExpr ia)
	{

		return null;
	}

	@Override
	public ASM visit(ASTArrayAssign aa)
	{

		return null;
	}

	@Override
	public ASM visit(ASTStringLiteral stringLiteral)
	{

		return null;
	}

	@Override
	public ASMVariable visit(ASTVariable variable)
	{

		return null;
	}

	@Override
	public ASMVariable visit(ASTVariableInit varDeclInit)
	{

		return null;
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
	public ASM visit(ASTFunctionReturn functionReturn)
	{

		return null;
	}

	@Override
	public ASM visit(ASTClass classDecl)
	{

		return null;
	}

	@Override
	public ASM visit(ASTInlineASM inlineASM)
	{

		return null;
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
	public ASM visit(ASTReturnStatement returnStatement)
	{

		return null;
	}

	@Override
	public ASM visit(ASTModulus modulus)
	{

		return null;
	}

	@Override
	public ASM visit(ASTFunctionType functionType)
	{

		return null;
	}

	@Override
	public ASM visit(ASTForLoop forLoop)
	{

		return null;
	}

	@Override
	public ASM visit(ASTPointerAssign pointerAssign)
	{

		return null;
	}

	@Override
	public ASM visit(ASTThis astThis)
	{

		return null;
	}

	@Override
	public ASM visit(ASTProperty property)
	{

		return null;
	}
}