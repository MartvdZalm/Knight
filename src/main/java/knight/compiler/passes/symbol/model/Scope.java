package knight.compiler.passes.symbol.model;

import java.util.Hashtable;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/*
 * File: Scope.java
 * @author: Mart van der Zalm
 * Date: 2025-04-12
 * Description:
 */
public class Scope
{
	private Hashtable<String, SymbolVariable> variables;
	private List<Scope> children;
	private Scope parent;

	public Scope(Scope parent)
	{
		this.variables = new Hashtable<>();
		this.children = new ArrayList<>();
		this.parent = parent;
	}

	public Scope addChild()
	{
		Scope scope = new Scope(this);
		children.add(scope);
		return scope;
	}

	public void addVariable(String id, SymbolVariable variable)
	{
		variables.put(id, variable);
	}

	public SymbolVariable getVariable(String id)
	{
		if (contains(id)) {
			return variables.get(id);
		} else if (parent != null) {
			return parent.getVariable(id);
		}
		return null;
	}

	public boolean contains(String id)
	{
		return variables.containsKey(id);
	}

	public Scope getParent()
	{
		return parent;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		toString(sb, 0);
		return sb.toString();
	}

	public void toString(StringBuilder sb, int level)
	{
		String indent = "\t".repeat(level);

		sb.append(indent).append("Scope Level ").append(level).append(":\n");

		for (SymbolVariable variable : variables.values()) {
			sb.append(indent).append("\t").append(variable.getId()).append(" -> ").append(variable.getType())
					.append("\n");
		}

		for (Scope scope : children) {
			scope.toString(sb, level + 1);
		}
	}

}
