package knight.compiler.ast.statements;

import knight.compiler.lexer.Token;
import knight.compiler.ast.ASTVisitor;

public class ASTSkip extends ASTStatement
{
	public ASTSkip(Token token)
	{
		super(token);
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}
}
