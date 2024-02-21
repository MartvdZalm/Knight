package knight.compiler.ast;

import knight.compiler.lexer.Token;
import knight.compiler.semantics.Binding;

public class IdentifierType extends Type
{
	private String varID;
	private Binding b;

	public IdentifierType(Token token, String varID)
	{
		super(token);
		this.varID = varID;
	}

	public String getVarID()
	{
		return varID;
	}

	public void setVarID(String varID)
	{
		this.varID = varID;
	}

	public Binding getB()
	{
		return b;
	}

	public void setB(Binding b)
	{
		this.b = b;
	}

	@Override
	public <R> R accept(Visitor<R> v)
	{
		return v.visit(this);
	}

	@Override
	public String toString()
	{
		return varID;
	}
}
