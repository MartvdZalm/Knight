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

package knight.compiler.ast.statements;

import knight.compiler.lexer.Token;
import knight.compiler.ast.declarations.ASTIdentifier;
import knight.compiler.ast.expressions.ASTExpression;
import knight.compiler.ast.ASTVisitor;

/*
 * File: ASTArrayAssign.java
 * @author: Mart van der Zalm
 * Date: 2024-01-06
 * Description:
 */
public class ASTArrayAssign extends ASTStatement
{
	private ASTIdentifier id;
	private ASTExpression expression1;
	private ASTExpression expression2;

	public ASTArrayAssign(Token token, ASTIdentifier id, ASTExpression expression1, ASTExpression expression2)
	{
		super(token);
		this.id = id;
		this.expression1 = expression1;
		this.expression2 = expression2;
	}

	public ASTIdentifier getId()
	{
		return id;
	}

	public void setIdentifier(ASTIdentifier id)
	{
		this.id = id;
	}

	public ASTExpression getExpression1()
	{
		return expression1;
	}

	public void setE1(ASTExpression expression1)
	{
		this.expression1 = expression1;
	}

	public ASTExpression getExpression2()
	{
		return expression2;
	}

	public void setE2(ASTExpression expression2)
	{
		this.expression2 = expression2;
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}
}