package src.ast;

import src.lexer.Token;

public class Skip extends Statement
{
	public Skip(Token token) {
		super(token);
	};

	@Override
	public <R> R accept(Visitor<R> v) {
		return v.visit(this);
	}
};
