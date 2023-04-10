package src.ast;

import src.lexer.Token;

public class Println extends Statement
{
    public Expression expression;

	public Println(Token token, Expression expression)
	{
		super(token);
		this.expression = expression;
	}

	public Expression getExpr()
	{
		return expression;
	}

	public void setExpr(Expression expression)
	{
		this.expression = expression;
	}

	@Override
	public <R> R accept(Visitor<R> v)
	{
		return v.visit(this);
	}    
}
