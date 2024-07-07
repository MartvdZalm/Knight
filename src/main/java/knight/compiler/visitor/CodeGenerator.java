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

/*
 * File: CodeGenerator.java
 * @author: Mart van der Zalm
 * Date: 2024-01-06
 * Description:
 */
public class CodeGenerator implements ASTVisitor<String>
{
	private SymbolClass currentClass;
	private SymbolFunction currentFunction;

	private final String PATH;
	private final String FILENAME;
	
	private int bytes;

	private int localArg;
	private int localVar;

	private int stack;
	private int localStack;

	private int counter;

	StringBuilder data = new StringBuilder();
	StringBuilder bss = new StringBuilder();
	StringBuilder text = new StringBuilder();

	public CodeGenerator(String progPath, String filename)
	{
		File f = new File(filename);
		String name = f.getName();

		FILENAME = name.substring(0, name.lastIndexOf("."));
		PATH = progPath;
	}

	@Override
	public String visit(ASTAssign assign)
	{
		assign.getExpr().accept(this);

		Binding b = assign.getId().getB();
		int lvIndex = getLocalVarIndex(b);

		if (lvIndex == -1) {
			text.append("movq %rax, " + assign.getId() + "\n");
		} else {
			text.append("movq %rax, -" + (lvIndex * 8) + "(%rbp)\n");
		}

		text.append("movq $0, %rax\n");

		return null;
	}

	@Override
	public String visit(ASTBlock block)
	{
		for (int i = 0; i < block.getStatListSize(); i++) {
			block.getStatAt(i).accept(this);
		}

		return null;
	}

	@Override
	public String visit(ASTIfThenElse ifThenElse)
	{
		ifThenElse.getExpr().accept(this);
		ifThenElse.getThen().accept(this);
		text.append("jmp end\n");
		text.append("else:\n");
		ifThenElse.getElze().accept(this);
		text.append("end:\n");

		return null;
	}

	@Override
	public String visit(ASTSkip skip)
	{
		return null;
	}

	@Override
	public String visit(ASTWhile w)
	{
		text.append("while: \n");
		w.getBody().accept(this);
		w.getExpr().accept(this);

		return null;
	}

	@Override
	public String visit(ASTIntLiteral intLiteral)
	{
		StringBuilder sb = new StringBuilder();

		sb.append("$" + intLiteral.getValue());

		return sb.toString();
	}

	@Override
	public String visit(ASTPlus plus)
	{
		StringBuilder sb = new StringBuilder();

		text.append("movq " + plus.getLhs().accept(this) + ", %rax" + "\n");
		text.append("addq " + plus.getRhs().accept(this) + ", %rax" + "\n");

		return sb.toString();
	}

	@Override
	public String visit(ASTMinus minus)
	{	
		StringBuilder sb = new StringBuilder();

		text.append("movq " + minus.getLhs().accept(this) + ", %rax" + "\n");
		text.append("subq " + minus.getRhs().accept(this) + ", %rax" + "\n");

		return sb.toString();
	}

	@Override
	public String visit(ASTTimes times)
	{
		StringBuilder sb = new StringBuilder();

		text.append("movq " + times.getLhs().accept(this) + ", %rax" + "\n");
		text.append("imulq " + times.getRhs().accept(this) + ", %rax" + "\n");

		return sb.toString();
	}

	@Override
	public String visit(ASTDivision division)
	{
		StringBuilder sb = new StringBuilder();

		text.append("movq " + division.getLhs().accept(this) + ", %rax\n");
		text.append("idivq " + division.getRhs().accept(this) + "\n");

		return sb.toString();
	}

	@Override
	public String visit(ASTEquals equals)
	{
		ASTExpression lhsExpr = equals.getLhs();

		if (lhsExpr instanceof ASTIdentifierExpr) {
			ASTIdentifierExpr lhs = (ASTIdentifierExpr) lhsExpr;
			Binding b = lhs.getB();
			int lvIndex = getLocalVarIndex(b);

			text.append("movl " + (lvIndex * 8) + "(%rbp), %eax\n");
		} else {
			text.append("movl " + equals.getLhs() + ", %eax\n");
		}

		text.append("cmpl " + equals.getRhs().accept(this) + ", %eax\n");
		text.append("jne else\n");

		return null;
	}

	@Override
	public String visit(ASTLessThan lessThan)
	{
		ASTExpression lhsExpr = lessThan.getLhs();

		if (lhsExpr instanceof ASTIdentifierExpr) {
			ASTIdentifierExpr lhs = (ASTIdentifierExpr) lhsExpr;
			Binding b = lhs.getB();
			int lvIndex = getLocalVarIndex(b);

			text.append("movl " + (lvIndex * 8) + "(%rbp), %eax\n");
		} else {
			text.append("movl " + lessThan.getLhs() + ", %eax\n");
		}

		text.append("cmpl " + lessThan.getRhs().accept(this) + ", %eax\n");
		text.append("jl while\n");

		return null;
	}

