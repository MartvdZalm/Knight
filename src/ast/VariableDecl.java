package src.ast;

import src.lexer.Token;

public class VariableDecl extends Tree
{
    private Type type;
	private Identifier id;

    public VariableDecl(Token token, Type type, Identifier id)
    {
        super(token);
        this.type = type;
		this.id = id;
    }

    public Type getType()
	{
		return type;
	}

    public void setType(Type type)
	{
		this.type = type;
	}

	public Identifier getId()
	{
		return id;
	}

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