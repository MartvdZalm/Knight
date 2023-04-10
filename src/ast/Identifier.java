package src.ast;

import src.lexer.Token;
import src.semantics.Binding;

public class Identifier extends Tree
{
	public String varID;
	public Binding b;

	public Identifier(Token token, String varID) {
		super(token);
		this.varID = varID;
	}

	public String getVarID() {
		return varID;
	}

	public void setVarID(String varID) {
		this.varID = varID;
	}

	public Binding getB() {
		return b;
	}

	public void setB(Binding b) {
		this.b = b;
	}

	@Override
	public <R> R accept(Visitor<R> v) {
		return v.visit(this);
	}

	@Override
	public String toString() {
		return varID;
	}
}