package knight.compiler.ast;

import knight.compiler.lexer.Token;

public class Assign extends Statement
{
	private Identifier id;
	private Expression expr;

	public Assign(Token token, Identifier id, Expression src)
	{
		super(token);
		this.id = id;
		this.expr = src;
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

	public void setExpr(Expression expr)
	{
		this.expr = expr;
	}

	@Override
	public <R> R accept(Visitor<R> v)
	{
		return v.visit(this);
	}
}
