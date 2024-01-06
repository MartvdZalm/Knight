package knight.ast;

import knight.lexer.Token;

public class BooleanType extends Type
{
	public BooleanType(Token token)
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
		return "boolean";
	}
}