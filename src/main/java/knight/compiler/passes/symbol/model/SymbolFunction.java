package knight.compiler.passes.symbol.model;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import knight.compiler.ast.ASTType;

/*
 * File: SymbolFunction.java
 * @author: Mart van der Zalm
 * Date: 2025-04-10
 */
public class SymbolFunction extends Binding
{
	private String id;
	private Vector<SymbolVariable> params;
	private Hashtable<String, SymbolVariable> variables;

	public SymbolFunction(String id, ASTType type)
	{
		super(type);
		this.id = id;
		variables = new Hashtable<>();
		params = new Vector<>();
	}

	public String getId()
	{
		return id;
	}

	public boolean addParam(String id, ASTType type)
	{
		if (containsParam(id)) {
			return false;
		} else {
			params.addElement(new SymbolVariable(id, type));
			return true;
		}
	}

	public Enumeration<SymbolVariable> getParams()
	{
		return params.elements();
	}

	public SymbolVariable getParamAt(int i)
	{
		if (i < params.size()) {
			return params.elementAt(i);
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

	public boolean containsVariable(String id)
	{
		return containsParam(id) || variables.containsKey(id);
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

	public SymbolVariable getVariable(String id)
	{
		if (containsVariable(id)) {
			return variables.get(id);
		} else {
			return null;
		}
	}

	public SymbolVariable getParam(String id)
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
