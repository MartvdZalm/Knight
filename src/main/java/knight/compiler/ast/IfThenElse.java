package knight.compiler.ast;

import knight.compiler.lexer.Token;

public class IfThenElse extends Statement
{
	private Expression expr;
	private Statement then;
	private Statement elze; 

	public IfThenElse(Token token, Expression expr, Statement then, Statement elze)
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

	public Statement getThen()
	{
		return then;
	}

	public void setThen(Statement then)
	{
		this.then = then;
	}

	public Statement getElze()
	{
		return elze;
	}

	public void setElze(Statement elze)
	{
		this.elze = elze;
	}

	@Override
	public <R> R accept(Visitor<R> v)
	{
		return v.visit(this);
	}
}