package knight.compiler.ast;

import knight.compiler.lexer.Token;

/*
 * File: ASTVoidType.java
 * @author: Mart van der Zalm
 * Date: 2025-04-10
 * Description:
 */
public class ASTVoidType extends ASTType
{
	public ASTVoidType(Token token)
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
		return "void";
	}
}
