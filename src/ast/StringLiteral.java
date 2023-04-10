package src.ast;

import src.lexer.Token;

public class StringLiteral extends Expression
{
	public String value;

	public StringLiteral(Token token, String value) {
		super(token);
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public <R> R accept(Visitor<R> v) {
		return v.visit(this);
	}
}