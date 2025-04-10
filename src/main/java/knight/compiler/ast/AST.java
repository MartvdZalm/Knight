package knight.compiler.ast;

import knight.compiler.lexer.Token;

/*
 * File: AST.java
 * @author: Mart van der Zalm
 * Date: 2025-04-10
 */
public abstract class AST
{
	private Token token;

	public AST(Token token)
	{
		this.token = token;
	}

	public Token getToken()
	{
		return token;
	}

	public void setToken(Token token)
	{
		this.token = token;
	}

	public abstract <R> R accept(ASTVisitor<R> v);
}
