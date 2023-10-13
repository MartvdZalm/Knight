package src.ast;

import src.lexer.Token;

public abstract class VarDecl extends Declaration
{
    private Type type;
	private Identifier id;

    public VarDecl(Token token, Type type, Identifier id)
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
}
