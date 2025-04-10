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

package knight.builder.code.statements;

import knight.builder.code.expressions.CodeBuilderExpression;

/*
 * File: CodeBuilderPointerAssign.java
 * @author: Mart van der Zalm
 * Date: 2024-05-26
 * Description:
 */
public class CodeBuilderPointerAssign extends CodeBuilderStatement
{
	private String pointer;
	private String variable;
	private CodeBuilderExpression expression;

	public CodeBuilderPointerAssign()
	{
		this.mock();
	}

	public CodeBuilderPointerAssign setPointer(String pointer)
	{
		this.pointer = pointer;

		return this;
	}

	public CodeBuilderPointerAssign setVariable(String variable)
	{
		this.variable = variable;

		return this;
	}

	public CodeBuilderPointerAssign setExpression(CodeBuilderExpression expression)
	{
		this.expression = expression;

		return this;
	}

	protected CodeBuilderPointerAssign mock()
	{
		this.pointer = "this"; // For Testing, needs to be changed.
		this.variable = super.random.identifier();
		this.expr = super.random.expression();

		return this;
	}

	public String toString()
	{
		return String.format("%s->%s = %s;", this.pointer, this.variable, this.expression);
	}
}