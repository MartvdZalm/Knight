package knight.compiler.semantics.model;

import knight.compiler.ast.types.ASTIdentifierType;
import knight.compiler.ast.types.ASTType;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;

public class SymbolClass extends Binding
{
	private final String name;
	private final String parentClassName;
	private final Map<String, SymbolFunction> functions;
	private final Map<String, SymbolProperty> properties;
	private final Set<String> implementedInterfaces;

	public SymbolClass(String name, String parentClassName)
	{
		super(new ASTIdentifierType(null, name));
		this.name = name;
		this.parentClassName = parentClassName;
		this.functions = new HashMap<>();
		this.properties = new HashMap<>();
		this.implementedInterfaces = new HashSet<>();
	}

	public String getName()
	{
		return name;
	}

	public String getParentClassName()
	{
		return parentClassName;
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

	public boolean hasFunction(String functionName)
	{
		return functions.containsKey(functionName);
	}

	public boolean addProperty(String propertyName, ASTType type)
	{
		if (properties.containsKey(propertyName)) {
			return false;
		}
		properties.put(propertyName, new SymbolProperty(propertyName, type));
		return true;
	}

	public SymbolProperty getProperty(String propertyName)
	{
		return properties.get(propertyName);
	}

	public Map<String, SymbolProperty> getProperties()
	{
		return Collections.unmodifiableMap(properties);
	}

	public boolean hasProperty(String propertyName)
	{
		return properties.containsKey(propertyName);
	}

	public boolean addImplementedInterface(String interfaceName)
	{
		return implementedInterfaces.add(interfaceName);
	}

	public Set<String> getImplementedInterfaces()
	{
		return Collections.unmodifiableSet(implementedInterfaces);
	}

	public boolean implementsInterface(String interfaceName)
	{
		return implementedInterfaces.contains(interfaceName);
	}

	public boolean hasParentClass()
	{
		return parentClassName != null;
	}

	@Override
	public String toString()
	{
		return String.format("Class(%s%s, properties=%s, functions=%s, interfaces=%s)", name,
				parentClassName != null ? " extends " + parentClassName : "", properties.values(), functions.values(),
				implementedInterfaces);
	}
}