	@Override
	public String visit(ASTLessThanOrEqual lessThanOrEqual)
	{
		return null;
	}

	@Override
	public String visit(ASTGreaterThan greaterThan)
	{
		return null;
	}

	@Override
	public String visit(ASTGreaterThanOrEqual greaterThanOrEqual)
	{
		return null;
	}

	@Override
	public String visit(ASTAnd n)
	{
		return null;
	}

	@Override
	public String visit(ASTOr n)
	{
		return null;
	}

	@Override
	public String visit(ASTTrue true1)
	{
		return null;
	}

	@Override
	public String visit(ASTFalse false1)
	{
		return null;
	}

	@Override
	public String visit(ASTIdentifierExpr id)
	{
		StringBuilder sb = new StringBuilder();

		Binding b = id.getB();
		int lvIndex = getLocalVarIndex(b);

		sb.append("-" + lvIndex * 8 + "(%rbp)");

		return sb.toString();
	}

	@Override
	public String visit(ASTNewArray na)
	{
		return null;
	}

	@Override
	public String visit(ASTNewInstance ni)
	{
		return null;
	}

	@Override
	public String visit(ASTCallFunctionExpr callFunctionExpr)
	{
		StringBuilder sb = new StringBuilder();

		for (int i = callFunctionExpr.getArgExprListSize() - 1; i >= 6; i--) {
			text.append("pushq " + callFunctionExpr.getArgExprAt(i).accept(this) + "\n");
		}

		for (int i = Math.min(callFunctionExpr.getArgExprListSize(), 6) - 1; i >= 0; i--) {
			text.append("movq " + callFunctionExpr.getArgExprAt(i).accept(this) + ", %" + getArgumentRegister(i) + "\n");
		}

		text.append("call " + callFunctionExpr.getMethodId() + "\n");

		if (callFunctionExpr.getArgExprListSize() > 6) {
			int stackCleanup = (callFunctionExpr.getArgExprListSize() - 6) * 8;
			text.append("addq $" + stackCleanup + ", %rsp\n");
		}

		return sb.toString();
	}

	@Override
	public String visit(ASTCallFunctionStat callFunctionStat)
	{
		StringBuilder sb = new StringBuilder();

		for (int i = callFunctionStat.getArgExprListSize() - 1; i >= 6; i--) {
			text.append("pushq " + callFunctionStat.getArgExprAt(i).accept(this) + "\n");
		}

		for (int i = Math.min(callFunctionStat.getArgExprListSize(), 6) - 1; i >= 0; i--) {
			if (callFunctionStat.getArgExprAt(i) instanceof ASTStringLiteral) {
				data.append(".LC" + counter + ":\n");
				data.append(".string " + callFunctionStat.getArgExprAt(i).accept(this) + "\n");
				text.append("movq $.LC" + counter + ", %" + getArgumentRegister(i) + "\n");
				counter++;
			} else {
				text.append("movq " + callFunctionStat.getArgExprAt(i).accept(this) + ", %" + getArgumentRegister(i) + "\n");
			}
		}

		text.append("call " + callFunctionStat.getMethodId() + "\n");

		if (callFunctionStat.getArgExprListSize() > 6) {
			int stackCleanup = (callFunctionStat.getArgExprListSize() - 6) * 8;
			text.append("addq $" + stackCleanup + ", %rsp\n");
		}

		return sb.toString();
	}

	private String getArgumentRegister(int index)
	{
	    switch (index) {
	        case 0: return "rdi";
	        case 1: return "rsi";
	        case 2: return "rdx";
	        case 3: return "rcx";
	        case 4: return "r8";
	        case 5: return "r9";
	        default: throw new IllegalArgumentException("Unsupported argument index: " + index);
	    }
	}


	@Override
	public String visit(ASTIntType intType)
	{
		return null;
	}

	@Override
	public String visit(ASTStringType stringType)
	{	
		return null;
	}

	@Override
	public String visit(ASTVoidType voidType)
	{
		return null;
	}

	@Override
	public String visit(ASTBooleanType booleanType)
	{
		return null;
	}

	@Override
	public String visit(ASTIntArrayType intArrayType)
	{
		return null;
	}

	@Override
	public String visit(ASTIdentifierType refT)
	{
		return null;
	}

