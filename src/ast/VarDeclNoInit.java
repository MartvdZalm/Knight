package src.ast;

import src.lexer.Token;

public class VarDeclNoInit extends VarDecl
{
	public VarDeclNoInit(Token token, Type type, Identifier id)
	{
		super(token, type, id);
	}

	@Override
	public <R> R accept(Visitor<R> v)
	{
		return v.visit(this);
	}
}