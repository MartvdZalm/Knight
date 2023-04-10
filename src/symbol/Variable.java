package src.symbol;

import src.ast.Type;
import src.semantics.Binding;

public class Variable extends Binding
{
	String id;
	int lvIndex = -1;

	public Variable(String id, Type type) {
		super(type);
		this.id = id;
	}

	public String id() {
		return id;
	}

	@Override
	public Type type() {
		return type;
	}

	public int getLvIndex() {
		return lvIndex;
	}

	public void setLvIndex(int lvIndex) {
		this.lvIndex = lvIndex;
	}

}