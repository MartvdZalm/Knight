package knight.compiler.ast;

import knight.compiler.lexer.Token;

/*
 * File: ASTArrayIndexExpr.java
 * @author: Mart van der Zalm
 * Date: 2025-04-10
 */
public class ASTArrayIndexExpr extends ASTExpression
{
	private ASTExpression array;
	private ASTExpression index;

	public ASTArrayIndexExpr(Token token, ASTExpression array, ASTExpression index)
	{
		super(token);
		this.array = array;
		this.index = index;
	}

	public ASTExpression getArray()
	{
		return array;
	}

	public void setArray(ASTExpression array)
	{
		this.array = array;
	}

	public ASTExpression getIndex()
	{
		return index;
	}

	public void setIndex(ASTExpression index)
	{
		this.index = index;
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}
}