package knight.compiler.ast;

import knight.compiler.lexer.Token;

/*
 * File: ASTStringType.java
 * @author: Mart van der Zalm
 * Date: 2025-04-10
 */
public class ASTStringType extends ASTType
{
	public ASTStringType(Token token)
	{
		super(token);
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}

	@Override
	public String toString()
	{
		return "string";
	}
}
