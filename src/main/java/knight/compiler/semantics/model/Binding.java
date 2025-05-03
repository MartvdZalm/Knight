package knight.compiler.semantics.model;

import knight.compiler.ast.ASTType;
import knight.compiler.semantics.utils.Counter;

/*
 * File: Binding.java
 * @author: Mart van der Zalm
 * Date: 2025-04-10
 * Description:
 */
public abstract class Binding
{
	private Counter counter = Counter.getInstance(); // Counter instance to generate unique counts for each binding
	protected ASTType type; // The type associated with the binding
	private int count; // A unique count assigned to each binding

	/**
	 * Constructor to create a new Binding with a given type. It assigns a unique
	 * count to the binding using the Counter instance.
	 *
	 * @param t The Type associated with the binding.
	 */
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
