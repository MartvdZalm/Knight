package knight.compiler.semantics.model;

import knight.compiler.ast.types.ASTIdentifierType;
import knight.compiler.ast.types.ASTType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SymbolInterface extends Binding
{
	private final String name;
	private final Map<String, SymbolFunction> functions;
	private final Map<String, String> extendedInterfaces;

	public SymbolInterface(String name)
	{
		super(new ASTIdentifierType(null, name));
		this.name = name;
		this.functions = new HashMap<>();
		this.extendedInterfaces = new HashMap<>();
	}

	public String getName()
	{
		return name;
	}

	public boolean addFunction(String name, ASTType returnType)
	{
		if (functions.containsKey(name)) {
			return false;
		}
		functions.put(name, new SymbolFunction(name, returnType));
		return true;
	}

	public boolean addExtendedInterface(String interfaceName)
	{
		if (extendedInterfaces.containsKey(interfaceName)) {
			return false;
		}
		extendedInterfaces.put(interfaceName, interfaceName);
		return true;
	}

	public Map<String, String> getAllExtendedInterfaces()
	{
		return Collections.unmodifiableMap(extendedInterfaces);
	}

	public SymbolFunction getFunction(String name)
	{
		return functions.get(name);
	}

	public Map<String, SymbolFunction> getFunctions()
	{
		return functions;
	}

	public boolean hasFunction(String name)
	{
		return functions.containsKey(name);
	}
}
