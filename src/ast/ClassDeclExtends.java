package src.ast;

import java.util.List;

import src.lexer.Token;

public class ClassDeclExtends extends ClassDecl
{
	private IdentifierExpr id;
	private IdentifierExpr parent;
	private List<Declaration> varList;
	private List<FuncDecl> methodList;

	public ClassDeclExtends(Token token, IdentifierExpr className, IdentifierExpr parentClassName, List<Declaration> varList, List<FuncDecl> methodList)
	{
		super(token);
		this.id = className;
		this.parent = parentClassName;
		this.varList = varList;
		this.methodList = methodList;
	}

	public IdentifierExpr getId()
	{
		return id;
	}

	public void setId(IdentifierExpr id)
	{
		this.id = id;
	}

	public IdentifierExpr getParent()
	{
		return parent;
	}

	public void setParent(IdentifierExpr parent)
	{
		this.parent = parent;
	}

	public int getMethodListSize()
	{
		return methodList.size();
	}

	public FuncDecl getMethodDeclAt(int index)
	{
		if (index < methodList.size()) {
			return methodList.get(index);
		}
		return null;
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