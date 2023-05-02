package src.ast;

import src.lexer.Token;

public class NewArray extends Expression
{
	private Expression arrayLength;

	public NewArray(Token token, Expression arrayLength)
	{
		super(token);
		this.arrayLength = arrayLength;
	}

	public Expression getArrayLength()
	{
		return arrayLength;
	}

	public void setArrayLength(Expression arrayLength)
	{
		this.arrayLength = arrayLength;
	}

	@Override
	public <R> R accept(Visitor<R> v)
	{
		return v.visit(this);
	}
}