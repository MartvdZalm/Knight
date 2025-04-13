package knight.compiler.ast;

import knight.compiler.lexer.Token;

/*
 * File: ASTExpression.java
 * @author: Mart van der Zalm
 * Date: 2025-04-10
 */
public abstract class ASTExpression extends AST
{
	private ASTType type;

	public ASTExpression(Token token)
	{
		super(token);
	}

	public ASTType getType()
	{
		return type;
	}

	public void setType(ASTType type)
	{
		this.type = type;
	}
}
