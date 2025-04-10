package knight.compiler.ast;

import knight.compiler.lexer.Token;

/*
 * File: ASTFalse.java
 * @author: Mart van der Zalm
 * Date: 2025-04-10
 */
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