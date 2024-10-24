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

import knight.compiler.asm.expressions.ASMExpression;
import knight.compiler.semantics.Binding;
import knight.compiler.asm.expressions.ASMCallFunctionExpr;
import knight.compiler.asm.expressions.ASMIdentifierExpr;
import knight.compiler.asm.expressions.operations.ASMPlus;
import knight.compiler.asm.expressions.operations.ASMTimes;

/*
 * File: ASMVariableInit.java
 * @author: Mart van der Zalm
 * Date: 2024-08-29
 * Description:
 */
public class ASMVariableInit extends ASMVariable
{
	private ASMExpression expression;

	public ASMVariableInit()
	{

	}

	public void setExpr(ASMExpression expression)
	{
		this.expression = expression;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		if (statistics.currentFunction !=  null) {
			Binding b = this.id.getB();
			statistics.setLocalVarIndex(b);

			int lvIndex = statistics.getLocalVarIndex(b);

			// Need to be changed
			if (this.expression instanceof ASMCallFunctionExpr) {
				sb.append(this.expression);
				sb.append("movq %rax, " + (lvIndex * 8) + "(%rbp)\n");
			} else if (this.expression instanceof ASMIdentifierExpr) {
				sb.append("movq " + this.expression + ", %rax\n");
				sb.append("movq %rax, " + (lvIndex * 8) + "(%rbp)\n");
			} else if (
				this.expression instanceof ASMPlus ||
				this.expression instanceof ASMTimes
			) {
				sb.append(this.expression);
				sb.append("movq %rax, -" + (lvIndex * 8) + "(%rbp)\n");
			} else {
				sb.append("movq " + this.expression + ", -" + (lvIndex * 8) + "(%rbp)\n");
			}
		}

		return sb.toString();
	}
}