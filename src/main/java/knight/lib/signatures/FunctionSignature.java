package knight.lib;

import java.util.List;
import knight.compiler.lexer.Tokens;

public class FunctionSignature
{
	private String name;
	private Tokens returnType;
	private List<Tokens> parameterTypes;

	public FunctionSignature(String name, Tokens returnType, List<Tokens> parameterTypes)
	{
		this.name = name;
		this.returnType = returnType;
		this.parameterTypes = parameterTypes;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Tokens getReturnType()
	{
		return returnType;
	}

	public void setReturnType(Tokens returnType)
	{
		this.returnType = returnType;
	}

	public List<Tokens> getParameterTypes()
	{
		return parameterTypes;
	}

	public void setParameterTypes(List<Tokens> parameterTypes)
	{
		this.parameterTypes = parameterTypes;
	}
}
