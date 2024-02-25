package knight.compiler.ast.statements;

import knight.compiler.lexer.Token;
import knight.compiler.ast.expressions.ASTExpression;
import knight.compiler.ast.declarations.ASTIdentifier;
import knight.compiler.ast.ASTVisitor;

public class ASTAssign extends ASTStatement
{
	private ASTIdentifier id;
	private ASTExpression expr;

	public ASTAssign(Token token, ASTIdentifier id, ASTExpression src)
	{
		super(token);
		this.id = id;
		this.expr = src;
	}

	public ASTIdentifier getId()
	{
		return id;
	}

	public void setId(ASTIdentifier id)
	{
		this.id = id;
	}

	public ASTExpression getExpr()
	{
		return expr;
	}

	public void setExpr(ASTExpression expr)
	{
		this.expr = expr;
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}
}
