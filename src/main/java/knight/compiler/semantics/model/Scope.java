package knight.compiler.semantics.model;

import knight.compiler.ast.types.ASTType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Scope
{
	private final Scope parentScope;
	private final Map<String, SymbolVariable> variables;

	public Scope(Scope parentScope)
	{
		this.parentScope = parentScope;
		this.variables = new HashMap<>();
	}

	public boolean addVariable(String name, ASTType type)
	{
		if (variables.containsKey(name)) {
			return false;
		}
		variables.put(name, new SymbolVariable(name, type));
		return true;
	}

	public SymbolVariable getVariable(String name)
	{
		SymbolVariable variable = variables.get(name);
		if (variable != null) {
			return variable;
		}
		return parentScope != null ? parentScope.getVariable(name) : null;
	}

	public boolean containsVariable(String name)
	{
		return variables.containsKey(name);
	}

	public Scope getParentScope()
	{
		return parentScope;
	}

	public Map<String, SymbolVariable> getVariables()
	{
		return Collections.unmodifiableMap(variables);
	}

	public int getVariableCount()
	{
		return variables.size();
	}
}
