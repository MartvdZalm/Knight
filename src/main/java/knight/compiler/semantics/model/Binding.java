package knight.compiler.semantics.model;

import knight.compiler.ast.types.ASTType;
import knight.compiler.semantics.utils.Counter;

public abstract class Binding
{
	private Counter counter = Counter.getInstance();
	protected ASTType type;
	private int count;

	public Binding(ASTType t)
	{
		count = counter.getCount();
		type = t;
	}

	public ASTType getType()
	{
		return type;
	}

	@Override
	public String toString()
	{
		return Integer.toString(count);
	}
}
