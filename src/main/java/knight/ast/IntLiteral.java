package knight.ast;

import knight.lexer.Token;

public class IntLiteral extends Expression
{
	private int value;

	public IntLiteral(Token token, int value)
	{
		super(token);
		this.value = value;
	}

	public int getValue()
	{
		return value;
	}

	public void setValue(int value)
	{
		this.value = value;
	}

	@Override
	public <R> R accept(Visitor<R> v)
	{
		return v.visit(this);
	}
}