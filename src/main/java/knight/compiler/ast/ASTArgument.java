package knight.compiler.ast;

import knight.compiler.ast.types.ASTType;
import knight.compiler.lexer.Token;

public class ASTArgument extends AST
{
	private ASTType type;
	private ASTIdentifier identifier;

	public ASTArgument(Token token, ASTType type, ASTIdentifier identifier)
	{
		super(token);
		this.type = type;
		this.identifier = identifier;
	}

	public ASTArgument setType(ASTType type)
	{
		this.type = type;
		return this;
	}

	public ASTType getType()
	{
		return type;
	}

	public ASTArgument setIdentifier(ASTIdentifier identifier)
	{
		this.identifier = identifier;
		return this;
	}

	public ASTIdentifier getIdentifier()
	{
		return identifier;
	}

	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}
}
