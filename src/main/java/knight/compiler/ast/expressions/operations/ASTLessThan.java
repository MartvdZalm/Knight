package knight.compiler.ast.expressions.operations;

import knight.compiler.lexer.Token;
import knight.compiler.ast.expressions.ASTExpression;
import knight.compiler.ast.ASTVisitor;

public class ASTLessThan extends ASTExpression
{
	private ASTExpression lhs;
	private ASTExpression rhs;

	public ASTLessThan(Token token, ASTExpression lhs, ASTExpression rhs)
	{
		super(token);
		this.lhs = lhs;
		this.rhs = rhs;
	}

	public ASTExpression getLhs()
	{
		return lhs;
	}

	public void setLhs(ASTExpression lhs)
	{
		this.lhs = lhs;
	}

	public ASTExpression getRhs()
	{
		return rhs;
	}

	public void setRhs(ASTExpression rhs)
	{
		this.rhs = rhs;
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}
}
