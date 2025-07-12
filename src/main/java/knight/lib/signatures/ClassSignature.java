package knight.lib;

import java.util.List;

public class ClassSignature
{
	private String name;
	private List<PropertySignature> properties;
	private List<FunctionSignature> functions;

	public ClassSignature(String name)
	{
		this.name = name;
	}

	public List<PropertySignature> getProperties()
	{
		return properties;
	}

	public void setProperties(List<PropertySignature> properties)
	{
		this.properties = properties;
	}

	public List<FunctionSignature> getFunctions()
	{
		return functions;
	}

	public void setFunctions(List<FunctionSignature> functions)
	{
		this.functions = functions;
	}
}
