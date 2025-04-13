package knight.compiler.ast;

import knight.compiler.lexer.Token;

/*
 * File: ASTIntLiteral.java
 * @author: Mart van der Zalm
 * Date: 2025-04-10
 */
public class ASTIntLiteral extends ASTExpression
{
	private int value;

	public ASTIntLiteral(Token token, int value)
	{
		super(token);
		this.value = value;
	}

	public int getValue()
	{
		return value;
	}

	public void setValue(int value)
	{
		this.value = value;
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}
}