	@Override
	public String visit(ASTArgument argDecl)
	{
		Binding b = argDecl.getId().getB();
		setLocalArgIndex(b);

		return null;
	}

	@Override
	public String visit(ASTIdentifier identifier)
	{
		return "\"" + identifier.getId() + "\"";
	}

	@Override
	public String visit(ASTArrayIndexExpr ia)
	{
		return null;
	}

	@Override
	public String visit(ASTArrayAssign aa)
	{
		return null;
	}

	@Override
	public String visit(ASTStringLiteral stringLiteral)
	{
		return stringLiteral.getValue();
	}

	@Override
	public String visit(ASTVariable variable)
	{
		bss.append(".lcomm " + variable.getId() + " 4\n");

		return null;
	}

	@Override
	public String visit(ASTVariableInit varDeclInit)
	{
		StringBuilder sb = new StringBuilder();

		if (currentFunction == null) {
			data.append(varDeclInit.getId() + ":\n");

			ASTExpression expr = varDeclInit.getExpr();

			data.append(expr.getType() + " " + varDeclInit.getExpr().accept(this) + "\n");
			
			if (expr instanceof ASTPlus) {
				text.append("movq %rax, " + varDeclInit.getId() + "\n");
			}
		} else {
			Binding b = varDeclInit.getId().getB();
			setLocalVarIndex(b);

			int lvIndex = getLocalVarIndex(b);

			// Need to be changed
			if (varDeclInit.getExpr() instanceof ASTCallFunctionExpr) {
				varDeclInit.getExpr().accept(this);
				text.append("movq %rax, " + (lvIndex * 8) + "(%rbp)\n");
			} else if (varDeclInit.getExpr() instanceof ASTIdentifierExpr) {
				text.append("movq " + varDeclInit.getExpr().accept(this) + ", %rax\n");
				text.append("movq %rax, " + (lvIndex * 8) + "(%rbp)\n");
			} else if (
				varDeclInit.getExpr() instanceof ASTPlus ||
				varDeclInit.getExpr() instanceof ASTTimes
			) {
				text.append(varDeclInit.getExpr().accept(this));
				text.append("movq %rax, -" + (lvIndex * 8) + "(%rbp)\n");
			} else {
				text.append("movq " + varDeclInit.getExpr().accept(this) + ", -" + (lvIndex * 8) + "(%rbp)\n");
			}
		}

		return sb.toString();
	}

	@Override
	public String visit(ASTFunction functionVoid)
	{
		localArg = 0;
	    localVar = 0;

	    currentFunction = (SymbolFunction) functionVoid.getId().getB();
	    text.append(".globl " + functionVoid.getId() + "\n");
	    text.append(".type " + functionVoid.getId() + ", @function\n");
	    text.append(functionVoid.getId() + ":\n");
	    text.append("pushq %rbp\n");
	    text.append("movq %rsp, %rbp\n");

	    for (int i = 0; i < Math.min(functionVoid.getArgumentListSize(), 6); i++) {
	    	functionVoid.getArgumentDeclAt(i).accept(this);
	        text.append("movq %" + getArgumentRegister(i) + ", -" + getLocalVariableReference(i) + "\n");
	    }

	    for (int i = 6; i < functionVoid.getArgumentListSize(); i++) {
	    	functionVoid.getArgumentDeclAt(i).accept(this);
	        text.append("movq " + (i - 6) * 8 + "(%rbp), -" + getLocalVariableReference(i) + "\n");
	    }

	    for (int i = 0; i < functionVoid.getInlineASMListSize(); i++) {
	    	functionVoid.getInlineASMAt(i).accept(this);
	    }

	    for (int i = 0; i < functionVoid.getVariableListSize(); i++) {
	        functionVoid.getVariableDeclAt(i).accept(this);
	    }

	    for (int i = 0; i < functionVoid.getStatementListSize(); i++) {
	        functionVoid.getStatementDeclAt(i).accept(this);
	    }
	    	
        text.append("movq %rbp, %rsp\n");
        text.append("pop %rbp\n");
        text.append("ret\n");
        currentFunction = null;

		return null;
	}

