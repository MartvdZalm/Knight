package knight.compiler.ast;

import knight.compiler.lexer.Token;

/*
 * File: ASTWhile.java
 * @author: Mart van der Zalm
 * Date: 2025-04-10
 */
public class ASTWhile extends ASTStatement
{
	private ASTExpression expr;
	private ASTBody body;

	public ASTWhile(Token token, ASTExpression expr, ASTBody body)
	{
		super(token);
		this.expr = expr;
		this.body = body;
	}

	public ASTExpression getExpr()
	{
		return expr;
	}

	public void setExpr(ASTExpression expr)
	{
		this.expr = expr;
	}

	public ASTBody getBody()
	{
		return body;
	}

	public void setBody(ASTBody body)
	{
		this.body = body;
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}
}
