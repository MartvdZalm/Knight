package src.ast;

import src.lexer.Token;

public class IntType extends Type
{
	public IntType(Token token)
	{
		super(token);
	}

	@Override
	public <R> R accept(Visitor<R> v)
	{
		return v.visit(this);
	}

	@Override
	public String toString()
	{
		return "int";
	}
}