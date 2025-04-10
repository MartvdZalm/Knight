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

package knight.compiler.asm.statements;

import java.util.List;
import java.util.ArrayList;

import knight.compiler.asm.expressions.ASMExpression;
import knight.compiler.asm.expressions.ASMIdentifierExpr;
import knight.compiler.asm.expressions.ASMStringLiteral;

/*
 * File: ASMCallFunctionStat.java
 * @author: Mart van der Zalm
 * Date: 2024-08-29
 * Description:
 */
public class ASMCallFunctionStat extends ASMStatement
{
	private ASMExpression instanceName;
	private ASMIdentifierExpr functionName;
	private List<ASMExpression> argExprList;

	public ASMCallFunctionStat()
	{
		this.argExprList = new ArrayList<>();
	}

	public void setInstanceName(ASMExpression instanceName)
	{
		this.instanceName = instanceName;
	}

	public void setFunctionName(ASMIdentifierExpr functionName)
	{
		this.functionName = functionName;
	}

	public void addArgExpr(ASMExpression argExpr)
	{
		this.argExprList.add(argExpr);
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		for (int i = this.argExprList.size() - 1; i >= 6; i--) {
			sb.append("pushq " + this.argExprList.get(i) + "\n");
		}

		for (int i = Math.min(this.argExprList.size(), 6) - 1; i >= 0; i--) {
			if (this.argExprList.get(i) instanceof ASMStringLiteral) {

			} else {
				sb.append("movq " + this.argExprList.get(i) + ", %" + this.helper.getArgumentRegister(i) + "\n");
			}
		}

		return sb.toString();
	}
}