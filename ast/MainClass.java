package ast;

import java.util.List;

import lexer.Token;

public class MainClass extends Tree
{
	IdentifierExpr className;
	List<Statement> statList;
	List<VarDecl> varList;

	public MainClass(Token token, IdentifierExpr className, List<Statement> statList, List<VarDecl> varList)
	{
		super(token);
		this.className = className;
		this.statList = statList;
		this.varList = varList;
	}

	public IdentifierExpr getClassName() {
		return className;
	}

	public void setClassName(IdentifierExpr className) {
		this.className = className;
	}

	public int getVarListSize()
	{
		return varList.size();
	}
	
	public int getStatListSize()
	{
		return statList.size();
	}

	public VarDecl getVarDeclAt(int index)
	{
		if (index < varList.size()) {
			return varList.get(index);
		}
		return null;
	}

	public Statement getStatAt(int index)
	{
		if (index < statList.size()) {
			return statList.get(index);
		}
		return null;
	}

	public void setStatAt(int index, Statement stat)
	{
		if (index < statList.size()) {
			statList.set(index, stat);
		}
	}

	@Override
	public <R> R accept(Visitor<R> v)
	{
		return v.visit(this);
	}
}
