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

package knight.builder.code.expressions;

/*
 * File: CodeBuilderBooleanLiteral.java
 * @author: Mart van der Zalm
 * Date: 2024-03-29
 * Description:
 */
public class CodeBuilderBooleanLiteral extends CodeBuilderExpression
{
	private boolean bool;

	public CodeBuilderBooleanLiteral()
	{
		this.mock();
	}

	public CodeBuilderBooleanLiteral setBool(boolean bool)
	{
		this.bool = bool;

		return this;
	}

	protected CodeBuilderBooleanLiteral mock()
	{
		this.bool = super.random.bool();

		return this;
	}

	public String toString()
	{
		return String.valueOf(bool);
	}
}