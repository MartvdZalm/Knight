package src.symbol;

import src.ast.Type;
import src.semantics.Binding;

/**
 * The Variable class represents a variable declaration in the symbol table.
 * It extends the Decl class, which contains common properties and methods for declarations.
 */
public class SVariable extends Binding
{
	private String id;
	private int lvIndex = -1; // The local variable index in the activation record (initialized to -1)

	/**
     * Creates a new Variable object with the specified identifier and type.
     *
     * @param id   The identifier (name) of the variable.
     * @param type The Type object representing the type of the variable.
     */
	public SVariable(String id, Type type)
	{
		super(type);
		this.id = id;
	}

	public String getId()
	{
		return id;
	}

	/**
     * Get the local variable index assigned to this variable in the activation record.
     * The local variable index is used during code generation to access the variable's value.
     *
     * @return The local variable index.
     */
	public int getLvIndex()
	{
		return lvIndex;
	}

	/**
     * Set the local variable index for this variable in the activation record.
     * The local variable index is used during code generation to access the variable's value.
     *
     * @param lvIndex The local variable index to be set.
     */
	public void setLvIndex(int lvIndex)
	{
		this.lvIndex = lvIndex;
	}
}