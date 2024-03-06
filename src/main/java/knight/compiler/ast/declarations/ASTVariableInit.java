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

package knight.compiler.ast.declarations;

import knight.compiler.lexer.Token;
import knight.compiler.ast.ASTVisitor;
import knight.compiler.ast.types.ASTType;
import knight.compiler.ast.expressions.ASTExpression;

/*
 * File: ASTVariableInit.java
 * @author: Mart van der Zalm
 * Date: 2024-01-06
 * Description: This class represents a variable with value in the Abstract Syntax Tree (AST) of the compiler.
 */
public class ASTVariableInit extends ASTVariable
{
	private ASTExpression expr;

    public ASTVariableInit(Token token, ASTType type, ASTIdentifier id, ASTExpression expr)
	{
		super(token, type, id);
		this.expr = expr;
	}

	public void setExpr(ASTExpression expr)
	{
		this.expr = expr;
	}

    public ASTExpression getExpr()
	{
		return expr;
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
    {
		return v.visit(this);
	}
}
