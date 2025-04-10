package knight.compiler.ast;

import knight.compiler.lexer.Token;

/*
 * File: ASTReturnStatement.java
 * @author: Mart van der Zalm
 * Date: 2025-04-10
 */
public class ASTReturnStatement extends ASTStatement
{
	private ASTExpression returnExpr;

	public ASTReturnStatement(Token token, ASTExpression returnExpr)
	{
		super(token);
		this.returnExpr = returnExpr;
	}

	public ASTExpression getReturnExpr()
	{
		return returnExpr;
	}

	public void setReturnExpr(ASTExpression returnExpr)
	{
		this.returnExpr = returnExpr;
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}
}
