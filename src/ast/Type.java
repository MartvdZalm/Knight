package src.ast;

import src.lexer.Token;

public abstract class Type extends Tree
{
	public Type(Token token) {
		super(token);
	}
}