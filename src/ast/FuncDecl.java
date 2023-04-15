package src.ast;

import src.lexer.Token;

public abstract class FuncDecl extends Tree
{
	public FuncDecl(Token token)
	{
		super(token);
	}
}