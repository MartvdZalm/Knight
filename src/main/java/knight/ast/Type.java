package knight.ast;

import knight.lexer.Token;

public abstract class Type extends Tree
{
	public Type(Token token)
	{
		super(token);
	}
}