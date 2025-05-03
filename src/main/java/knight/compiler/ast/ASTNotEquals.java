package knight.compiler.ast;

import knight.compiler.lexer.Token;

/*
 * File: ASTNotEquals.java
 * @author: Mart van der Zalm
 * Date: 2025-04-10
 */
public class ASTNotEquals extends ASTExpression
{
	private ASTExpression leftSide;
	private ASTExpression rightSide;

	public ASTNotEquals(Token token, ASTExpression leftSide, ASTExpression rightSide)
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
