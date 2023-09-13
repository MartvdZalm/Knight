package src.ast;

import src.lexer.Token;

public class Include extends Tree
{
    private IdentifierExpr id;

    public Include(Token token, IdentifierExpr id)
    {
        super(token);
        this.id = id;
    }

	public IdentifierExpr getId()
	{
		return id;
	}

	public void setId(IdentifierExpr id)
	{
		this.id = id;
	}

    @Override
    public <R> R accept(Visitor<R> v)
    {
        return v.visit(this);
    }
}
