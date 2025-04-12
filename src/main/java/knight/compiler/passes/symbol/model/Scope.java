package knight.compiler.passes.symbol.model;

import java.util.Hashtable;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import knight.compiler.ast.ASTType;

/*
 * File: Scope.java
 * @author: Mart van der Zalm
 * Date: 2025-04-12
 * Description:
 */
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
}
