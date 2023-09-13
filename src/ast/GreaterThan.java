package src.ast;

import src.lexer.Token;

public class GreaterThan extends Expression
{
    private Expression lhs;
	private Expression rhs;

    public GreaterThan(Token token, Expression lhs, Expression rhs)
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
