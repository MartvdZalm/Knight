package knight.compiler.ast;

import knight.compiler.lexer.Token;

public abstract class Statement extends Tree
{
    public Statement(Token token)
	{
		super(token);
	}
}
