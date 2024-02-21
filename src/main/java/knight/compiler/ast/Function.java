package knight.compiler.ast;

import java.util.List;

import knight.compiler.lexer.Token;

public class Function extends Tree
{
    private Type returnType;
    private Identifier id;
    private List<Argument> argumentList;
    private List<Variable> variableList;
	private List<Statement> statementList;

    public Function(Token token, Type returnType, Identifier id, List<Argument> argumentList, List<Variable> variableList, List<Statement> statementList)
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

    public List<Argument> getArgumentList()
    {
    	return argumentList;
    }

    public List<Variable> getVariableList()
    {
    	return variableList;
    }

    public List<Statement> getStatementList()
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

	public Argument getArgumentDeclAt(int index)
	{
		if (index < getArgumentListSize()) {
			return argumentList.get(index);
		}
		return null;
	}

	public Variable getVariableDeclAt(int index)
	{
		if (index < getVariableListSize()) {
			return variableList.get(index);
		}
		return null;
	}

	public Statement getStatementDeclAt(int index)
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
