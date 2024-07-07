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
import knight.compiler.ast.expressions.ASTExpression;
import knight.compiler.ast.declarations.ASTIdentifier;
import knight.compiler.ast.ASTVisitor;
import knight.compiler.ast.ASTPointer;

/*
 * File: ASTPointerAssign.java
 * @author: Mart van der Zalm
 * Date: 2024-05-26
 * Description: this->x = x; person->age = 18;
 */
public class ASTPointerAssign extends ASTStatement
{
	private ASTPointer pointer;
	private ASTIdentifier variable;
	private ASTExpression expression;

	public ASTPointerAssign(Token token, ASTPointer pointer, ASTIdentifier variable, ASTExpression expression)
	{
		super(token);
		this.pointer = pointer;
		this.variable = variable;
		this.expression = expression;
	}

	public ASTPointer getPointer()
	{
		return pointer;
	}

	public void setPointer(ASTPointer pointer)
	{
		this.pointer = pointer;
	}

	public ASTIdentifier getVariable()
	{
		return variable;
	}

	public void setVariable(ASTIdentifier variable)
	{
		this.variable = variable;
	}

	public ASTExpression getExpression()
	{
		return expression;
	}

	public void setExpression(ASTExpression expression)
	{
		this.expression = expression;
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}
}