package src.ast;

import src.lexer.Token;

public class ArgDecl extends VarDecl
{
	public ArgDecl(Token token, Type type, Identifier id)
	{
		super(token, type, id);
	}
	
	@Override
	public Type getType()
	{ 
		return type; 
	}
	
	@Override
	public void setType(Type type)
	{
		this.type = type;
	}
	
	@Override
	public Identifier getId()
	{
		return id;
	}
	
	@Override
	public void setId(Identifier id)
	{
		this.id = id;
	}

	@Override
	public <R> R accept(Visitor<R> v)
	{
		return v.visit(this);
	}
}