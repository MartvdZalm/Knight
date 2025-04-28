package knight.compiler.ast;

import knight.compiler.lexer.Token;

/*
 * File: ASTStringArrayType.java
 * @author: Mart van der Zalm
 * Date: 2025-04-28
 */
public class ASTStringArrayType extends ASTType
{
	public ASTStringArrayType(Token token)
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
		return "string[]";
	}
}
