package src.symbol;

import java.util.Enumeration;
import java.util.Hashtable;

import src.ast.IdentifierType;
import src.ast.Type;
import src.semantics.Binding;

/**
 * Represents a class declaration in the program's symbol handling phase.
 * Klass class extends the Binding class and is used to store information about a class, including its methods and global variables.
 */
public class Klass extends Binding
{
	private String id; // The identifier (name) of the class
	private Hashtable<String, Function> methods; // Hashtable to store the class's methods (Function objects) with their identifiers as keys
	private Hashtable<String, Variable> globals; // Hashtable to store the class's global variables (Variable objects) with their identifiers as keys
	private String parent; // The name of the parent class (if any) that this class extends

	/**
     * Constructor to create a new class declaration with the specified identifier and parent class.
     *
     * @param id     The identifier (name) of the class.
     * @param parent The name of the parent class that this class extends (can be null for no parent).
     */
	public Klass(String id, String p)
	{
		super(new IdentifierType(null, id));
		this.id = id;
		parent = p;
		methods = new Hashtable<>();
		globals = new Hashtable<>();
	}

	/**
     * Get the identifier (name) of the class.
     *
     * @return The identifier (name) of the class.
     */
	public String getId()
	{
		return id;
	}

	/**
     * Get the Type associated with the class.
     *
     * @return The Type associated with the class (IdentifierType with a null location).
     */
	public Type type()
	{
		return type;
	}

	/**
     * Adds a new method to the class with the specified identifier and return type.
     *
     * @param id   The identifier (name) of the method.
     * @param type The Type associated with the method.
     * @return True if the method is successfully added, false if a method with the same identifier already exists.
     */
	public boolean addMethod(String id, Type type)
	{
		if (containsMethod(id)) {
			return false;
		} else {
			methods.put(id, new Function(id, type));
			return true;
		}
	}

    /**
     * Get an enumeration of the class's methods (method identifiers).
     *
     * @return An Enumeration of the class's methods.
     */
	public Enumeration<String> getMethods()
	{
		return methods.keys();
	}

	/**
     * Get the method (Function object) with the specified identifier (name) from the class.
     *
     * @param id The identifier (name) of the method to retrieve.
     * @return The Function object representing the method with the specified identifier, or null if it does not exist.
     */
	public Function getMethod(String id)
	{
		if (containsMethod(id)) {
			return methods.get(id);
		} else {
			return null;
		}
	}

	/**
     * Adds a new global variable to the class with the specified identifier and type.
     *
     * @param id   The identifier (name) of the global variable.
     * @param type The Type associated with the global variable.
     * @return True if the global variable is successfully added, false if a global variable with the same identifier already exists.
     */
	public boolean addVar(String id, Type type)
	{
		if (containsVar(id)) {
			return false;
		} else {
			globals.put(id, new Variable(id, type));
			return true;
		}
	}

    /**
     * Get the global variable (Variable object) with the specified identifier (name) from the class.
     *
     * @param id The identifier (name) of the global variable to retrieve.
     * @return The Variable object representing the global variable with the specified identifier, or null if it does not exist.
     */
	public Variable getVar(String id)
	{
		if (containsVar(id)) {
			return globals.get(id);
		} else {
			return null;
		}
	}

    /**
     * Check if the class contains a global variable with the specified identifier (name).
     *
     * @param id The identifier (name) of the global variable to check.
     * @return True if the class contains the global variable, false otherwise.
     */
	public boolean containsVar(String id)
	{
		return globals.containsKey(id);
	}

    /**
     * Check if the class contains a method with the specified identifier (name).
     *
     * @param id The identifier (name) of the method to check.
     * @return True if the class contains the method, false otherwise.
     */
	public boolean containsMethod(String id)
	{
		return methods.containsKey(id);
	}

	/**
     * Get the name of the parent class that this class extends.
     *
     * @return The name of the parent class (can be null if this class has no parent).
     */
	public String parent()
	{
		return parent;
	}
}
