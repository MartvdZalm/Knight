package knight.compiler.ast;

import knight.compiler.lexer.Token;

/*
 * File: ASTIntArrayType.java
 * @author: Mart van der Zalm
 * Date: 2025-04-10
 */
public class ASTIntArrayType extends ASTType
{
	public ASTIntArrayType(Token token)
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
		return "int[]";
	}
}