package knight.compiler.ast;

import knight.compiler.lexer.Token;

public abstract class Type extends Tree
{
	public Type(Token token)
	{
		super(token);
	}
}