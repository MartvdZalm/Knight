package knight.compiler.ast;

import knight.compiler.lexer.Token;

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
