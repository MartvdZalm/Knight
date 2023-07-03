package src.symbol;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import src.ast.Type;

/**
 * Represents a function declaration in the program's symbol handling phase.
 * Function class extends the Decl class and is used to store information about a function, including its parameters and variables.
 */
public class Function extends Decl
{
	private Vector<Variable> params; // Vector to store the function's parameters (Variable objects)
	private Hashtable<String, Variable> vars; // Hashtable to store the function's variables (Variable objects) with their identifiers as keys

	/**
     * Constructor to create a new function declaration with the specified identifier and return type.
     *
     * @param id   The identifier (name) of the function.
     * @param type The return Type associated with the function.
     */
	public Function(String id, Type type)
	{
		super(id, type);
		vars = new Hashtable<>();
		params = new Vector<>();
	}

	/**
     * Adds a new parameter to the function with the specified identifier and type.
     *
     * @param id   The identifier (name) of the parameter.
     * @param type The Type associated with the parameter.
     * @return True if the parameter is successfully added, false if a parameter with the same identifier already exists.
     */
	public boolean addParam(String id, Type type)
	{
		if (containsParam(id)) {
			return false;
		} else {
			params.addElement(new Variable(id, type));
			return true;
		}
	}

	/**
     * Get an enumeration of the function's parameters (Variable objects).
     *
     * @return An Enumeration of the function's parameters.
     */
	public Enumeration<Variable> getParams()
	{
		return params.elements();
	}

	/**
     * Get the parameter (Variable object) at the specified index in the parameters vector.
     *
     * @param i The index of the parameter to retrieve.
     * @return The Variable object representing the parameter at the specified index, or null if the index is out of bounds.
     */
	public Variable getParamAt(int i)
	{
		if (i < params.size()) {
			return params.elementAt(i);
		} else {
			return null;
		}
	}

	/**
     * Adds a new variable to the function with the specified identifier and type.
     *
     * @param id   The identifier (name) of the variable.
     * @param type The Type associated with the variable.
     * @return True if the variable is successfully added, false if a variable with the same identifier already exists.
     */
	public boolean addVar(String id, Type type)
	{
		if (containsVar(id)) {
			return false;
		} else {
			vars.put(id, new Variable(id, type));
			return true;
		}
	}

	/**
     * Checks if the function contains a variable with the specified identifier (name).
     *
     * @param id The identifier (name) of the variable to check.
     * @return True if the function contains a variable with the specified identifier, false otherwise.
     */
	public boolean containsVar(String id)
	{
		return containsParam(id) || vars.containsKey(id);
	}

	/**
     * Checks if the function contains a parameter with the specified identifier (name).
     *
     * @param id The identifier (name) of the parameter to check.
     * @return True if the function contains a parameter with the specified identifier, false otherwise.
     */
	public boolean containsParam(String id)
	{
		for (int i = 0; i < params.size(); i++) {
			if (params.elementAt(i).getId().equals(id)) {
				return true;
			}
		}
		return false;
	}

	/**
     * Get the variable (Variable object) with the specified identifier (name) from the function.
     *
     * @param id The identifier (name) of the variable to retrieve.
     * @return The Variable object representing the variable with the specified identifier, or null if it does not exist.
     */
	public Variable getVar(String id)
	{
		if (containsVar(id)) {
			return vars.get(id);
		} else {
			return null;
		}
	}

	/**
     * Get the parameter (Variable object) with the specified identifier (name) from the function.
     *
     * @param id The identifier (name) of the parameter to retrieve.
     * @return The Variable object representing the parameter with the specified identifier, or null if it does not exist.
     */
	public Variable getParam(String id)
	{
		for (int i = 0; i < params.size(); i++) {
			if (params.elementAt(i).getId().equals(id)) {
				return (params.elementAt(i));
			}
		}

		return null;
	}

	/**
     * Get the number of parameters in the function.
     *
     * @return The number of parameters in the function.
     */
	public int getParamsSize()
	{
		return params.size();
	}
}