package knight.compiler.semantics;

import knight.compiler.ast.Type;
import knight.compiler.symbol.Counter;

public abstract class Binding
{
	private Counter counter = Counter.getInstance(); // Counter instance to generate unique counts for each binding
	protected Type type; // The type associated with the binding
	private int count; // A unique count assigned to each binding

	/**
     * Constructor to create a new Binding with a given type.
     * It assigns a unique count to the binding using the Counter instance.
     *
     * @param t The Type associated with the binding.
     */
	public Binding(Type t)
	{
		count = counter.getCount();
		type = t;
	}

	public Type getType()
	{
		return type;
	}

	@Override
	public String toString()
	{
		return Integer.toString(count);
	}
}