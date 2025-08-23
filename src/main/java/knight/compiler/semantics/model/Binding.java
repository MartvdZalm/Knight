package knight.compiler.semantics.model;

import knight.compiler.ast.types.ASTType;
import knight.compiler.semantics.utils.Counter;

public abstract class Binding
{
	protected final ASTType type;
	private final int uniqueId;

	public Binding(ASTType type)
	{
		this.uniqueId = Counter.getInstance().getCount();
		this.type = type;
	}

	public ASTType getType()
	{
		return type;
	}

	public int getUniqueId()
	{
		return uniqueId;
	}

	@Override
	public String toString()
	{
		return Integer.toString(uniqueId);
	}
}
