package knight.compiler.ast;

import knight.compiler.lexer.Token;

public class ASTTrue extends ASTExpression
{
	public ASTTrue(Token token)
	{
		super(token);
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}
}
