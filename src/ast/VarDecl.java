package src.ast;

import src.lexer.Token;

public abstract class VarDecl extends Declaration
{
    private Type type;
	private Identifier id;
	private Token access;

    public VarDecl(Token token, Type type, Identifier id, Token access)
    {
        super(token);
        this.type = type;
		this.id = id;
		this.access = access;
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

	public Token getAccess()
	{
		return access;
	}

	public void setAccess(Token access)
	{
		this.access = access;
	}
}
