package knight.compiler.semantics.model;

import knight.compiler.ast.types.ASTIdentifierType;
import knight.compiler.ast.types.ASTType;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SymbolInterface extends Binding
{
	private final String name;
	private final Map<String, SymbolFunction> functions;
	private final Set<String> extendedInterfaces;

	public SymbolInterface(String name)
	{
		super(new ASTIdentifierType(null, name));
		this.name = name;
		this.functions = new HashMap<>();
		this.extendedInterfaces = new HashSet<>();
	}

	public String getName()
	{
		return name;
	}

	public boolean addFunction(String functionName, ASTType returnType)
	{
		if (functions.containsKey(functionName)) {
			return false;
		}
		functions.put(functionName, new SymbolFunction(functionName, returnType));
		return true;
	}

	public SymbolFunction getFunction(String functionName)
	{
		return functions.get(functionName);
	}

	public Map<String, SymbolFunction> getFunctions()
	{
		return Collections.unmodifiableMap(functions);
	}

	public boolean hasFunctions(String functionName)
	{
		return functions.containsKey(functionName);
	}

	public boolean addExtendedInterface(String interfaceName)
	{
		return extendedInterfaces.add(interfaceName);
	}

	public Set<String> getExtendedInterfaces()
	{
		return Collections.unmodifiableSet(extendedInterfaces);
	}

	public boolean extendsInterface(String interfaceName)
	{
		return extendedInterfaces.contains(interfaceName);
	}

	@Override
	public String toString()
	{
		return String.format("Interface(%s, extends=%s, functions=%s)", name, extendedInterfaces, functions.values());
	}
}
