package knight.compiler.semantics.model;

import knight.compiler.ast.types.*;

import java.util.*;

public class SymbolProgram
{
	private final Map<String, SymbolClass> classes;
	private final Map<String, SymbolInterface> interfaces;
	private final Map<String, SymbolFunction> globalFunctions;
	private final Map<String, SymbolVariable> globalVariables;

	public SymbolProgram()
	{
		this.classes = new HashMap<>();
		this.interfaces = new HashMap<>();
		this.globalFunctions = new HashMap<>();
		this.globalVariables = new HashMap<>();
	}

	public void clear()
	{
		classes.clear();
		interfaces.clear();
		globalFunctions.clear();
		globalVariables.clear();
	}

	public boolean addClass(String name, String parentClassName)
	{
		if (classes.containsKey(name)) {
			return false;
		}
		classes.put(name, new SymbolClass(name, parentClassName));
		return true;
	}

	public SymbolClass getClass(String name)
	{
		return classes.get(name);
	}

	public Map<String, SymbolClass> getClasses()
	{
		return Collections.unmodifiableMap(classes);
	}

	public boolean hasClass(String name)
	{
		return classes.containsKey(name);
	}

	public boolean addInterface(String name)
	{
		if (interfaces.containsKey(name)) {
			return false;
		}
		interfaces.put(name, new SymbolInterface(name));
		return true;
	}

	public SymbolInterface getInterface(String name)
	{
		return interfaces.get(name);
	}

	public Map<String, SymbolInterface> getInterfaces()
	{
		return Collections.unmodifiableMap(interfaces);
	}

	public boolean hasInterface(String name)
	{
		return interfaces.containsKey(name);
	}

	public boolean addGlobalFunction(String name, ASTType returnType)
	{
		if (globalFunctions.containsKey(name)) {
			return false;
		}
		globalFunctions.put(name, new SymbolFunction(name, returnType));
		return true;
	}

	public SymbolFunction getGlobalFunction(String name)
	{
		return globalFunctions.get(name);
	}

	public Map<String, SymbolFunction> getGlobalFunctions()
	{
		return Collections.unmodifiableMap(globalFunctions);
	}

	public boolean hasGlobalFunction(String name)
	{
		return globalFunctions.containsKey(name);
	}

	public boolean addGlobalVariable(String name, ASTType type)
	{
		if (globalVariables.containsKey(name)) {
			return false;
		}
		globalVariables.put(name, new SymbolVariable(name, type));
		return true;
	}

	public SymbolVariable getGlobalVariable(String name)
	{
		return globalVariables.get(name);
	}

	public Map<String, SymbolVariable> getGlobalVariables()
	{
		return Collections.unmodifiableMap(globalVariables);
	}

	public boolean hasGlobalVariable(String name)
	{
		return globalVariables.containsKey(name);
	}

	@Override
	public String toString()
	{
		return String.format("Program(\n  classes=%s,\n  interfaces=%s,\n  functions=%s,\n  globals=%s\n)",
				classes.values(), interfaces.values(), globalFunctions.values(), globalVariables.values());
	}
}
