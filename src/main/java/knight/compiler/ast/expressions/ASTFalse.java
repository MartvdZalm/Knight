package knight.compiler.ast.expressions;

import knight.compiler.lexer.Token;
import knight.compiler.ast.ASTVisitor;

public class ASTFalse extends ASTExpression 
{
	public ASTFalse(Token token)
	{
		super(token);
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}
}