package knight.compiler.library;

import knight.compiler.ast.program.ASTProgram;
import java.util.Map;
import java.util.HashMap;

public class Library
{
	private final String name;
	private final ASTProgram ast;
	private final Map<String, LibraryFunction> functions;

	public Library(String name, ASTProgram ast)
	{
		this.name = name;
		this.ast = ast;
		this.functions = new HashMap<>();
	}

	public String getName()
	{
		return name;
	}

	public ASTProgram getAST()
	{
		return ast;
	}

	public LibraryFunction getFunction(String name)
	{
		return functions.get(name);
	}

	public boolean hasFunction(String name)
	{
		return functions.containsKey(name);
	}
}
