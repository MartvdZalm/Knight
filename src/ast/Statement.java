package src.ast;

import src.lexer.Token;

public abstract class Statement extends Declaration
{
    public Statement(Token token)
	{
		super(token);
	}
}
