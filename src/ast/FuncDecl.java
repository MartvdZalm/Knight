package src.ast;

import src.lexer.Token;

public abstract class FuncDecl extends Tree
{
	public FuncDecl(Token token)
	{
		super(token);
	}

	public Type getReturnType()
	{
		return null;
	}

	public int getArgListSize()
	{
		return 0;
	}

	public ArgDecl getArgDeclAt(int index)
	{
		return null;
	}

}