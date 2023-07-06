package src.symbol;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Hashtable;

import src.ast.IdentifierType;
import src.ast.IntArrayType;
import src.ast.IntType;
import src.ast.StringType;
import src.ast.Type;

/**
 * Represents the SymbolTable used during the symbol handling phase of the program.
 * The SymbolTable stores information about classes, their methods, global variables, and types.
 */
public class SymbolTable
{
	private Hashtable<String, Klass> hashtable; // Hashtable to store classes with their identifiers as keys
	private Deque<String> rstack = new ArrayDeque<String>(); // Deque to keep track of the class hierarchy during type checking

	/**
     * Constructor to create a new empty SymbolTable.
     */
	public SymbolTable()
	{
		hashtable = new Hashtable<>();
	}

	/**
     * Adds a new class to the SymbolTable with the specified identifier and parent class.
     *
     * @param id     The identifier (name) of the class.
     * @param parent The name of the parent class that this class extends (can be null for no parent).
     * @return True if the class is successfully added, false if a class with the same identifier already exists.
     */
	public boolean addKlass(String id, String parent)
	{
		if (containsKlass(id)) {
			return false;
		} else {
			hashtable.put(id, new Klass(id, parent));
		}
		return true;
	}

	/**
     * Get the class (Klass object) with the specified identifier (name) from the SymbolTable.
     *
     * @param id The identifier (name) of the class to retrieve.
     * @return The Klass object representing the class with the specified identifier, or null if it does not exist.
     */
	public Klass getKlass(String id)
	{
		if (containsKlass(id)) {
			return hashtable.get(id);
		} else {
			return null;
		}
	}

	/**
     * Check if the SymbolTable contains a class with the specified identifier (name).
     *
     * @param id The identifier (name) of the class to check.
     * @return True if the SymbolTable contains the class, false otherwise.
     */
	public boolean containsKlass(String id)
	{
		if (id != null) {
			return hashtable.containsKey(id);
		}
		return false;
	}

	/**
     * Get the Variable object for a variable with the specified identifier (name) within the scope of a given function and class.
     * This method searches for the variable in the method's local variables (parameters and variables) and then in the class's global variables.
     * It also handles the case when a variable is defined in a parent class and is accessible through inheritance.
     *
     * @param m   The Function object representing the method (function) where the variable is used.
     * @param c   The Klass object representing the class where the method (function) is defined.
     * @param id  The identifier (name) of the variable to retrieve.
     * @return The Variable object representing the variable with the specified identifier, or null if it does not exist in the given scope.
     */
	public Variable getVar(Function m, Klass c, String id)
	{
		if (m != null) {
			if (m.getVar(id) != null) {
				return m.getVar(id);
			}
			if (m.getParam(id) != null) {
				return m.getParam(id);
			}
		}

		while (c != null && !rstack.contains(c.getId())) {
			rstack.push(c.getId());
			if (c.getVar(id) != null) {
				rstack.clear();
				return c.getVar(id);
			} else {
				if (c.parent() == null) {
					c = null;
				} else {
					c = getKlass(c.parent());
				}
			}
		}
		rstack.clear();
		return null;
	}

	/**
     * Check if a variable with the specified identifier (name) exists within the scope of a given function and class.
     *
     * @param m   The Function object representing the method (function) where the variable is used.
     * @param c   The Klass object representing the class where the method (function) is defined.
     * @param id  The identifier (name) of the variable to check.
     * @return True if the variable with the specified identifier exists in the given scope, false otherwise.
     */
	public boolean containsVar(Function m, Klass c, String id)
	{
		Variable var = getVar(m, c, id);
		if (var != null) {
			return true;
		}
		return false;
	}

    /**
     * Get the Type object for a variable with the specified identifier (name) within the scope of a given function and class.
     *
     * @param m   The Function object representing the method (function) where the variable is used.
     * @param c   The Klass object representing the class where the method (function) is defined.
     * @param id  The identifier (name) of the variable to retrieve its type.
     * @return The Type object representing the type of the variable with the specified identifier, or null if it does not exist in the given scope.
     */
	public Type getVarType(Function m, Klass c, String id)
	{
		Variable var = getVar(m, c, id);
		if (var != null) {
			return var.getType();
		}
		return null;
	}

