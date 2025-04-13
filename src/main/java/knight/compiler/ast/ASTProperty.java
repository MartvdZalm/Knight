package knight.compiler.ast;

import knight.compiler.lexer.Token;

/*
 * File: ASTProperty.java
 * @author: Mart van der Zalm
 * Date: 2025-04-10
 */
public class ASTProperty extends AST
{
	private ASTType type;
	private ASTIdentifier identifier;

	public ASTProperty(Token token, ASTType type, ASTIdentifier identifier)
	{
		super(token);
		this.type = type;
		this.identifier = identifier;
	}

	public ASTType getType()
	{
		return type;
	}

	public void setType(ASTType type)
	{
		this.type = type;
	}

	public ASTIdentifier getId()
	{
		return identifier;
	}

	public void setId(ASTIdentifier identifier)
	{
		this.identifier = identifier;
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}
}