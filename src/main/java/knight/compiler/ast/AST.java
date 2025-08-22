package knight.compiler.ast;

import knight.compiler.lexer.Token;

public abstract class AST
{
	private Token token;
	private String sourceFile;

	public AST(Token token)
	{
		this.token = token;
	}

	public AST(Token token, String sourceFile)
	{
		this.token = token;
		this.sourceFile = sourceFile;
	}

	public Token getToken()
	{
		return token;
	}

	public void setToken(Token token)
	{
		this.token = token;
	}

	public String getSourceFile()
	{
		return sourceFile;
	}

	public void setSourceFile(String sourceFile)
	{
		this.sourceFile = sourceFile;
	}

	public abstract <R> R accept(ASTVisitor<R> visitor);
}
