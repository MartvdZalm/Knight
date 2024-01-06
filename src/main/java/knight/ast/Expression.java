package knight.ast;

import knight.lexer.Token;

public abstract class Expression extends Tree
{
	private Type type;

	public Expression(Token token)
	{
		super(token);
	}

	public Type type()
	{
		return type;
	}

	public void setType(Type t)
	{
		type = t;
	}
};