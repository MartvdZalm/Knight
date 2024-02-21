package knight.compiler.ast;

import knight.compiler.lexer.Token;

public class FunctionType extends Type
{
    public FunctionType(Token token)
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
		return "FUNC";
	}
}
