package knight.compiler.library;

import knight.compiler.ast.program.ASTProgram;
import java.util.Map;
import java.util.HashMap;

public class Library
{
	private final String name;
	private final ASTProgram ast;
	private final Map<String, LibraryFunction> functions;
	private final Map<String, LibraryClass> classes;

	public Library(String name, ASTProgram ast)
	{
		this.name = name;
		this.ast = ast;
		this.functions = new HashMap<>();
		this.classes = new HashMap<>();
		extractSymbols();
	}

	private void extractSymbols()
	{
		// Extract functions and classes from the AST
		// This will be implemented to parse the AST and populate the maps
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

	public LibraryClass getClass(String name)
	{
		return classes.get(name);
	}

	public boolean hasFunction(String name)
	{
		return functions.containsKey(name);
	}

	public boolean hasClass(String name)
	{
		return classes.containsKey(name);
	}
}
