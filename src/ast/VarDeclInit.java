package src.ast;

import src.lexer.Token;

public class VarDeclInit extends VarDecl
{
	private Expression expr;

    public VarDeclInit(Token token, Type type, Identifier id, Expression expr, Token access)
	{
		super(token, type, id, access);
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
