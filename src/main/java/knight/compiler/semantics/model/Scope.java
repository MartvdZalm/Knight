package knight.compiler.semantics.model;

import knight.compiler.ast.types.ASTType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Scope
{
	private final Scope parent;
	private final Map<String, SymbolVariable> variables = new HashMap<>();

	public Scope(Scope parent)
	{
		this.parent = parent;
	}

	public boolean addVariable(String name, ASTType type)
	{
		if (contains(name)) {
			return false;
		}
		variables.put(name, new SymbolVariable(name, type));
		return true;
	}

	public SymbolVariable getVariable(String name)
	{
		if (contains(name)) {
			return variables.get(name);
		} else if (parent != null) {
			return parent.getVariable(name);
		}
		return null;
	}

	public boolean contains(String name)
	{
		return variables.containsKey(name);
	}

	public Scope getParent()
	{
		return parent;
	}

	public Map<String, SymbolVariable> getAllVariables()
	{
		return Collections.unmodifiableMap(variables);
	}

	public int size()
	{
		return variables.size();
	}
}
