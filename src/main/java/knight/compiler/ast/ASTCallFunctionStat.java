package knight.compiler.ast;

import java.util.List;

import knight.compiler.lexer.Token;

/*
 * File: ASTCallFunctionStat.java
 * @author: Mart van der Zalm
 * Date: 2025-04-10
 */
public class ASTCallFunctionStat extends ASTStatement
{
	private ASTExpression instanceName;
	private ASTIdentifierExpr methodId;
	private List<ASTExpression> argExprList;

	public ASTCallFunctionStat(Token token, ASTExpression instanceName, ASTIdentifierExpr methodId,
			List<ASTExpression> argExprList)
	{
		super(token);
		this.instanceName = instanceName;
		this.methodId = methodId;
		this.argExprList = argExprList;
	}

	public ASTExpression getInstanceName()
	{
		return instanceName;
	}

	public void setInstanceName(ASTExpression instanceName)
	{
		this.instanceName = instanceName;
	}

	public ASTIdentifierExpr getFunctionId()
	{
		return methodId;
	}

	public void setFunctionId(ASTIdentifierExpr methodId)
	{
		this.methodId = methodId;
	}

	public void setArgExprList(List<ASTExpression> argExprList)
	{
		this.argExprList = argExprList;
	}

	public List<ASTExpression> getArgExprList()
	{
		return argExprList;
	}

	public int getArgExprListSize()
	{
		return argExprList.size();
	}

	public ASTExpression getArgExprAt(int index)
	{
		if (index < argExprList.size()) {
			return argExprList.get(index);
		}
		return null;
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}
}