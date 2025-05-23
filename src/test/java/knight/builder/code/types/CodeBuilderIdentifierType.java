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

package knight.builder.code.types;

import knight.builder.code.expressions.CodeBuilderIdentifierExpr;
import knight.builder.code.expressions.CodeBuilderExpression;

/*
 * File: CodeBuilderIdentifierType.java
 * @author: Mart van der Zalm
 * Date: 2024-03-29
 * Description:
 */
public class CodeBuilderIdentifierType extends CodeBuilderType
{
	private String id;

	public CodeBuilderIdentifierType()
	{
		this.mock();
	}

	public CodeBuilderIdentifierType setId(String id)
	{
		this.id = id;

		return this;
	}

	public CodeBuilderExpression getExpr()
	{
		return new CodeBuilderIdentifierExpr();
	}

	protected CodeBuilderIdentifierType mock()
	{
		this.id = super.random.className();

		return this;
	}

	public String toString()
	{
		return id;
	}
}