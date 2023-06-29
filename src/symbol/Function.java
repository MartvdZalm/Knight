package src.symbol;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import src.ast.Type;

public class Function extends Decl
{
	private Vector<Variable> params;
	private Hashtable<String, Variable> vars;

	public Function(String id, Type type)
	{
		super(id, type);
		vars = new Hashtable<>();
		params = new Vector<>();
	}

	public boolean addParam(String id, Type type)
	{
		if (containsParam(id)) {
			return false;
		} else {
			params.addElement(new Variable(id, type));
			return true;
		}
	}

	public Enumeration<Variable> getParams()
	{
		return params.elements();
	}

	public Variable getParamAt(int i)
	{
		if (i < params.size()) {
			return params.elementAt(i);
		} else {
			return null;
		}
	}

	public boolean addVar(String id, Type type)
	{
		if (containsVar(id)) {
			return false;
		} else {
			vars.put(id, new Variable(id, type));
			return true;
		}
	}

	public boolean containsVar(String id)
	{
		return containsParam(id) || vars.containsKey(id);
	}

	public boolean containsParam(String id)
	{
		for (int i = 0; i < params.size(); i++) {
			if (params.elementAt(i).getId().equals(id)) {
				return true;
			}
		}
		return false;
	}

	public Variable getVar(String id)
	{
		if (containsVar(id)) {
			return vars.get(id);
		} else {
			return null;
		}
	}

	public Variable getParam(String id)
	{
		for (int i = 0; i < params.size(); i++) {
			if (params.elementAt(i).getId().equals(id)) {
				return (params.elementAt(i));
			}
		}

		return null;
	}

	public int getParamsSize()
	{
		return params.size();
	}
}