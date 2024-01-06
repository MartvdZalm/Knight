package knight.ast;

import knight.lexer.Token;

public class Skip extends Statement
{
	public Skip(Token token)
	{
		super(token);
	}

	@Override
	public <R> R accept(Visitor<R> v)
	{
		return v.visit(this);
	}
}
