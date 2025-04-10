package knight.compiler.ast;

import knight.compiler.lexer.Token;

/*
 * File: ASTThis.java
 * @author: Mart van der Zalm
 * Date: 2025-04-10
 */
public class ASTThis extends AST implements ASTPointer
{
	public ASTThis(Token token)
	{
		super(token);
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}
}