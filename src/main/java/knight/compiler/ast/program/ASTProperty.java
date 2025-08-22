package knight.compiler.ast.program;

import knight.compiler.ast.AST;
import knight.compiler.ast.ASTVisitor;
import knight.compiler.ast.expressions.ASTExpression;
import knight.compiler.ast.types.ASTType;
import knight.compiler.lexer.Token;

public class ASTProperty extends AST
{
	private ASTType type;
	private ASTIdentifier identifier;
	private ASTExpression expression;
	private boolean isStatic;

	public ASTProperty(Token token, ASTType type, ASTIdentifier identifier, ASTExpression expression, boolean isStatic)
	{
		super(token);
		this.type = type;
		this.identifier = identifier;
		this.expression = expression;
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

	public ASTIdentifier getIdentifier()
	{
		return identifier;
	}

	public void setIdentifier(ASTIdentifier identifier)
	{
		this.identifier = identifier;
	}

	public ASTExpression getExpression()
	{
		return expression;
	}

	public void setExpression(ASTExpression expression)
	{
		this.expression = expression;
	}

	public boolean isStatic()
	{
		return isStatic;
	}

	@Override
	public <R> R accept(ASTVisitor<R> visitor)
	{
		return visitor.visit(this);
	}
}
