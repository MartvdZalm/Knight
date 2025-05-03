package knight.compiler.ast;

import knight.compiler.lexer.Token;

public class ASTModulus extends ASTExpression
{
	private ASTExpression leftSide;
	private ASTExpression rightSide;

	public ASTModulus(Token token, ASTExpression leftSide, ASTExpression rightSide)
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
