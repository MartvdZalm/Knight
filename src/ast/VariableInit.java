package src.ast;

import src.lexer.Token;

public class VariableInit extends Variable
{
	private Expression expr;

    public VariableInit(Token token, Type type, Identifier id, Expression expr)
	{
		super(token, type, id);
		this.expr = expr;
	}

	public void setExpr(Expression expr)
	{
		this.expr = expr;
	}

    public Expression getExpr()
	{
		return expr;
	}

	@Override
	public <R> R accept(Visitor<R> v)
    {
		return v.visit(this);
	}
}
