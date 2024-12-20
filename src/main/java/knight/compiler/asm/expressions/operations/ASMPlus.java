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

package knight.compiler.asm.expressions.operations;

import knight.compiler.asm.ASM;
import knight.compiler.asm.expressions.ASMExpression;

/*
 * File: ASMPlus.java
 * @author: Mart van der Zalm
 * Date: 2024-08-29
 * Description:
 */
public class ASMPlus extends ASMExpression
{
	private ASMExpression lhs;
	private ASMExpression rhs;

	public ASMPlus()
	{

	}

	public void setLhs(ASMExpression lhs)
	{
		this.lhs = lhs;
	}

	public void setRhs(ASMExpression rhs)
	{
		this.rhs = rhs;		
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		sb.append("movq " + this.lhs + ", %rax" + ASM.NEWLINE);
		sb.append("addq " + this.rhs + ", %rax" + ASM.NEWLINE);

		return sb.toString();
	}
}