package src.ast;

import java.util.List;

import src.lexer.Token;

public class FunctionDecl extends Tree
{
    private Type returnType;
    private Identifier id;
    private List<ArgumentDecl> argumentList;
    private List<VariableDecl> variableList;
	private List<StatementDecl> statementList;

    public FunctionDecl(Token token, Type returnType, Identifier id, List<ArgumentDecl> argumentList, List<VariableDecl> variableList, List<StatementDecl> statementList)
    {
        super(token);
        this.returnType = returnType;
        this.id = id;
        this.argumentList = argumentList;
        this.variableList = variableList;
        this.statementList = statementList;
    }

    public Type getReturnType()
    {
        return returnType;
    }

    public Identifier getId()
    {
    	return id;
    }

    public List<ArgumentDecl> getArgumentList()
    {
    	return argumentList;
    }

    public List<VariableDecl> getVariableList()
    {
    	return variableList;
    }

    public List<StatementDecl> getStatementList()
    {
    	return statementList;
    }

	public int getArgumentListSize()
	{
		return argumentList.size();
	}

	public int getVariableListSize()
	{
		return variableList.size();
	}

	public int getStatementListSize()
	{
		return statementList.size();
	}

	public ArgumentDecl getArgumentDeclAt(int index)
	{
		if (index < getArgumentListSize()) {
			return argumentList.get(index);
		}
		return null;
	}

	public VariableDecl getVariableDeclAt(int index)
	{
		if (index < getVariableListSize()) {
			return variableList.get(index);
		}
		return null;
	}

	public StatementDecl getStatementDeclAt(int index)
	{
		if (index < getStatementListSize()) {
			return statementList.get(index);
		}
		return null;
	}

	@Override
	public <R> R accept(Visitor<R> v)
	{
		return v.visit(this);
	}
}
