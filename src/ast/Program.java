package src.ast;

import java.util.List;

import src.lexer.Token;

public class Program extends Tree
{
	private List<ClassDecl> classList;
	private List<Include> includeList;

	public Program(Token token, List<ClassDecl> classList, List<Include> includeList)
	{
		super(token);
		this.classList = classList;
		this.includeList = includeList;
	}

	public List<ClassDecl> getClassList()
	{
		return classList;
	}

	public void setClassList(List<ClassDecl> classList)
	{
		this.classList = classList;
	}

	public int getClassListSize()
	{
		return classList.size();
	}

	public ClassDecl getClassDeclAt(int index)
	{
		if (index < classList.size()) {
			return classList.get(index);
		}
		return null;
	}

	public List<Include> getIncludeList()
	{
		return includeList;
	}

	public void setIncludeList(List<Include> includeList)
	{
		this.includeList = includeList;
	}

	public int getIncludeListSize()
	{
		return includeList.size();
	}

	public Include getIncludeAt(int index)
	{
		if (index < includeList.size()) {
			return includeList.get(index);
		}
		return null;
	}

	@Override
	public <R> R accept(Visitor<R> v)
	{
		return v.visit(this);
	}
}
