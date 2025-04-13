package knight.compiler.ast;

import knight.compiler.lexer.Token;

/*
 * File: ASTStatement.java
 * @author: Mart van der Zalm
 * Date: 2025-04-10
 */
public abstract class ASTStatement extends AST
{
	public ASTStatement(Token token)
	{
		super(token);
	}
}
