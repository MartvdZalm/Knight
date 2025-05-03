package knight.compiler.ast;

import knight.compiler.lexer.Token;

public class ASTVariable extends AST
{
	private ASTType type;
	private ASTIdentifier id;

	public ASTVariable(Token token, ASTType type, ASTIdentifier id)
	{
		super(token);
		this.type = type;
		this.id = id;
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
		return id;
	}

	public void setId(ASTIdentifier id)
	{
		this.id = id;
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}
}
