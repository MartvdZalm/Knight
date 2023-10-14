package src.symbol;

import java.util.Enumeration;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Hashtable;

import src.ast.BooleanType;
import src.ast.IdentifierType;
import src.ast.IntArrayType;
import src.ast.IntType;
import src.ast.StringType;
import src.ast.Type;

public class SymbolProgram
{
	private Hashtable<String, SymbolClass> classes; 
	private Hashtable<String, SymbolFunction> functions;
	private Hashtable<String, SymbolVariable> variables;

	private Deque<String> rstack = new ArrayDeque<String>();

	public SymbolProgram()
	{
		classes = new Hashtable<>();
		functions = new Hashtable<>();
		variables = new Hashtable<>();
	}

	public boolean addClass(String id, String parent)
	{
		if (containsClass(id)) {
			return false;
		} else {
			classes.put(id, new SymbolClass(id, parent));
		}
		return true;
	}

	public boolean addFunction(String id, Type type)
	{
		if (containsFunction(id)) {
			return false;
		} else {
			functions.put(id, new SymbolFunction(id, type));
			return true;
		}
	}

	public boolean addVariable(String id, Type type)
	{
		if (containsVariable(id)) {
			return false;
		} else {
			variables.put(id, new SymbolVariable(id, type));
			return true;
		}
	}

	public SymbolClass getClass(String id)
	{
		if (containsClass(id)) {
			return classes.get(id);
		} else {
			return null;
		}
	}

	public SymbolFunction getFunction(String id)
	{
		if (containsFunction(id)) {
			return functions.get(id);
		} else {
			return null;
		}
	}

	public SymbolFunction getFunction(String id, String classScope)
	{
		if (getClass(classScope) == null) {
			return null;
		}

		SymbolClass c = getClass(classScope);
		while (c != null && !rstack.contains(c.getId())) {
			rstack.push(c.getId());
			if (c.getFunction(id) != null) {
				rstack.clear();
				return c.getFunction(id);
			} else {
				if (c.parent() == null) {
					c = null;
				} else {
					c = getClass(c.parent());
				}
			}
		}
		rstack.clear();
		return null;
	}

	public Enumeration<String> getFunctions()
	{
		return functions.keys();
	}


	public Type getFunctionType(String id, String classScope)
	{
		SymbolFunction m = getFunction(id, classScope);
		if (m == null) {
			return null;
		} else {
			return m.getType();
		}
	}

	public SymbolVariable getVariable(String id)
	{
		return variables.get(id);
	}

	public SymbolVariable getVariable(String id, SymbolClass sClass, SymbolFunction sFunction)
	{
		if (sFunction != null) {
			if (sFunction.getVariable(id) != null) {
				return sFunction.getVariable(id);
			}
			if (sFunction.getParam(id) != null) {
				return sFunction.getParam(id);
			}
		} else if (sClass != null) {

			while (sClass != null && !rstack.contains(sClass.getId())) {
				rstack.push(sClass.getId());
				if (sClass.getVariable(id) != null) {
					rstack.clear();
					return sClass.getVariable(id);
				} else {
					if (sClass.parent() == null) {
						sClass = null;
					} else {
						sClass = getClass(sClass.parent());
					}
				}
			}
			rstack.clear();
			return null;
		}

		return getVariable(id);
	}

	public Type getVariableType(String id)
	{
		SymbolVariable var = getVariable(id);
		if (var != null) {
			return var.getType();
		}
		return null;
	}

	public boolean containsClass(String id)
	{
		if (id != null) {
			return classes.containsKey(id);
		}
		return false;
	}

	public boolean containsFunction(String id)
	{
		return functions.containsKey(id);
	}

	public boolean containsVariable(String id)
	{
		SymbolVariable var = getVariable(id);
		if (var != null) {
			return true;
		}
		return false;
	}

	public boolean compareTypes(Type t1, Type t2)
	{
		if (t1 == null || t2 == null) {
			return false;
		}

		if (t1 instanceof IntType && t2 instanceof IntType) {
			return true;
		}
		if (t1 instanceof BooleanType && t2 instanceof BooleanType) {
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

			SymbolClass c = getClass(i2.getVarID());
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
					c = getClass(c.parent());
				}
			}
			rstack.clear();
		}
		return false;
	}

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