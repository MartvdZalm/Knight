package src.ast;

import java.util.List;

import src.lexer.Token;

public class FuncExpr extends Expression
{
	private List<ArgDecl> argList;
	private List<Declaration> varList;
	private List<Statement> statList;
	private Expression returnExpr;

	public FuncExpr(Token token, List<ArgDecl> argList, List<Declaration> varList, List<Statement> statList, Expression returnExpr)
	{
		super(token);
		this.argList = argList;
		this.varList = varList;
		this.statList = statList;
		this.returnExpr = returnExpr;
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
