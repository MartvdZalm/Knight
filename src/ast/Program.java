package src.ast;

import java.util.List;

import src.lexer.Token;

public class Program extends Tree
{
	private List<Include> includeList;
    private List<ClassDecl> classList;
	private List<EnumDecl> enumList;
    private List<Declaration> declList;

	public Program(Token token, List<Include> includeList, List<ClassDecl> classList, List<EnumDecl> enumList, List<Declaration> declList)
	{
		super(token);
        this.includeList = includeList;
		this.classList = classList;
		this.enumList = enumList;
		this.declList = declList;
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

	public List<EnumDecl> getEnumList()
	{
		return enumList;
	}

	public void setEnumList(List<EnumDecl> enumList)
	{
		this.enumList = enumList;
	}

	public int getEnumListSize()
	{
		return declList.size();
	}

	public EnumDecl getEnumAt(int index)
	{
		if (index < enumList.size()) {
			return enumList.get(index);
		}
		return null;
	}

	public List<Declaration> getDeclList()
	{
		return declList;
	}

	public void setDeclList(List<Declaration> declList)
	{
		this.declList = declList;
	}

	public int getDeclListSize()
	{
		return declList.size();
	}

	public Declaration getDeclAt(int index)
	{
		if (index < declList.size()) {
			return declList.get(index);
		}
		return null;
	}

	@Override
	public <R> R accept(Visitor<R> v)
	{
		return v.visit(this);
	}
}
