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

package knight.compiler.ast.expressions;

import knight.compiler.lexer.Token;
import knight.compiler.ast.ASTVisitor;

/*
 * File: ASTArrayIndexExpr.java
 * @author: Mart van der Zalm
 * Date: 2024-01-06
 * Description: This class represents an ArrayIndexExpr (array[index]) in the Abstract Syntax Tree (AST) of the compiler.
 */
public class ASTArrayIndexExpr extends ASTExpression
{
	private ASTExpression array;
	private ASTExpression index;

	public ASTArrayIndexExpr(Token token, ASTExpression array, ASTExpression index)
	{
		super(token);
		this.array = array;
		this.index = index;
	}

	public ASTExpression getArray()
	{
		return array;
	}

	public void setArray(ASTExpression array)
	{
		this.array = array;
	}

	public ASTExpression getIndex()
	{
		return index;
	}

	public void setIndex(ASTExpression index)
	{
		this.index = index;
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}
}