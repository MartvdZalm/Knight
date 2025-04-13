package knight.compiler.ast;

import knight.compiler.lexer.Token;

/*
 * File: ASTNewInstance.java
 * @author: Mart van der Zalm
 * Date: 2025-04-10
 */
public class ASTNewInstance extends ASTExpression
{
	private ASTIdentifierExpr className;

	public ASTNewInstance(Token token, ASTIdentifierExpr className)
	{
		super(token);
		this.className = className;
	}

	public ASTIdentifierExpr getClassName()
	{
		return className;
	}

	public void setClassName(ASTIdentifierExpr className)
	{
		this.className = className;
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}
}