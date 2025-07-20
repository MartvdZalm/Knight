package knight.compiler.semantics.model;

import knight.compiler.ast.types.ASTIdentifierType;
import knight.compiler.ast.types.ASTType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SymbolClass extends Binding
{
	private final String name;
	private final Map<String, SymbolFunction> functions;
	private final Map<String, SymbolVariable> variables;
	private final String parent;
	private final Map<String, String> implementedInterfaces;

	public SymbolClass(String name, String parent)
	{
		super(new ASTIdentifierType(null, name));
		this.name = name;
		this.parent = parent;
		this.functions = new HashMap<>();
		this.variables = new HashMap<>();
		this.implementedInterfaces = new HashMap<>();
	}

	public String getId()
	{
		return name;
	}

	public ASTType type()
	{
		return type;
	}

	public boolean addFunction(String id, ASTType type)
	{
		if (containsFunction(id)) {
			return false;
		} else {
			functions.put(id, new SymbolFunction(id, type));
			return true;
		}
	}

	public Map<String, SymbolFunction> getFunctions()
	{
		return Collections.unmodifiableMap(functions);
	}

	public SymbolFunction getFunction(String id)
	{
		if (containsFunction(id)) {
			return functions.get(id);
		} else {
			return null;
		}
	}

	public boolean addVariable(String id, ASTType type)
	{
		if (containsVariable(id)) {
			return false;
		} else {
			variables.put(id, new SymbolVariable(id, type));
			return true;
		}
	}

	public SymbolVariable getVariable(String id)
	{
		if (containsVariable(id)) {
			return variables.get(id);
		} else {
			return null;
		}
	}

	public boolean containsVariable(String id)
	{
		return variables.containsKey(id);
	}

	public boolean containsFunction(String id)
	{
		return functions.containsKey(id);
	}

	public String parent()
	{
		return parent;
	}

	public boolean addImplementedInterface(String interfaceName)
	{
		if (implementedInterfaces.containsKey(interfaceName)) {
			return false;
		}
		implementedInterfaces.put(interfaceName, interfaceName);
		return true;
	}

	public boolean implementsInterface(String interfaceName)
	{
		return implementedInterfaces.containsKey(interfaceName);
	}

	public boolean implementsAnyInterface()
	{
		return !implementedInterfaces.isEmpty();
	}

	public Map<String, String> getImplementedInterfaces()
	{
		return Collections.unmodifiableMap(implementedInterfaces);
	}
}
