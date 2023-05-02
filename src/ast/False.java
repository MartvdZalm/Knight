package src.ast;

import src.lexer.Token;

public class False extends Expression 
{
	public False(Token token)
	{
		super(token);
	}

	@Override
	public <R> R accept(Visitor<R> v)
	{
		return v.visit(this);
	}
}