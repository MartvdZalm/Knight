package knight.compiler.ast;

import knight.compiler.lexer.Token;

/*
 * File: ASTWhile.java
 * @author: Mart van der Zalm
 * Date: 2025-04-10
 */
public class ASTWhile extends ASTStatement
{
	private ASTExpression condition;
	private ASTBody body;

	public ASTWhile(Token token, ASTExpression condition, ASTBody body)
	{
		super(token);
		this.condition = condition;
		this.body = body;
	}

	public ASTExpression getCondition()
	{
		return condition;
	}

	public void setCondition(ASTExpression condition)
	{
		this.condition = condition;
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
