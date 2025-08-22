package knight.compiler.ast.expressions;

import knight.compiler.ast.ASTVisitor;
import knight.compiler.lexer.Token;

public class ASTEquals extends ASTBinaryExpression
{
	public ASTEquals(Token token, ASTExpression left, ASTExpression right)
	{
		super(token, left, right);
	}

	@Override
	public <R> R accept(ASTVisitor<R> visitor)
	{
		return visitor.visit(this);
	}
}
