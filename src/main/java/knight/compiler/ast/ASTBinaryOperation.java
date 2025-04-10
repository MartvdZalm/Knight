package knight.compiler.ast;

import knight.compiler.lexer.Token;

/*
 * File: ASTBinaryOperation.java
 * @author: Mart van der Zalm
 * Date: 2025-04-10
 */
public class ASTBinaryOperation extends ASTExpression
{
	private ASTExpression leftSide;
	private ASTExpression rightSide;

	public ASTBinaryOperation(Token token)
	{
		super(token);
	}

	public ASTBinaryOperation setLeftSide(ASTExpression leftSide)
	{
		this.leftSide = leftSide;
		return this;
	}

	public ASTExpression getLeftSide()
	{
		return leftSide;
	}

	public ASTBinaryOperation setRightSide(ASTExpression rightSide)
	{
		this.rightSide = rightSide;
		return this;
	}

	public ASTExpression getRightSide()
	{
		return rightSide;
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}
}
