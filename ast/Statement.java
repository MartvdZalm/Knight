package ast;

import lexer.Token;

public abstract class Statement extends Tree
{
    public Statement(Token token) {
		super(token);
	}
}
