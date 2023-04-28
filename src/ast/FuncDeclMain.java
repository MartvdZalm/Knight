package src.ast;

import java.util.List;

import src.lexer.Token;

public class FuncDeclMain extends FuncDecl
{
	public IdentifierExpr methodName;
	public List<Declaration> varList;
	public List<Statement> statList;

	public FuncDeclMain(Token token, IdentifierExpr methodName, List<Declaration> varList, List<Statement> statList)
	{
		super(token);
		this.methodName = methodName;
		this.varList = varList;
		this.statList = statList;
	}

	public IdentifierExpr getMethodName()
	{
		return methodName;
	}

	public void setMethodName(IdentifierExpr methodName)
	{
		this.methodName = methodName;
	}

	public int getVarListSize()
	{
		return varList.size();
	}

	public int getStatListSize()
	{
		return statList.size();
	}

	public Declaration getVarDeclAt(int index)
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