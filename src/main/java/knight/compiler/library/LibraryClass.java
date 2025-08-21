package knight.compiler.library;

import java.util.Map;
import java.util.HashMap;

public class LibraryClass
{
	private final String name;
	private final Map<String, LibraryFunction> methods;
	private final Map<String, String> properties;

	public LibraryClass(String name)
	{
		this.name = name;
		this.methods = new HashMap<>();
		this.properties = new HashMap<>();
	}

	public void addMethod(String name, LibraryFunction method)
	{
		methods.put(name, method);
	}

	public void addProperty(String name, String type)
	{
		properties.put(name, type);
	}

	public String getName()
	{
		return name;
	}

	public LibraryFunction getMethod(String name)
	{
		return methods.get(name);
	}

	public String getPropertyType(String name)
	{
		return properties.get(name);
	}

	public boolean hasMethod(String name)
	{
		return methods.containsKey(name);
	}

	public boolean hasProperty(String name)
	{
		return properties.containsKey(name);
	}
}
