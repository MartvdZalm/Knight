package src.ast;

import java.util.List;

import src.lexer.Token;

public class FuncDeclMain extends FuncDecl
{
	private Type returnType;
	private IdentifierExpr methodName;
	private List<Declaration> varList;
	private List<Statement> statList;
	private Expression returnExpr;
	private Token access;

	public FuncDeclMain(Token token, Type returnType, IdentifierExpr methodName, List<Declaration> varList, List<Statement> statList, Expression returnExpr, Token access)
	{
		super(token);
		this.returnType = returnType;
		this.methodName = methodName;
		this.varList = varList;
		this.statList = statList;
		this.access = access;
		this.returnExpr = returnExpr;
	}

	public Type getReturnType()
	{
		return returnType;
	}

	public void setReturnType(Type returnType)
	{
		this.returnType = returnType;
	}

	public Expression getReturnExpr()
	{
		return returnExpr;
	}

	public void setReturnExpr(Expression returnExpr)
	{
		this.returnExpr = returnExpr;
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

	public Token getAccess()
	{
		return access;
	}

	public void setAccess(Token access)
	{
		this.access = access;
	}

	@Override
	public <R> R accept(Visitor<R> v)
	{
		return v.visit(this);
	}
}