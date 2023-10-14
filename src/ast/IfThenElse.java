package src.ast;

import src.lexer.Token;

public class IfThenElse extends StatementDecl
{
	private Expression expr;
	private StatementDecl then;
	private StatementDecl elze; 

	public IfThenElse(Token token, Expression expr, StatementDecl then, StatementDecl elze)
	{
		super(token);
		this.expr = expr;
		this.then = then;
		this.elze = elze;
	}

	public Expression getExpr()
	{
		return expr;
	}

	public void setExpr(Expression expr)
	{
		this.expr = expr;
	}

	public StatementDecl getThen()
	{
		return then;
	}

	public void setThen(StatementDecl then)
	{
		this.then = then;
	}

	public StatementDecl getElze()
	{
		return elze;
	}

	public void setElze(StatementDecl elze)
	{
		this.elze = elze;
	}

	@Override
	public <R> R accept(Visitor<R> v)
	{
		return v.visit(this);
	}
}