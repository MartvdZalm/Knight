package knight.compiler.semantics.model;

import knight.compiler.ast.types.ASTType;

public class SymbolProperty extends Binding
{
	private final String name;

	public SymbolProperty(String name, ASTType type)
	{
		super(type);
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	@Override
	public String toString()
	{
		return String.format("Property(%s : %s)", name, type);
	}
}
