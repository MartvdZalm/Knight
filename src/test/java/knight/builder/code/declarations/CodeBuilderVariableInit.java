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

package knight.builder.code.declarations;

import knight.builder.code.CodeBuilder;
import knight.builder.code.expressions.CodeBuilderExpression;
import knight.builder.code.types.CodeBuilderType;

/*
 * File: CodeBuilderVariableInit.java
 * @author: Mart van der Zalm
 * Date: 2024-02-22
 * Description:
 */
public class CodeBuilderVariableInit extends CodeBuilderVariable
{
	private CodeBuilderExpression expr;

	public CodeBuilderVariableInit()
	{
		this.mock();
	}

	@Override
	public CodeBuilderVariableInit setId(String id)
	{
		super.setId(id);

		return this;
	}

	@Override
	public CodeBuilderVariableInit setType(CodeBuilderType type)
	{
		super.setType(type);

		this.expr = this.type.getExpr();

		return this;
	}

	public CodeBuilderVariableInit setExpr(CodeBuilderExpression expr)
	{
		this.expr = expr;

		return this;
	}

	protected CodeBuilderVariableInit mock()
	{
		super.mock();

		this.expr = this.type.getExpr();

		return this;
	}

	public String toString()
	{
		return String.format("%s %s = %s;", super.type, super.id, this.expr);
	}
}