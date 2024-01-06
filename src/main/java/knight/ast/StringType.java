package knight.ast;

import knight.lexer.Token;

public class StringType extends Type
{
	public StringType(Token token)
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
		return ".string";
	}
}