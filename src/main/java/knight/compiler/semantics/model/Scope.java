package knight.compiler.semantics.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import knight.compiler.ast.types.ASTType;

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

		Scope currentScope = this;
		while (currentScope != null) {
			if (currentScope.variables.containsKey(name)) {
				return false;
			}
			currentScope = currentScope.parentScope;
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
