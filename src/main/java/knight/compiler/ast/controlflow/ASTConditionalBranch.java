package knight.compiler.ast.controlflow;

import knight.compiler.ast.ASTVisitor;
import knight.compiler.ast.expressions.ASTExpression;
import knight.compiler.ast.statements.ASTBody;
import knight.compiler.ast.statements.ASTStatement;
import knight.compiler.lexer.Token;

public class ASTConditionalBranch extends ASTStatement
{
	private ASTExpression condition;
	private ASTBody body;

	public ASTConditionalBranch(Token token, ASTExpression condition, ASTBody body)
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
	public <R> R accept(ASTVisitor<R> visitor)
	{
		return visitor.visit(this);
	}
}
