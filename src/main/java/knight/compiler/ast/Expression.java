package knight.compiler.ast;

import knight.compiler.lexer.Token;

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