	/**
     * Get the Function object for a method with the specified identifier (name) within the scope of a given class.
     * This method searches for the method in the given class and its parent classes (inherited methods).
     *
     * @param id          The identifier (name) of the method to retrieve.
     * @param classScope  The name of the class where the method is being called (search starts here).
     * @return The Function object representing the method with the specified identifier, or null if it does not exist in the given class or its parent classes.
     */
	public Function getFunction(String id, String classScope)
	{
		if (getKlass(classScope) == null) {
			return null;
		}

		Klass c = getKlass(classScope);
		while (c != null && !rstack.contains(c.getId())) {
			rstack.push(c.getId());
			if (c.getFunction(id) != null) {
				rstack.clear();
				return c.getFunction(id);
			} else {
				if (c.parent() == null) {
					c = null;
				} else {
					c = getKlass(c.parent());
				}
			}
		}
		rstack.clear();
		return null;
	}

    /**
     * Get the return Type object for a method with the specified identifier (name) within the scope of a given class.
     *
     * @param id          The identifier (name) of the method to retrieve its return type.
     * @param classScope  The name of the class where the method is being called (search starts here).
     * @return The Type object representing the return type of the method with the specified identifier, or null if it does not exist in the given class or its parent classes.
     */
	public Type getFunctionType(String id, String classScope)
	{
		Function m = getFunction(id, classScope);
		if (m == null) {
			return null;
		} else {
			return m.getType();
		}
	}

    /**
     * Compare two Type objects and check if they are equivalent.
     * This method is used for type checking to ensure the compatibility of types in assignments, method calls, etc.
     *
     * @param t1 The first Type object to compare.
     * @param t2 The second Type object to compare.
     * @return True if the two Type objects are equivalent, false otherwise.
     */
	public boolean compareTypes(Type t1, Type t2)
	{
		if (t1 == null || t2 == null) {
			return false;
		}

		if (t1 instanceof IntType && t2 instanceof IntType) {
			return true;
		}
		if (t1 instanceof IntArrayType && t2 instanceof IntArrayType) {
			return true;
		}
		if (t1 instanceof StringType && t2 instanceof StringType) {
			return true;
		}
		if (t1 instanceof IdentifierType && t2 instanceof IdentifierType) {
			IdentifierType i1 = (IdentifierType) t1;
			IdentifierType i2 = (IdentifierType) t2;

			Klass c = getKlass(i2.getVarID());
			while (c != null && !rstack.contains(c.getId())) {
				rstack.push(c.getId());
				if (i1.getVarID().equals(c.getId())) {
					rstack.clear();
					return true;
				} else {
					if (c.parent() == null) {
						rstack.clear();
						return false;
					}
					c = getKlass(c.parent());
				}
			}
			rstack.clear();
		}
		return false;
	}

	/**
     * Compare two Type objects and check if they are exactly the same (not considering inheritance).
     *
     * @param t1 The first Type object to compare.
     * @param t2 The second Type object to compare.
     * @return True if the two Type objects are exactly the same, false otherwise.
     */
	public boolean absCompTypes(Type t1, Type t2)
	{
		if (t1 == null || t2 == null) {
			return false;
		}

		if (t1 instanceof IntType && t2 instanceof IntType) {
			return true;
		}
		if (t1 instanceof IntArrayType && t2 instanceof IntArrayType) {
			return true;
		}
		if (t1 instanceof IdentifierType && t2 instanceof IdentifierType) {
			IdentifierType i1 = (IdentifierType) t1;
			IdentifierType i2 = (IdentifierType) t2;
			if (i1.getVarID().equals(i2.getVarID())) {
				return true;
			}
		}
		
		return false;
	}
}