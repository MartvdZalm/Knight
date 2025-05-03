package knight.compiler.ast;

import knight.compiler.lexer.Token;

public class ASTConditionalBranch extends AST
{
	private ASTExpression condition;
	private ASTBody body;

	public ASTConditionalBranch(Token token, ASTExpression condition, ASTBody body)
	{
		super(token);
		this.condition = condition;
		this.body = body;
	}

	public void setCondition(ASTExpression condition)
	{
		this.condition = condition;
	}

	public ASTExpression getCondition()
	{
		return condition;
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
