package src.ast;

import java.util.List;

import src.lexer.Token;

public class FunctionVoid extends Declaration
{
    private Token access;
    private Type returnType;
    private IdentifierExpr functionName;
    private List<ArgDecl> argList;
	private List<Declaration> varList;
	private List<Statement> statList;

    public FunctionVoid(Token token, Token access, Type returnType, IdentifierExpr functionName, List<ArgDecl> argList, List<Declaration> varList, List<Statement> statList)
    {
       	super(token);
		this.access = access;
        this.returnType = returnType;
		this.functionName = functionName;
		this.argList = argList;
		this.varList = varList;
		this.statList = statList;
    }

    public Token getAccess()
	{
		return access;
	}

	public void setAccess(Token access)
	{
		this.access = access;
	}

    public Type getReturnType()
    {
        return returnType;
    }

    public void setReturnType(Type returnType)
    {
        this.returnType = returnType;
    }

    public IdentifierExpr getFunctionName()
    {
        return functionName;
    }

    public void setFunctionName(IdentifierExpr functionName)
    {
        this.functionName = functionName;
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
