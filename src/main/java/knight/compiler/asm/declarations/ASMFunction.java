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

package knight.compiler.asm.declarations;

import java.io.*;
import java.util.List;
import java.util.ArrayList;

import knight.compiler.asm.ASM;
import knight.compiler.asm.statements.ASMStatement;
import knight.compiler.asm.declarations.ASMVariable;
import knight.compiler.asm.declarations.ASMArgument;

/*
 * File: ASMFunction.java
 * @author: Mart van der Zalm
 * Date: 2024-08-04
 * Description:
 */
public class ASMFunction extends ASMProgram
{
	protected ASMIdentifier id;
	protected List<ASMArgument> argumentList;
	protected List<ASMVariable> variableList;
	protected List<ASMStatement> statementList;

	public ASMFunction()
	{
		this.argumentList = new ArrayList<>();
		this.variableList = new ArrayList<>();
		this.statementList = new ArrayList<>();
	}

	public void setId(ASMIdentifier id)
	{
		this.id = id;
	}

	public ASMIdentifier getId()
	{
		return this.id;
	}

	public void addArgument(ASMArgument asmArgument)
	{
		this.argumentList.add(asmArgument);
	}

	public void addVariable(ASMVariable asmVariable)
	{
		this.variableList.add(asmVariable);
	}

	public void addStatement(ASMStatement asmStatement)
	{
		this.statementList.add(asmStatement);
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		sb.append(".globl " + this.id + ASM.NEWLINE);
		sb.append(".type " + this.id +  ", @function" + ASM.NEWLINE);
		sb.append(this.id + ":" + ASM.NEWLINE);
		sb.append("pushq %rbp" + ASM.NEWLINE);
		sb.append("movq %rsp, %rbp" + ASM.NEWLINE);

		for (int i = 0; i < Math.min(this.argumentList.size(), 6); i++) {
	        sb.append("movq %" + this.helper.getArgumentRegister(i) + ", -" + this.helper.getLocalVariableReference(i) + "\n");
	    }

	    for (int i = 6; i < this.argumentList.size(); i++) {
	        sb.append("movq " + (i - 6) * 8 + "(%rbp), -" + this.helper.getLocalVariableReference(i) + "\n");
	    }


		// for (ASMArgument asmArgument : this.argumentList) {
		// 	sb.append(asmArgument).append(ASM.NEWLINE);
		// }

		for (ASMVariable asmVariable : this.variableList) {
			sb.append(asmVariable).append(ASM.NEWLINE);
		}

		for (ASMStatement asmStatement : this.statementList) {
			sb.append(asmStatement).append(ASM.NEWLINE);
		}

		sb.append("movq %rbp, %rsp" + ASM.NEWLINE);
		sb.append("pop %rbp" + ASM.NEWLINE);
		sb.append("ret" + ASM.NEWLINE);

		return sb.toString();
	}
}