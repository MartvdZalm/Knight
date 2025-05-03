package knight.compiler.semantics.model;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Enumeration;
import java.util.Hashtable;

import knight.compiler.ast.ASTBooleanType;
import knight.compiler.ast.ASTIdentifierType;
import knight.compiler.ast.ASTIntArrayType;
import knight.compiler.ast.ASTIntType;
import knight.compiler.ast.ASTStringType;
import knight.compiler.ast.ASTType;

/*
 * File: SymbolProgram.java
 * @author: Mart van der Zalm
 * Date: 2025-04-10
 */
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
		}

		classes.put(id, new SymbolClass(id, parent));
		return true;
	}

	public boolean addFunction(String id, ASTType type)
	{
		if (containsFunction(id)) {
			return false;
		}

		functions.put(id, new SymbolFunction(id, type));
		return true;
	}

	public boolean addVariable(String id, ASTType type)
	{
		if (containsVariable(id)) {
			return false;
		}

		variables.put(id, new SymbolVariable(id, type));
		return true;
	}

	public SymbolClass getClass(String id)
	{
		if (containsClass(id)) {
			return classes.get(id);
		}
		return null;
	}

	public SymbolFunction getFunction(String id)
	{
		if (containsFunction(id)) {
			return functions.get(id);
		}
		return null;
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

	public Hashtable<String, SymbolFunction> getFunctions()
	{
		return functions;
	}

	public ASTType getFunctionType(String id, String classScope)
	{
		SymbolFunction m = getFunction(id, classScope);
		if (m == null) {
			return null;
		}
		return m.getType();
	}

	public SymbolVariable getVariable(String id)
	{
		return variables.get(id);
	}

	public SymbolVariable getVariable(String id, SymbolClass sClass, SymbolFunction sFunction)
	{
		if (sFunction != null) {
			// if (sFunction.getVariable(id) != null) {
			// return sFunction.getVariable(id);
			// }
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

	public ASTType getVariableType(String id)
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

	public boolean compareTypes(ASTType t1, ASTType t2)
	{
		if (t1 == null || t2 == null) {
			return false;
		}

		if (t1 instanceof ASTIntType && t2 instanceof ASTIntType) {
			return true;
		}
		if (t1 instanceof ASTBooleanType && t2 instanceof ASTBooleanType) {
			return true;
		}
		if (t1 instanceof ASTIntArrayType && t2 instanceof ASTIntArrayType) {
			return true;
		}
		if (t1 instanceof ASTStringType && t2 instanceof ASTStringType) {
			return true;
		}
		if (t1 instanceof ASTIdentifierType && t2 instanceof ASTIdentifierType) {
			ASTIdentifierType i1 = (ASTIdentifierType) t1;
			ASTIdentifierType i2 = (ASTIdentifierType) t2;

			SymbolClass c = getClass(i2.getId());
			while (c != null && !rstack.contains(c.getId())) {
				rstack.push(c.getId());
				if (i1.getId().equals(c.getId())) {
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

	public boolean absCompTypes(ASTType t1, ASTType t2)
	{
		if (t1 == null || t2 == null) {
			return false;
		}

		if (t1 instanceof ASTIntType && t2 instanceof ASTIntType) {
			return true;
		}
		if (t1 instanceof ASTIntArrayType && t2 instanceof ASTIntArrayType) {
			return true;
		}
		if (t1 instanceof ASTIdentifierType && t2 instanceof ASTIdentifierType) {
			ASTIdentifierType i1 = (ASTIdentifierType) t1;
			ASTIdentifierType i2 = (ASTIdentifierType) t2;
			if (i1.getId().equals(i2.getId())) {
				return true;
			}
		}

		return false;
	}
}
