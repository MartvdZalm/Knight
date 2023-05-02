package src.ast;

import src.lexer.Token;

public class NewInstance extends Expression
{
	private IdentifierExpr className;

	public NewInstance(Token token, IdentifierExpr className)
	{
		super(token);
		this.className = className;
	}

	public IdentifierExpr getClassName()
	{
		return className;
	}

	public void setClassName(IdentifierExpr className)
	{
		this.className = className;
	}

	@Override
	public <R> R accept(Visitor<R> v)
	{
		return v.visit(this);
	}
}