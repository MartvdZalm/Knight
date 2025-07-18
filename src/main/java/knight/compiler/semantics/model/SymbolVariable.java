package knight.compiler.semantics.model;

import knight.compiler.ast.types.ASTType;

public class SymbolVariable extends Binding
{
	private final String name;
	private int lvIndex = -1;

	public SymbolVariable(String name, ASTType type)
	{
		super(type);
		this.name = name;
	}

	public String getId()
	{
		return name;
	}

	public int getLvIndex()
	{
		return lvIndex;
	}

	public void setLvIndex(int lvIndex)
	{
		this.lvIndex = lvIndex;
	}
}
