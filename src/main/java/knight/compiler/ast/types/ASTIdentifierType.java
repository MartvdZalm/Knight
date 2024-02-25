package knight.compiler.ast.types;

import knight.compiler.lexer.Token;
import knight.compiler.semantics.Binding;
import knight.compiler.ast.ASTVisitor;

public class ASTIdentifierType extends ASTType
{
	private String id;
	private Binding b;

	public ASTIdentifierType(Token token, String id)
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
