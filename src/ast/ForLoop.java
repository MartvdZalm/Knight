package src.ast;

import java.util.List;

import src.lexer.Token;

public class ForLoop extends Statement
{
    private Declaration initialization;
    private Expression condition;
    private Expression increment;
	private List<Declaration> varList;
	private List<Statement> statList;

    public ForLoop(Token token, Declaration initialization, Expression condition, Expression increment, List<Declaration> varList, List<Statement> statList)
    {
        super(token);
        this.initialization = initialization;
        this.condition = condition;
        this.increment = increment;
        this.varList = varList;
        this.statList = statList;
    }

    public Declaration getInitialization()
    {
        return initialization;
    }

    public void setInitialization(Declaration initialization)
    {
        this.initialization = initialization;
    }

    public Expression getCondition()
    {
        return condition;
    }

    public void setCondition(Expression condition)
    {
        this.condition = condition;
    }

    public Expression getIncrement()
    {
        return increment;
    }

    public void setIncrement(Expression increment)
    {
        this.increment = increment;
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

    @Override
    public <R> R accept(Visitor<R> v)
    {
        return v.visit(this);
    }
}