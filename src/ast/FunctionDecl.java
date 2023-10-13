package src.ast;

import java.util.List;

import src.lexer.Token;

public abstract class FunctionDecl extends Declaration
{
    private Type returnType;
    private Identifier functionName;
    private List<ArgDecl> argList;
	private List<Declaration> declList;

    public FunctionDecl(Token token, Type returnType, Identifier functionName, List<ArgDecl> argList, List<Declaration> declList)
    {
        super(token);
        this.returnType = returnType;
        this.functionName = functionName;
        this.argList = argList;
        this.declList = declList;
    }

    public Type getReturnType()
    {
        return returnType;
    }

    public void setReturnType(Type returnType)
    {
        this.returnType = returnType;
    }

    public Identifier getFunctionName()
    {
        return functionName;
    }

    public void setFunctionName(Identifier functionName)
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
}
