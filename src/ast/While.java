package src.ast;

import src.lexer.Token;

public class While extends Statement
{
	private Expression expr;
	private Statement body;

	public While(Token token, Expression expr, Statement body)
	{
		super(token);
		this.expr = expr;
		this.body = body;
	}

	public Expression getExpr()
	{
		return expr;
	}

	public void setExpr(Expression expr)
	{
		this.expr = expr;
	}

	public Statement getBody()
	{
		return body;
	}

	public void setBody(Statement body)
	{
		this.body = body;
	}

	@Override
	public <R> R accept(Visitor<R> v)
	{
		return v.visit(this);
	}
}