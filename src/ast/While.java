package src.ast;

import src.lexer.Token;

public class While extends StatementDecl
{
	private Expression expr;
	private StatementDecl body;

	public While(Token token, Expression expr, StatementDecl body)
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

	public StatementDecl getBody()
	{
		return body;
	}

	public void setBody(StatementDecl body)
	{
		this.body = body;
	}

	@Override
	public <R> R accept(Visitor<R> v)
	{
		return v.visit(this);
	}
}