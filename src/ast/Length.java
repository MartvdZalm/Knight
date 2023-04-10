package src.ast;

import src.lexer.Token;

public class Length extends Expression
{
	public Expression array;

	public Length(Token token, Expression array) {
		super(token);
		this.array = array;
	}

	public Expression getArray() {
		return array;
	}

	public void setArray(Expression array) {
		this.array = array;
	}

	@Override
	public <R> R accept(Visitor<R> v) {
		return v.visit(this);
	}
}
