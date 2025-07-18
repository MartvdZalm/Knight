package knight.compiler.semantics.model;

import knight.compiler.ast.types.ASTType;
import knight.compiler.semantics.utils.Counter;

public abstract class Binding
{
	protected final ASTType type;
	private final int count;

	public Binding(ASTType type)
	{
		Counter counter = Counter.getInstance();
		this.count = counter.getCount();
		this.type = type;
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
