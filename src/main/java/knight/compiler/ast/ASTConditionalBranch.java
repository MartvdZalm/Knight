package knight.compiler.ast;

import knight.compiler.lexer.Token;

/*
 * File: ASTConditionalBranch.java
 * @author: Mart van der Zalm
 * Date: 2025-04-10
 */
public class ASTConditionalBranch extends AST
{
	private ASTExpression condition;
	private ASTBody body;

	public ASTConditionalBranch(Token token)
	{
		super(token);
	}

	public ASTConditionalBranch setCondition(ASTExpression condition)
	{
		this.condition = condition;
		return this;
	}

	public ASTExpression getCondition()
	{
		return condition;
	}

	public ASTBody getBody()
	{
		return body;
	}

	public ASTConditionalBranch setBody(ASTBody body)
	{
		this.body = body;
		return this;
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}
}
