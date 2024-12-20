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
import knight.compiler.asm.expressions.ASMIdentifierExpr;
import knight.compiler.asm.expressions.ASMExpression;

/*
 * File: ASMEquals.java
 * @author: Mart van der Zalm
 * Date: 2024-08-29
 * Description:
 */
public class ASMEquals extends ASMExpression
{
	private ASMExpression lhs;
	private ASMExpression rhs;

	public ASMEquals()
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

		if (this.lhs instanceof ASMIdentifierExpr) {
			// ASMIdentifierExpr lhsExpr = (ASMIdentifierExpr) lhs;
			// Binding b = lhsExpr.getB();
			// int lvIndex = this.helper.getLocalVarIndex(b);

			// sb.append("movl " + (lvIndex * 8) + "(%rbp), %eax" + ASM.NEWLINE);
		} else {
			sb.append("movl " + this.lhs + ", %eax" + ASM.NEWLINE);
		}

		sb.append("cmpl " + this.rhs + ", %eax" + ASM.NEWLINE);
		sb.append("jne else" + ASM.NEWLINE);

		return sb.toString();
	}
}