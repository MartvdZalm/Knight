package knight.compiler.ast.expressions;

import java.util.List;

import knight.compiler.ast.utils.ASTList;
import knight.compiler.ast.ASTVisitor;
import knight.compiler.lexer.Token;

public class ASTCallFunctionExpr extends ASTExpression
{
	private ASTIdentifierExpr instance;
	private ASTIdentifierExpr functionName;
	private ASTList<ASTExpression> argumentList;

	public ASTCallFunctionExpr(Token token, ASTIdentifierExpr instance, ASTIdentifierExpr functionName,
			List<ASTExpression> argumentList)
	{
		super(token);
		this.instance = instance;
		this.functionName = functionName;
		this.argumentList = new ASTList<>(argumentList);
	}

	public ASTIdentifierExpr getInstance()
	{
		return instance;
	}

	public ASTIdentifierExpr getFunctionName()
	{
		return functionName;
	}

	public void setFunctionName(ASTIdentifierExpr functionName)
	{
		this.functionName = functionName;
	}

	public void setArgumentList(List<ASTExpression> argumentList)
	{
		this.argumentList = new ASTList<>(argumentList);
	}

	public List<ASTExpression> getArgumentList()
	{
		return argumentList.getList();
	}

	public int getArgumentListSize()
	{
		return argumentList.getSize();
	}

	public ASTExpression getArgumentAt(int index)
	{
		return argumentList.getAt(index);
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}
}
