package knight.compiler.ast.expressions;

import knight.compiler.ast.ASTVisitor;
import knight.compiler.lexer.Token;

public abstract class ASTBinaryExpression extends ASTExpression
{
	private ASTExpression left;
	private ASTExpression right;

	public ASTBinaryExpression(Token token, ASTExpression left, ASTExpression right)
	{
		super(token);
		this.left = left;
		this.right = right;
	}

	public ASTExpression getLeft()
	{
		return left;
	}

	public void setLeft(ASTExpression left)
	{
		this.left = left;
	}

	public ASTExpression getRight()
	{
		return right;
	}

	public void setRight(ASTExpression right)
	{
		this.right = right;
	}

}
