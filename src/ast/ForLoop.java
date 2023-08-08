package src.ast;

import java.util.List;

import src.lexer.Token;

public class ForLoop extends Statement
{
    private Declaration initialization;
    private Expression condition;
    private Expression increment;
	private List<Declaration> declList;

    public ForLoop(Token token, Declaration initialization, Expression condition, Expression increment, List<Declaration> declList)
    {
        super(token);
        this.initialization = initialization;
        this.condition = condition;
        this.increment = increment;
        this.declList = declList;
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

    public int getDeclListSize()
	{
		return declList.size();
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