package src.ast;

import java.util.List;

import src.lexer.Token;

public class FunctionVoid extends Declaration
{
    private Token access;
    private Type returnType;
    private IdentifierExpr functionName;
    private List<ArgDecl> argList;
	private List<Declaration> declList;

    public FunctionVoid(Token token, Token access, Type returnType, IdentifierExpr functionName, List<ArgDecl> argList, List<Declaration> declList)
    {
       	super(token);
		this.access = access;
        this.returnType = returnType;
		this.functionName = functionName;
		this.argList = argList;
		this.declList = declList;
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

	public int getDeclListSize()
	{
		return declList.size();
	}

	public ArgDecl getArgDeclAt(int index)
	{
		if (index < argList.size()) {
			return argList.get(index);
		}
		return null;
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
