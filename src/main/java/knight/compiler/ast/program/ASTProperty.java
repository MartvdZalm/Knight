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

	public ASTIdentifier getId()
	{
		return identifier;
	}

	public void setId(ASTIdentifier identifier)
	{
		this.identifier = identifier;
	}

	public void setExpression(ASTExpression expression)
	{
		this.expression = expression;
	}

	public ASTExpression getExpression()
	{
		return expression;
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