package knight.compiler.ast;

import knight.compiler.lexer.Token;

public class VoidType extends Type
{
    public VoidType(Token token)
	{
		super(token);
	}

	@Override
	public <R> R accept(Visitor<R> v)
	{
		return v.visit(this);
	}

	@Override
	public String toString()
	{
		return "void";
	}
}
