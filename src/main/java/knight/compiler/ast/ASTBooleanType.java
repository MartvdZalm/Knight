package knight.compiler.ast;

import knight.compiler.lexer.Token;

/*
 * File: ASTBooleanType.java
 * @author: Mart van der Zalm
 * Date: 2025-04-10
 */
public class ASTBooleanType extends ASTType
{
	public ASTBooleanType(Token token)
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
		return "bool";
	}
}
