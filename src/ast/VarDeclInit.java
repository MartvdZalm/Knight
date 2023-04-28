package src.ast;

import src.lexer.Token;

public class VarDeclInit extends Declaration
{
    Type type;
    Identifier id;
    Expression expr;

    public VarDeclInit(Token token, Type type, Identifier id, Expression expr)
    {
        super(token);
        this.type = type;
        this.id = id;
        this.expr = expr;
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

	@Override
	public <R> R accept(Visitor<R> v)
    {
		return v.visit(this);
	}
}
