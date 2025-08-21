package knight.compiler.library;

public class LibraryFunction
{
	private final String name;
	private final String returnType;
	private final String[] parameterTypes;
	private final String implementation; // Target language implementation

	public LibraryFunction(String name, String returnType, String[] parameterTypes, String implementation)
	{
		this.name = name;
		this.returnType = returnType;
		this.parameterTypes = parameterTypes;
		this.implementation = implementation;
	}

	public String getName()
	{
		return name;
	}

	public String getReturnType()
	{
		return returnType;
	}

	public String[] getParameterTypes()
	{
		return parameterTypes;
	}

	public String getImplementation()
	{
		return implementation;
	}
}
