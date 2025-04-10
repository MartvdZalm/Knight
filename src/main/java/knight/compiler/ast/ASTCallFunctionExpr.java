package knight.compiler.ast;

import java.util.List;

import knight.compiler.lexer.Token;

/*
 * File: ASTCallFunctionExpr.java
 * @author: Mart van der Zalm
 * Date: 2025-04-10
 */
public class ASTCallFunctionExpr extends ASTExpression
{
	private ASTIdentifierExpr functionName;
	private ASTList<ASTExpression> argumentList;

	public ASTCallFunctionExpr(Token token, ASTIdentifierExpr functionName, List<ASTExpression> argumentList)
	{
		super(token);
		this.functionName = functionName;
		this.argumentList = new ASTList<>(argumentList);
	}

	public ASTIdentifierExpr getFunctionName()
	{
		return functionName;
	}

	public void setFunctionName(ASTIdentifierExpr functionName)
	{
		this.functionName = functionName;
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