	@Override
	public String visit(ASTFunctionReturn functionReturn)
	{
		localArg = 0;
	    localVar = 0;

	    currentFunction = (SymbolFunction) functionReturn.getId().getB();
	    text.append(".globl " + functionReturn.getId() + "\n");
	    text.append(".type " + functionReturn.getId() + ", @function\n");
	    text.append(functionReturn.getId() + ":\n");
	    text.append("pushq %rbp\n");
	    text.append("movq %rsp, %rbp\n");

	    for (int i = 0; i < Math.min(functionReturn.getArgumentListSize(), 6); i++) {
	    	functionReturn.getArgumentDeclAt(i).accept(this);
	        text.append("movq %" + getArgumentRegister(i) + ", -" + getLocalVariableReference(i) + "\n");
	    }

	    for (int i = 6; i < functionReturn.getArgumentListSize(); i++) {
	    	functionReturn.getArgumentDeclAt(i).accept(this);
	        text.append("movq " + (i - 6) * 8 + "(%rbp), -" + getLocalVariableReference(i) + "\n");
	    }

	   	for (int i = 0; i < functionReturn.getInlineASMListSize(); i++) {
	    	functionReturn.getInlineASMAt(i).accept(this);
	    }

	    for (int i = 0; i < functionReturn.getVariableListSize(); i++) {
	        functionReturn.getVariableDeclAt(i).accept(this);
	    }

	    for (int i = 0; i < functionReturn.getStatementListSize(); i++) {
	        functionReturn.getStatementDeclAt(i).accept(this);
	    }

	    if (functionReturn.getId().getId().equals("main")) {
	        text.append("movq $60, %rax\n");
	        text.append("movq " + functionReturn.getReturnExpr().accept(this) + ", %rdi\n");
	        text.append("syscall\n");
	    } else {
	    	if (functionReturn.getReturnExpr() instanceof ASTIdentifierExpr) {
	    		text.append("movq " + functionReturn.getReturnExpr().accept(this) + ", %rax" + "\n");
	    	} else {
	    		functionReturn.getReturnExpr().accept(this);
	    	}
	    	
	        text.append("movq %rbp, %rsp\n");
	        text.append("pop %rbp\n");
	        text.append("ret\n");
	        currentFunction = null;
	    }

	    return null;
	}

	private String getLocalVariableReference(int index)
	{
	    return ((index + 1) * 8) + "(%rbp)";
	}

	@Override
	public String visit(ASTClass classDecl)
	{



		return null;
	}

	@Override
	public String visit(ASTInlineASM inlineASM)
	{
		for (int i = 0; i < inlineASM.getLinesSize(); i++) {
			String line = inlineASM.getLineAt(i);

			if (line.startsWith("\"") && line.endsWith("\"")) {
                line = line.substring(1, line.length() - 1);
            }

            text.append(line).append("\n");
		}
		return null;
	}

	@Override
	public String visit(ASTProgram program)
	{
		StringBuilder sb = new StringBuilder();

		sb.append(".file \"" + FILENAME + ".knight\"\n");
		data.append(".section .data\n");
		bss.append(".section .bss\n");
		text.append(".section .text\n");

		for (int i = 0; i < program.getVariableListSize(); i++) {
			program.getVariableDeclAt(i).accept(this);
		}

		for (int i = 0; i < program.getInlineASMListSize(); i++) {
			program.getInlineASMDeclAt(i).accept(this);
		}

		for (int i = 0; i < program.getFunctionListSize(); i++) {
			program.getFunctionDeclAt(i).accept(this);
		}

		sb.append(data).append(bss).append(text);

		write(sb.toString());

		return null;
	}

	private File write(String code)
	{
		try {
			File f = new File(PATH + FILENAME + ".s");
			PrintWriter writer = new PrintWriter(f, "UTF-8");
			writer.println(code);
			writer.close();
			return f;
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

		return null;
	}

	private int getLocalArgIndex(Binding b)
	{
		if (b != null && b instanceof SymbolVariable) {
			return ((SymbolVariable) b).getLvIndex(); 
		}
		return -1;
	}

	private int setLocalArgIndex(Binding b)
	{
		if (b != null && b instanceof SymbolVariable) {
			((SymbolVariable) b).setLvIndex(++localArg);
			return localArg;
		}
		return -1;
	}
	
	private int getLocalVarIndex(Binding b)
	{
		if (b != null && b instanceof SymbolVariable) {
			return ((SymbolVariable) b).getLvIndex();
		}
		return -1;
	}

	private int setLocalVarIndex(Binding b)
	{
		if (b != null && b instanceof SymbolVariable) {
			((SymbolVariable) b).setLvIndex(++localVar);
			return localVar;
		}
		return -1;
	}

	@Override
	public String visit(ASTReturnStatement returnStatement)
	{
		return null;
	}

	@Override
	public String visit(ASTModulus modulus)
	{
		return null;
	}

	@Override
	public String visit(ASTFunctionType functionType)
	{
		return null;
	}

	@Override
	public String visit(ASTForLoop forLoop)
	{
		return null;
	}

	@Override
	public String visit(ASTPointerAssign pointerAssign)
	{
		return null;
	}

	@Override
	public String visit(ASTThis astThis)
	{
		return null;
	}
}
