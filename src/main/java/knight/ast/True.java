package knight.ast;

import knight.lexer.Token;

public class True extends Expression
{
	public True(Token token)
	{
		super(token);
	}

	@Override
	public <R> R accept(Visitor<R> v)
	{
		return v.visit(this);
	}
}