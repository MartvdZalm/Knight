package ast;

import lexer.Token;

public abstract class Type extends Tree
{
	public Type(Token token) {
		super(token);
	}

}