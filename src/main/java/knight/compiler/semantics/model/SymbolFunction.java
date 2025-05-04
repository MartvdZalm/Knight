package knight.compiler.semantics.model;

import java.util.Enumeration;
import java.util.Vector;

import knight.compiler.ast.types.ASTType;

public class SymbolFunction extends Binding
{
	private String id;
	private Vector<SymbolVariable> params;

	public SymbolFunction(String id, ASTType type)
	{
		super(type);

		this.id = id;
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

	public boolean containsParam(String id)
	{
		for (int i = 0; i < params.size(); i++) {
			if (params.elementAt(i).getId().equals(id)) {
				return true;
			}
		}
		return false;
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
