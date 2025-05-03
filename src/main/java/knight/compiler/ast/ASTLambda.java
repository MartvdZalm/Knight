package knight.compiler.ast;

import java.util.List;

import knight.compiler.lexer.Token;

public class ASTLambda extends ASTExpression
{
	private ASTType returnType;
	private ASTList<ASTArgument> argumentList;
	private ASTBody body;

	public ASTLambda(Token token, ASTType returnType, List<ASTArgument> argumentList, ASTBody body)
	{
		super(token);
		this.returnType = returnType;
		this.argumentList = new ASTList<>(argumentList);
		this.body = body;
	}

	public ASTType getReturnType()
	{
		return returnType;
	}

	public void setReturnType(ASTType returnType)
	{
		this.returnType = returnType;
	}

	public List<ASTArgument> getArgumentList()
	{
		return argumentList.getList();
	}

	public int getArgumentListSize()
	{
		return argumentList.getSize();
	}

	public ASTArgument getArgumentAt(int index)
	{
		return argumentList.getAt(index);
	}

	public ASTBody getBody()
	{
		return body;
	}

	public void setBody(ASTBody body)
	{
		this.body = body;
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}
}
