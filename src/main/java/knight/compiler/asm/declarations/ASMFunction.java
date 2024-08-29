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

import java.util.List;

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
public class ASMFunction extends ASM
{
	private String id;
	private List<ASMArgument> argumentList;
	private List<ASMVariable> variableList;
	private List<ASMStatement> statementList;

	public ASMFunction()
	{
		
	}

	public void setId(String id)
	{
		this.id = id;
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
}