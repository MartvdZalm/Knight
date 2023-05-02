package src.symbol;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Hashtable;

import src.ast.IdentifierType;
import src.ast.IntArrayType;
import src.ast.IntType;
import src.ast.StringType;
import src.ast.Type;

public class SymbolTable
{
	private Hashtable<String, Klass> hashtable;
	private Deque<String> rstack = new ArrayDeque<String>();

	public SymbolTable()
	{
		hashtable = new Hashtable<>();
	}

	public boolean addKlass(String id, String parent)
	{
		if (containsKlass(id)) {
			return false;
		} else {
			hashtable.put(id, new Klass(id, parent));
		}
		return true;
	}

	public Klass getKlass(String id)
	{
		if (containsKlass(id)) {
			return hashtable.get(id);
		} else {
			return null;
		}
	}

	public boolean containsKlass(String id)
	{
		if (id != null) {
			return hashtable.containsKey(id);
		}
		return false;
	}

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

	public boolean containsVar(Function m, Klass c, String id)
	{
		Variable var = getVar(m, c, id);
		if (var != null) {
			return true;
		}
		return false;
	}

	public Type getVarType(Function m, Klass c, String id)
	{
		Variable var = getVar(m, c, id);
		if (var != null) {
			return var.getType();
		}
		return null;
	}

	public Function getMethod(String id, String classScope)
	{
		if (getKlass(classScope) == null) {
			return null;
		}

		Klass c = getKlass(classScope);
		while (c != null && !rstack.contains(c.getId())) {
			rstack.push(c.getId());
			if (c.getMethod(id) != null) {
				rstack.clear();
				return c.getMethod(id);
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

	public Type getMethodType(String id, String classScope)
	{
		Function m = getMethod(id, classScope);
		if (m == null) {
			return null;
		} else {
			return m.getType();
		}
	}

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