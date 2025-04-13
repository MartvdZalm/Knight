package knight.compiler.ast;

import java.util.List;

import knight.compiler.lexer.Token;

/*
 * File: ASTFunction.java
 * @author: Mart van der Zalm
 * Date: 2025-04-10
 */
public class ASTFunction extends AST
{
	private ASTType returnType;
	private ASTIdentifier functionName;
	private ASTList<ASTArgument> argumentList;
	private ASTBody body;

	public ASTFunction(Token token, ASTType returnType, ASTIdentifier functionName, List<ASTArgument> argumentList,
			ASTBody body)
	{
		super(token);

		this.returnType = returnType;
		this.functionName = functionName;
		this.argumentList = new ASTList<>(argumentList);
		this.body = body;
	}

	public ASTType getReturnType()
	{
		return returnType;
	}

	public ASTIdentifier getFunctionName()
	{
		return functionName;
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

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}
}
