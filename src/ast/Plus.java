package src.ast;

import src.lexer.Token;

public class Plus extends Expression
{
	public Expression lhs;
	public Expression rhs;

	public Plus(Token token, Expression lhs, Expression rhs) {
		super(token);
		this.lhs = lhs;
		this.rhs = rhs;
	}

	public Expression getLhs() {
		return lhs;
	}

	public void setLhs(Expression lhs) {
		this.lhs = lhs;
	}

	public Expression getRhs() {
		return rhs;
	}

	public void setRhs(Expression rhs) {
		this.rhs = rhs;
	}

	@Override
	public <R> R accept(Visitor<R> v) {
		return v.visit(this);
	}
}