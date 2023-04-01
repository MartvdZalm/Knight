package ast;

import lexer.Token;

public class While extends Statement
{
	public Expression expr;
	public Statement body;

	public While(Token token, Expression expr, Statement body) {
		super(token);
		this.expr = expr;
		this.body = body;
	}

	public Expression getExpr() {
		return expr;
	}

	public void setExpr(Expression expr) {
		this.expr = expr;
	}

	public Statement getBody() {
		return body;
	}

	public void setBody(Statement body) {
		this.body = body;
	}

	@Override
	public <R> R accept(Visitor<R> v) {
		return v.visit(this);
	}
}