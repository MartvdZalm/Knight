package knight.compiler.semantics.model;

import knight.compiler.ast.types.*;

import java.util.*;

public class SymbolProgram
{
	private final Map<String, SymbolClass> classes;
	private final Map<String, SymbolFunction> functions;
	private final Map<String, SymbolVariable> variables;
	private final Map<String, SymbolInterface> interfaces;

	private final Deque<String> rstack = new ArrayDeque<>();

	public SymbolProgram()
	{
		this.classes = new HashMap<>();
		this.functions = new HashMap<>();
		this.variables = new HashMap<>();
		this.interfaces = new HashMap<>();
	}

	public void clear()
	{
		classes.clear();
		functions.clear();
		variables.clear();
		interfaces.clear();
		rstack.clear();
	}

	public boolean addClass(String id, String parent)
	{
		if (containsClass(id)) {
			return false;
		}

		classes.put(id, new SymbolClass(id, parent));
		return true;
	}

	public SymbolClass getClass(String id)
	{
		if (containsClass(id)) {
			return classes.get(id);
		}
		return null;
	}

	public Map<String, SymbolClass> getAllClasses()
	{
		return Collections.unmodifiableMap(classes);
	}

	public boolean addInterface(String name)
	{
		if (interfaces.containsKey(name)) {
			return false;
		}
		interfaces.put(name, new SymbolInterface(name));
		return true;
	}

	public SymbolInterface getInterface(String name)
	{
		return interfaces.get(name);
	}

	public boolean interfaceExists(String name)
	{
		return interfaces.containsKey(name);
	}

	public boolean addFunction(String id, ASTType type)
	{
		if (containsFunction(id)) {
			return false;
		}

		functions.put(id, new SymbolFunction(id, type));
		return true;
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

	public Map<String, SymbolFunction> getFunctions()
	{
		return Collections.unmodifiableMap(this.functions);
	}

	public ASTType getFunctionType(String id, String classScope)
	{
		SymbolFunction m = getFunction(id, classScope);
		if (m == null) {
			return null;
		}
		return m.getType();
	}

	public boolean addVariable(String id, ASTType type)
	{
		if (containsVariable(id)) {
			return false;
		}

		variables.put(id, new SymbolVariable(id, type));
		return true;
	}

	public SymbolVariable getVariable(String id)
	{
		return variables.get(id);
	}

	public SymbolVariable getVariable(String id, SymbolClass sClass, SymbolFunction sFunction)
	{
		if (sFunction != null) {
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
		return var != null;
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

			if (interfaceExists(i1.getName()) || interfaceExists(i2.getName())) {
				return i1.getName().equals(i2.getName());
			}

			SymbolClass c = getClass(i2.getName());
			while (c != null && !rstack.contains(c.getId())) {
				rstack.push(c.getId());
				if (i1.getName().equals(c.getId())) {
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

	public Map<String, SymbolInterface> getInterfaces()
	{
		return Collections.unmodifiableMap(this.interfaces);
	}
}
