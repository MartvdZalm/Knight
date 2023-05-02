package src.ast;

import src.lexer.Token;

public class IndexArray extends Expression
{
	private Expression array;
	private Expression index;

	public IndexArray(Token token, Expression array, Expression index)
	{
		super(token);
		this.array = array;
		this.index = index;
	}

	public Expression getArray()
	{
		return array;
	}

	public void setArray(Expression array)
	{
		this.array = array;
	}

	public Expression getIndex()
	{
		return index;
	}

	public void setIndex(Expression index)
	{
		this.index = index;
	}

	@Override
	public <R> R accept(Visitor<R> v)
	{
		return v.visit(this);
	}
}