package src.ast;

import java.util.List;

import src.lexer.Token;

public class Program extends Tree
{
	private List<IncludeDecl> includeList;
	private List<EnumDecl> enumList;
	private List<InterDecl> interList;
	private List<ClassDecl> classList;
	private List<FunctionDecl> functionList;
	private List<VariableDecl> variableList;

	public Program(Token token, List<IncludeDecl> includeList, List<EnumDecl> enumList, List<InterDecl> interList, List<ClassDecl> classList, List<FunctionDecl> functionList, List<VariableDecl> variableList)
	{
		super(token);
		this.includeList = includeList;
		this.enumList = enumList;
		this.interList = interList;
		this.classList = classList;
		this.functionList = functionList;
		this.variableList = variableList;
	}

	public List<IncludeDecl> getIncludeList()
	{
		return includeList;
	}

	public List<EnumDecl> getEnumList()
	{
		return enumList;
	}

	public List<InterDecl> getInterList()
	{
		return interList;
	}

	public List<ClassDecl> getClassList()
	{
		return classList;
	}

	public List<FunctionDecl> getFunctionList()
	{
		return functionList;
	}

	public List<VariableDecl> getVariableList()
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

	public IncludeDecl getIncludeDeclAt(int index)
	{
		if (index < getIncludeListSize()) {
			return includeList.get(index);
		}
		return null;
	}

	public EnumDecl getEnumDeclAt(int index)
	{
		if (index < getEnumListSize()) {
			return enumList.get(index);
		}
		return null;
	}

	public InterDecl getInterDeclAt(int index)
	{
		if (index < getInterListSize()) {
			return interList.get(index);
		}
		return null;
	}

	public ClassDecl getClassDeclAt(int index)
	{
		if (index < getClassListSize()) {
			return classList.get(index);
		}
		return null;
	}

	public FunctionDecl getFunctionDeclAt(int index)
	{
		if  (index < getFunctionListSize()) {
			return functionList.get(index);
		}
		return null;
	}

	public VariableDecl getVariableDeclAt(int index)
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
