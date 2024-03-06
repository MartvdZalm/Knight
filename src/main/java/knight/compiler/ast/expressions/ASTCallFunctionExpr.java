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

import java.util.List;

import knight.compiler.lexer.Token;
import knight.compiler.ast.ASTVisitor;

/*
 * File: ASTCallFunctionExpr.java
 * @author: Mart van der Zalm
 * Date: 2024-01-06
 * Description: This class represents a CallFunctionExpr (int num = functionName()) in the Abstract Syntax Tree (AST) of the compiler.
 */
public class ASTCallFunctionExpr extends ASTExpression
{
	private ASTExpression instanceName;
	private ASTIdentifierExpr methodId;
	private List<ASTExpression> argExprList;

	public ASTCallFunctionExpr(Token token, ASTExpression instanceName, ASTIdentifierExpr methodId, List<ASTExpression> argExprList)
	{
		super(token);
		this.instanceName = instanceName;
		this.methodId = methodId;
		this.argExprList = argExprList;
	}

	public ASTExpression getInstanceName()
	{
		return instanceName;
	}

	public void setInstanceName(ASTExpression instanceName)
	{
		this.instanceName = instanceName;
	}

	public ASTIdentifierExpr getMethodId()
	{
		return methodId;
	}

	public void setMethodId(ASTIdentifierExpr methodId)
	{
		this.methodId = methodId;
	}

	public int getArgExprListSize()
	{
		return argExprList.size();
	}

	public ASTExpression getArgExprAt(int index)
	{
		if (index < argExprList.size()) {
			return argExprList.get(index);
		}
		return null;
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}
}