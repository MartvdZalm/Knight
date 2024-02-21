package knight.compiler.ast;

import knight.compiler.lexer.Token;

public class Times extends Expression
{
	private Expression lhs;
	private Expression rhs;

	public Times(Token token, Expression lhs, Expression rhs)
	{
		super(token);
		this.lhs = lhs;
		this.rhs = rhs;
	}

	public Expression getLhs()
	{
		return lhs;
	}

	public void setLhs(Expression lhs)
	{
		this.lhs = lhs;
	}

	public Expression getRhs()
	{
		return rhs;
	}

	public void setRhs(Expression rhs)
	{
		this.rhs = rhs;
	}

	@Override
	public <R> R accept(Visitor<R> v)
	{
		return v.visit(this);
	}
}