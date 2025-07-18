package knight.compiler.semantics.model;

import knight.compiler.ast.types.ASTType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SymbolFunction extends Binding
{
	private final String name;
	private final List<SymbolVariable> params;

	public SymbolFunction(String name, ASTType type)
	{
		super(type);

		this.name = name;
		this.params = new ArrayList<>();
	}

	public String getId()
	{
		return name;
	}

	public boolean addParam(String id, ASTType type)
	{
		if (containsParam(id)) {
			return false;
		} else {
			params.add(new SymbolVariable(id, type));
			return true;
		}
	}

	public List<SymbolVariable> getAllParams()
	{
		return Collections.unmodifiableList(params);
	}

	public SymbolVariable getParamAt(int i)
	{
		if (i < 0 || i >= params.size()) {
			throw new IndexOutOfBoundsException("Invalid parameter index: " + i);
		}
		return params.get(i);
	}

	public boolean containsParam(String id)
	{
		for (SymbolVariable param : params) {
			if (param.getId().equals(id)) {
				return true;
			}
		}
		return false;
	}

	public SymbolVariable getParam(String id)
	{
		for (SymbolVariable param : params) {
			if (param.getId().equals(id)) {
				return param;
			}
		}

		return null;
	}

	public int getParamsSize()
	{
		return params.size();
	}
}
