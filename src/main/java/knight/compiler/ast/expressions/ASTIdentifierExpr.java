package knight.compiler.ast.expressions;

import knight.compiler.ast.ASTVisitor;
import knight.compiler.lexer.Token;
import knight.compiler.semantics.model.Binding;

public class ASTIdentifierExpr extends ASTExpression
{
	private String name;
	private Binding binding;

	public ASTIdentifierExpr(Token token, String name)
	{
		super(token);
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Binding getBinding()
	{
		return binding;
	}

	public void setBinding(Binding binding)
	{
		this.binding = binding;
	}

	@Override
	public <R> R accept(ASTVisitor<R> visitor)
	{
		return visitor.visit(this);
	}

	@Override
	public String toString()
	{
		return name;
	}
}
