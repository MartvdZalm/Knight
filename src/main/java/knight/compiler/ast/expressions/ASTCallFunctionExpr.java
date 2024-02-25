package knight.compiler.ast.expressions;

import java.util.List;

import knight.compiler.lexer.Token;
import knight.compiler.ast.ASTVisitor;

public class ASTCallFunctionExpr extends ASTExpression
{
	private ASTExpression instanceName;
	private ASTIdentifierExpr methodId;
	private List<ASTExpression> argExprList;

	public ASTCallFunctionExpr(Token token, ASTExpression instanceName, ASTIdentifierExpr methodId, List<ASTExpression> argExprList)
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

	public ASTIdentifierExpr getMethodId()
	{
		return methodId;
	}

	public void setMethodId(ASTIdentifierExpr methodId)
	{
		this.methodId = methodId;
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