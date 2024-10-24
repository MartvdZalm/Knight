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
import java.util.ArrayList;
import knight.compiler.asm.ASM;

/*
 * File: ASMProgram.java
 * @author: Mart van der Zalm
 * Date: 2024-08-06
 * Description:
 */
public class ASMProgram extends ASM
{
	private List<ASMVariable> variableList;
	private List<ASMFunction> functionList;
	private String fileName;

	public ASMProgram()
	{
		this.variableList = new ArrayList<>();
		this.functionList = new ArrayList<>();
	}

	public void addVariable(ASMVariable asmVariable)
	{
		this.variableList.add(asmVariable);
	}

	public void addFunction(ASMFunction asmFunction)
	{
		this.functionList.add(asmFunction);
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public List<ASMVariable> getVariables()
	{
		return this.variableList;
	}

	public List<ASMFunction> getFunctions()
	{
		return this.functionList;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		sb.append(".file \"" + this.fileName + ".knight\"" + ASM.NEWLINE);

		// for (ASMVariable variable : this.variableList) {
		// 	sb.append(variable);
		// }

		for (ASMFunction function : this.functionList) {
			sb.append(function).append(ASM.NEWLINE);
		}

		return sb.toString();
	}
}