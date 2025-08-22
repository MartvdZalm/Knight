package knight.compiler.ast.program;

import knight.compiler.ast.AST;
import knight.compiler.ast.ASTVisitor;
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

	public void setType(ASTType type)
	{
		this.type = type;
	}

	public ASTType getType()
	{
		return type;
	}

	public void setIdentifier(ASTIdentifier identifier)
	{
		this.identifier = identifier;
	}

	public ASTIdentifier getIdentifier()
	{
		return identifier;
	}

	public <R> R accept(ASTVisitor<R> visitor)
	{
		return visitor.visit(this);
	}
}
