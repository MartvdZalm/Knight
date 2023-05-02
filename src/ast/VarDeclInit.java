package src.ast;

import src.lexer.Token;

public class VarDeclInit extends Declaration
{
    private Type type;
    private Identifier id;
    private Expression expr;
	private Token access;

    public VarDeclInit(Token token, Type type, Identifier id, Expression expr, Token access)
    {
        super(token);
        this.type = type;
        this.id = id;
        this.expr = expr;
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

    public Expression getExpr()
	{
		return expr;
	}

	public void setId(Expression expr)
	{
		this.expr = expr;
	}

	public Token getAccess()
	{
		return access;
	}

	public void setAccess(Token access)
	{
		this.access = access;
	}

	@Override
	public <R> R accept(Visitor<R> v)
    {
		return v.visit(this);
	}
}
