package knight.compiler.ast.statements;

import knight.compiler.ast.ASTVisitor;
import knight.compiler.ast.expressions.ASTExpression;
import knight.compiler.lexer.Token;

public class ASTReturnStatement extends ASTStatement
{
	private ASTExpression expression;

	public ASTReturnStatement(Token token, ASTExpression expression)
	{
		super(token);
		this.expression = expression;
	}

	public ASTExpression getExpression()
	{
		return expression;
	}

	public void setExpression(ASTExpression expression)
	{
		this.expression = expression;
	}

	@Override
	public <R> R accept(ASTVisitor<R> visitor)
	{
		return visitor.visit(this);
	}
}
