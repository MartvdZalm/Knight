package src.ast;

import src.lexer.Token;

public abstract class StatementDecl extends Tree
{
    public StatementDecl(Token token)
	{
		super(token);
	}
}
