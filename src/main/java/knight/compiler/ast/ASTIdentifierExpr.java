package knight.compiler.ast;

import knight.compiler.lexer.Token;
import knight.compiler.semantics.Binding;

/*
 * File: ASTIdentifierExpr.java
 * @author: Mart van der Zalm
 * Date: 2025-04-10
 */
public class ASTIdentifierExpr extends ASTExpression
{
	private String id;
	private Binding b;

	public ASTIdentifierExpr(Token token, String id)
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
