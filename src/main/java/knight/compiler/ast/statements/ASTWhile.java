package knight.compiler.ast.statements;

import knight.compiler.lexer.Token;
import knight.compiler.ast.expressions.ASTExpression;
import knight.compiler.ast.ASTVisitor;

public class ASTWhile extends ASTStatement
{
	private ASTExpression expr;
	private ASTStatement body;

	public ASTWhile(Token token, ASTExpression expr, ASTStatement body)
	{
		super(token);
		this.expr = expr;
		this.body = body;
	}

	public ASTExpression getExpr()
	{
		return expr;
	}

	public void setExpr(ASTExpression expr)
	{
		this.expr = expr;
	}

	public ASTStatement getBody()
	{
		return body;
	}

	public void setBody(ASTStatement body)
	{
		this.body = body;
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}
}