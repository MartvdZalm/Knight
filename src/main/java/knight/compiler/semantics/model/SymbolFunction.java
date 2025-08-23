package knight.compiler.semantics.model;

import knight.compiler.ast.types.ASTType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class SymbolFunction extends Binding
{
	private final String name;
	private final List<SymbolVariable> parameters;
	private final Map<String, SymbolVariable> parameterMap;

	public SymbolFunction(String name, ASTType returnType)
	{
		super(returnType);
		this.name = name;
		this.parameters = new ArrayList<>();
		this.parameterMap = new HashMap<>();
	}

	public String getName()
	{
		return name;
	}

	public boolean addParameter(String parameterName, ASTType parameterType)
	{
		if (parameterMap.containsKey(parameterName)) {
			return false;
		}

		SymbolVariable parameter = new SymbolVariable(parameterName, parameterType);
		parameters.add(parameter);
		parameterMap.put(parameterName, parameter);
		return true;
	}

	public List<SymbolVariable> getParameters()
	{
		return Collections.unmodifiableList(parameters);
	}

	public SymbolVariable getParameter(String parameterName)
	{
		return parameterMap.get(parameterName);
	}

	public SymbolVariable getParameter(int index)
	{
		if (index < 0 || index >= parameters.size()) {
			throw new IndexOutOfBoundsException(
					String.format("Parameter index %d out of bounds for function %s", index, name));
		}
		return parameters.get(index);
	}

	public boolean hasParameter(String parameterName)
	{
		return parameterMap.containsKey(parameterName);
	}

	public int getParameterCount()
	{
		return parameters.size();
	}

	public ASTType getReturnType()
	{
		return getType();
	}

	@Override
	public String toString()
	{
		return String.format("Function(%s : %s, params=%s)", name, type, parameters);
	}
}
