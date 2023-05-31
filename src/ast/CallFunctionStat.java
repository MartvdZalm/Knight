package src.ast;

import java.util.List;

import src.lexer.Token;

public class CallFunctionStat extends Statement
{
	private Expression instanceName;
	private IdentifierExpr methodId;
	private List<Expression> argExprList;

	public CallFunctionStat(Token token, Expression instanceName, IdentifierExpr methodId, List<Expression> argExprList)
	{
		super(token);
		this.instanceName = instanceName;
		this.methodId = methodId;
		this.argExprList = argExprList;
	}

	public Expression getInstanceName()
	{
		return instanceName;
	}

	public void setInstanceName(Expression instanceName)
	{
		this.instanceName = instanceName;
	}

	public IdentifierExpr getMethodId()
	{
		return methodId;
	}

	public void setMethodId(IdentifierExpr methodId)
	{
		this.methodId = methodId;
	}

	public int getArgExprListSize()
	{
		return argExprList.size();
	}

	public Expression getArgExprAt(int index)
	{
		if (index < argExprList.size()) {
			return argExprList.get(index);
		}
		return null;
	}

	@Override
	public <R> R accept(Visitor<R> v)
	{
		return v.visit(this);
	}
}