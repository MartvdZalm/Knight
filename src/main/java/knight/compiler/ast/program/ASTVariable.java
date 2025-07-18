package knight.compiler.ast.program;

import knight.compiler.ast.AST;
import knight.compiler.ast.ASTVisitor;
import knight.compiler.ast.types.ASTType;
import knight.compiler.lexer.Token;

public class ASTVariable extends AST
{
	private ASTType type;
	private ASTIdentifier identifier;
	private boolean isStatic;

	public ASTVariable(Token token, ASTType type, ASTIdentifier identifier, boolean isStatic)
	{
		super(token);
		this.type = type;
		this.identifier = identifier;
		this.isStatic = isStatic;
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

	public boolean isStatic()
	{
		return isStatic;
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}
}
