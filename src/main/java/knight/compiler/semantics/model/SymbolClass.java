package knight.compiler.semantics.model;

import java.util.Enumeration;
import java.util.Hashtable;

import knight.compiler.ast.types.ASTIdentifierType;
import knight.compiler.ast.types.ASTType;

public class SymbolClass extends Binding
{
	private String id;
	private Hashtable<String, SymbolFunction> functions;
	private Hashtable<String, SymbolVariable> variables;
	private String parent;

	public SymbolClass(String id, String p)
	{
		super(new ASTIdentifierType(null, id));
		this.id = id;
		parent = p;

		functions = new Hashtable<>();
		variables = new Hashtable<>();
	}

	public String getId()
	{
		return id;
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

	public Enumeration<String> getFunctions()
	{
		return functions.keys();
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
}
