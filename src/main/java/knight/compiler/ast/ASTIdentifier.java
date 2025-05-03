package knight.compiler.ast;

import knight.compiler.lexer.Token;
import knight.compiler.semantics.model.Binding;

public class ASTIdentifier extends AST
{
	private String id;
	private Binding b;

	public ASTIdentifier(Token token, String id)
	{
		super(token);
		this.id = id;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
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
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}

	@Override
	public String toString()
	{
		return id;
	}
}