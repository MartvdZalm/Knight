package knight.ast;

import java.util.List;

import knight.lexer.Token;

public class Program extends Tree
{
	private List<Include> includeList;
	private List<Enumeration> enumList;
	private List<Interface> interList;
	private List<Class> classList;
	private List<Function> functionList;
	private List<Variable> variableList;

	public Program(Token token, List<Include> includeList, List<Enumeration> enumList, List<Interface> interList, List<Class> classList, List<Function> functionList, List<Variable> variableList)
	{
		super(token);
		this.includeList = includeList;
		this.enumList = enumList;
		this.interList = interList;
		this.classList = classList;
		this.functionList = functionList;
		this.variableList = variableList;
	}

	public List<Include> getIncludeList()
	{
		return includeList;
	}

	public List<Enumeration> getEnumList()
	{
		return enumList;
	}

	public List<Interface> getInterList()
	{
		return interList;
	}

	public List<Class> getClassList()
	{
		return classList;
	}

	public List<Function> getFunctionList()
	{
		return functionList;
	}

	public List<Variable> getVariableList()
	{
		return variableList;
	}

	public int getIncludeListSize()
	{
		return includeList.size();
	}

	public int getEnumListSize()
	{
		return enumList.size();
	}

	public int getInterListSize()
	{
		return interList.size();
	}

	public int getClassListSize()
	{
		return classList.size();
	}

	public int getFunctionListSize()
	{
		return functionList.size();
	}

	public int getVariableListSize()
	{
		return variableList.size();
	}

	public Include getIncludeDeclAt(int index)
	{
		if (index < getIncludeListSize()) {
			return includeList.get(index);
		}
		return null;
	}

	public Enumeration getEnumDeclAt(int index)
	{
		if (index < getEnumListSize()) {
			return enumList.get(index);
		}
		return null;
	}

	public Interface getInterDeclAt(int index)
	{
		if (index < getInterListSize()) {
			return interList.get(index);
		}
		return null;
	}

	public Class getClassDeclAt(int index)
	{
		if (index < getClassListSize()) {
			return classList.get(index);
		}
		return null;
	}

	public Function getFunctionDeclAt(int index)
	{
		if  (index < getFunctionListSize()) {
			return functionList.get(index);
		}
		return null;
	}

	public Variable getVariableDeclAt(int index)
	{
		if (index < getVariableListSize()) {
			return variableList.get(index);
		}
		return null;
	}

	@Override
	public <R> R accept(Visitor<R> v)
	{
		return v.visit(this);
	}
}
