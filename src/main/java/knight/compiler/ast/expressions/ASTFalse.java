package knight.compiler.ast.expressions;

import knight.compiler.ast.ASTVisitor;
import knight.compiler.lexer.Token;

public class ASTFalse extends ASTExpression
{
	public ASTFalse(Token token)
	{
		super(token);
	}

	@Override
	public <R> R accept(ASTVisitor<R> visitor)
	{
		return visitor.visit(this);
	}
}
