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

package knight.builder.code;

/*
 * File: CodeBuilderIfThenElse.java
 * @author: Mart van der Zalm
 * Date: 2024-01-06
 * Description:
 */
public class CodeBuilderIfThenElse extends CodeBuilderStatement
{
	private CodeBuilderExpression expr;
	private CodeBuilderStatement then;
	private CodeBuilderStatement elze;

	public CodeBuilderIfThenElse()
	{
		this.mock();
	}

	public CodeBuilderIfThenElse setExpr(CodeBuilderExpression expr)
	{
		this.expr = expr;

		return this;
	}

	public CodeBuilderIfThenElse setThen(CodeBuilderStatement then)
	{
		this.then = then;

		return this;
	}

	public CodeBuilderIfThenElse setElse(CodeBuilderStatement elze)
	{
		this.elze = elze;

		return this;
	}

	protected CodeBuilderIfThenElse mock()
	{
		this.expr = new CodeBuilderLessThan()
							.setLhs(new CodeBuilderIdentifierExpr().setId("a"))
							.setRhs(new CodeBuilderIntLiteral().setValue(10));

		this.then = super.random.statement();
		this.elze = super.random.statement();

		return this;
	}

	public String toString()
	{
		return String.format("if (%s) {\n %s \n} else {\n %s \n}\n", this.expr, this.then, this.elze);
	} 
}