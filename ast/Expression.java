package ast;

import lexer.Token;

public abstract class Expression extends Tree
{
	public Type type;

	public Expression(Token token) {
		super(token);
	}

	public Type type() {
		return type;
	}

	public void setType(Type t) {
		type = t;
	}
};