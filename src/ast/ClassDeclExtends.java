package src.ast;

import java.util.List;

import src.lexer.Token;

public class ClassDeclExtends extends ClassDecl
{
	private Identifier id;
	private Inheritance parentId;
	private List<Declaration> declList;

	public ClassDeclExtends(Token token, Identifier classId, Inheritance parentId, List<Declaration> declList)
	{
		super(token);
		this.id = classId;
		this.parentId = parentId;
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

	public Inheritance getParent()
	{
		return parentId;
	}

	public void setParent(Inheritance parentId)
	{
		this.parentId = parentId;
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