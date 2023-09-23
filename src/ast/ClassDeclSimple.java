package src.ast;

import java.util.List;

import src.lexer.Token;

public class ClassDeclSimple extends ClassDecl
{
	private Identifier id;
	private List<Declaration> declList;

	public ClassDeclSimple(Token token, Identifier className, List<Declaration> declList)
	{
		super(token);
		this.id = className;
		this.declList = declList;
	}

	public Identifier getId()
	{
		return id;
	}

	public void setId(Identifier id)
	{
		this.id = id;
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