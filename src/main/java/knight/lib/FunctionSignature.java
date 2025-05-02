package knight.lib;

import java.util.List;

import knight.compiler.ast.ASTType;

public class FunctionSignature
{
	private String name;
	private ASTType returnType;
	private List<ASTType> parameterTypes;

	public FunctionSignature(String name, ASTType returnType, List<ASTType> parameterTypes)
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

	public ASTType getReturnType()
	{
		return returnType;
	}

	public void setReturnType(ASTType returnType)
	{
		this.returnType = returnType;
	}

	public List<ASTType> getParameterTypes()
	{
		return parameterTypes;
	}

	public void setParameterTypes(List<ASTType> parameterTypes)
	{
		this.parameterTypes = parameterTypes;
	}
}
