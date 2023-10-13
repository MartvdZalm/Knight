package src.ast;

import java.util.List;
import src.lexer.Token;

// Base class for class declaration
public class ClassDecl extends Declaration
{
	private Identifier id;
	private List<Declaration> declList;

	public ClassDecl(Token token, Identifier id, List<Declaration> declList)
	{
		super(token);
		this.id = id;
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