package src.ast;

import java.util.List;

import src.lexer.Token;

public class FuncDeclStandard extends FuncDecl
{
    public Type returnType;
	public IdentifierExpr methodName;
	public List<ArgDecl> argList;
	public List<Declaration> varList;
	public List<Statement> statList;
	public Expression returnExpr;

	public FuncDeclStandard(Token token, Type returnType, IdentifierExpr methodName, List<ArgDecl> argList, List<Declaration> varList, List<Statement> statList, Expression returnExpr)
	{
		super(token);
		this.returnType = returnType;
		this.methodName = methodName;
		this.argList = argList;
		this.varList = varList;
		this.statList = statList;
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

	public IdentifierExpr getMethodName()
	{
		return methodName;
	}

	public void setMethodName(IdentifierExpr methodName)
	{
		this.methodName = methodName;
	}

	public Expression getReturnExpr()
	{
		return returnExpr;
	}

	public void setReturnExpr(Expression returnExpr)
	{
		this.returnExpr = returnExpr;
	}

	public int getArgListSize()
	{
		return argList.size();
	}

	public int getVarListSize()
	{
		return varList.size();
	}

	public int getStatListSize()
	{
		return statList.size();
	}

	public ArgDecl getArgDeclAt(int index)
	{
		if (index < argList.size()) {
			return argList.get(index);
		}
		return null;
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