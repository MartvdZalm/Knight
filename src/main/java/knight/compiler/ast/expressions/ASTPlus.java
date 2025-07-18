package knight.compiler.ast.expressions;

import knight.compiler.ast.ASTVisitor;
import knight.compiler.lexer.Token;

public class ASTPlus extends ASTExpression
{
	private ASTExpression leftSide;
	private ASTExpression rightSide;

	public ASTPlus(Token token, ASTExpression leftSide, ASTExpression rightSide)
	{
		super(token);
		this.leftSide = leftSide;
		this.rightSide = rightSide;
	}

	public ASTExpression getLeftSide()
	{
		return leftSide;
	}

	public void setLeftSide(ASTExpression leftSide)
	{
		this.leftSide = leftSide;
	}

	public ASTExpression getRightSide()
	{
		return rightSide;
	}

	public void setRightSide(ASTExpression rightSide)
	{
		this.rightSide = rightSide;
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}
}
