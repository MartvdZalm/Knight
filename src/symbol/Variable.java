package src.symbol;

import src.ast.Type;

public class Variable extends Decl
{
	private int lvIndex = -1;

	public Variable(String id, Type type)
	{
		super(id, type);
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