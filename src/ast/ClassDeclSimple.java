package src.ast;

import java.util.List;

import src.lexer.Token;

public class ClassDeclSimple extends ClassDecl
{
	private IdentifierExpr id;
	private List<Declaration> varList;

	public ClassDeclSimple(Token jSymbol, IdentifierExpr className, List<Declaration> varList)
	{
		super(jSymbol);
		this.id = className;
		this.varList = varList;
	}

	public IdentifierExpr getId()
	{
		return id;
	}

	public void setId(IdentifierExpr id)
	{
		this.id = id;
	}

	public int getVarListSize()
	{
		return varList.size();
	}

	public Declaration getVarDeclAt(int index)
	{
		if (index < varList.size()) {
			return varList.get(index);
		}
		return null;
	}

	@Override
	public <R> R accept(Visitor<R> v)
	{
		return v.visit(this);
	}
}