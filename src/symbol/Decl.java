package src.symbol;

import src.ast.Type;
import src.semantics.Binding;

/**
 * Represents a declaration in the program's symbol handling phase.
 * Decl class extends the Binding class and is used to associate an identifier with its corresponding type in the symbol table.
 */
public class Decl extends Binding
{
    private String id; // The identifier (name) of the declaration

    /**
     * Constructor to create a new declaration with the specified identifier and type.
     *
     * @param id   The identifier (name) of the declaration.
     * @param type The Type associated with the declaration.
     */
    public Decl(String id, Type type)
    {
        super(type);
        this.id = id;
    }

    /**
     * Get the identifier (name) of the declaration.
     *
     * @return The identifier (name) of the declaration.
     */
    public String getId()
	{
		return id;
	}

    /**
     * Get the Type associated with the declaration.
     * This method overrides the getType() method from the parent class (Binding).
     *
     * @return The Type associated with the declaration.
     */
    @Override
	public Type getType()
	{
		return type;
	}
}
