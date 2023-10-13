package src.ast;

import java.util.List;

import src.lexer.Token;

public class Program extends Tree
{
    private List<Declaration> declList;

	public Program(Token token, List<Declaration> declList)
	{
		super(token);
		this.declList = declList;
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
