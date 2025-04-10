package knight.compiler.ast;

import knight.compiler.lexer.Token;

/*
 * File: ASTType.java
 * @author: Mart van der Zalm
 * Date: 2025-04-10
 */
public abstract class ASTType extends AST
{
	public ASTType(Token token)
	{
		super(token);
	}
}