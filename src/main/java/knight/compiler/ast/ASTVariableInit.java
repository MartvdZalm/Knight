package knight.compiler.ast;

import knight.compiler.lexer.Token;

/*
 * File: ASTVariableInit.java
 * @author: Mart van der Zalm
 * Date: 2025-04-10
 */
public class ASTVariableInit extends ASTVariable
{
	private ASTExpression expr;

	public ASTVariableInit(Token token, ASTType type, ASTIdentifier id, ASTExpression expr)
	{
		super(token, type, id);
		this.expr = expr;
	}

	public void setExpr(ASTExpression expr)
	{
		this.expr = expr;
	}

	public ASTExpression getExpr()
	{
		return expr;
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}
}
