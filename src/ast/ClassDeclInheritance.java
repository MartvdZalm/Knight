package src.ast;

import java.util.List;
import src.lexer.Token;

// Extends the base class
public class ClassDeclInheritance extends ClassDecl
{
	private Inheritance parentId;

	public ClassDeclInheritance(Token token, Identifier id, List<Declaration> declList, Inheritance parentId)
	{
		super(token, id, declList);
		this.parentId = parentId;
	}

	public Inheritance getParent()
	{
		return parentId;
	}

	public void setParent(Inheritance parentId)
	{
		this.parentId = parentId;
	}

	@Override
	public <R> R accept(Visitor<R> v)
	{
		return v.visit(this);
	}
}