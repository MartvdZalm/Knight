package knight.compiler.ast;

import knight.compiler.lexer.Token;

/*
 * File: ASTIntType.java
 * @author: Mart van der Zalm
 * Date: 2024-01-06
 * Description:
 */
public class ASTIntType extends ASTType
{
	public ASTIntType(Token token)
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
		return "int";
	}
}
