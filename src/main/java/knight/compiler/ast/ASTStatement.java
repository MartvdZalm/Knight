package knight.compiler.ast;

import knight.compiler.lexer.Token;

public abstract class ASTStatement extends AST
{
	public ASTStatement(Token token)
	{
		super(token);
	}
}
