package knight.compiler.ast;

import knight.compiler.lexer.Token;

public abstract class ASTType extends AST
{
	public ASTType(Token token)
	{
		super(token);
	}
}