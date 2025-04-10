package knight.compiler.ast;

import knight.compiler.lexer.Token;

/*
 * File: ASTAssign.java
 * @author: Mart van der Zalm
 * Date: 2025-04-10
 */
public class ASTAssign extends ASTStatement
{
	private ASTIdentifier id;
	private ASTExpression expr;

	public ASTAssign(Token token, ASTIdentifier id, ASTExpression expr)
	{
		super(token);
		this.id = id;
		this.expr = expr;
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
