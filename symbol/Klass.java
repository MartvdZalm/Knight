package symbol;

import java.util.Enumeration;
import java.util.Hashtable;

import ast.IdentifierType;
import ast.Type;
import semantics.Binding;

public class Klass extends Binding
{
	String id;
	Hashtable<String, Function> methods;
	Hashtable<String, Variable> globals;
	String parent;

	public Klass(String id, String p)
	{
		super(new IdentifierType(null, id));
		this.id = id;
		parent = p;
		methods = new Hashtable<>();
		globals = new Hashtable<>();
	}

	public String getId()
	{
		return id;
	}

	public Type type()
	{
		return type;
	}

	public boolean addMethod(String id, Type type)
	{
		if (containsMethod(id)) {
			return false;
		} else {
			methods.put(id, new Function(id, type));
			return true;
		}
	}

	public Enumeration<String> getMethods()
	{
		return methods.keys();
	}

	public Function getMethod(String id)
	{
		if (containsMethod(id)) {
			return methods.get(id);
		} else {
			return null;
		}
	}

	public boolean addVar(String id, Type type)
	{
		if (containsVar(id)) {
			return false;
		} else {
			globals.put(id, new Variable(id, type));
			return true;
		}
	}

	public Variable getVar(String id)
	{
		if (containsVar(id)) {
			return globals.get(id);
		} else {
			return null;
		}
	}

	public boolean containsVar(String id)
	{
		return globals.containsKey(id);
	}

	public boolean containsMethod(String id)
	{
		return methods.containsKey(id);
	}

	public String parent()
	{
		return parent;
	}
}
