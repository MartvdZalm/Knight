package src.symbol;

import src.ast.Type;
import src.semantics.Binding;

public class SymbolVariable extends Binding
{
	private String id;
	private int lvIndex = -1;

	public SymbolVariable(String id, Type type)
	{
		super(type);
		this.id = id;
	}

	public String getId()
	{
		return id;
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