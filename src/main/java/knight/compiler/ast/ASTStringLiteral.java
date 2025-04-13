package knight.compiler.ast;

import knight.compiler.lexer.Token;

/*
 * File: ASTStringLiteral.java
 * @author: Mart van der Zalm
 * Date: 2025-04-10
 */
public class ASTStringLiteral extends ASTExpression
{
	private String value;

	public ASTStringLiteral(Token token, String value)
	{
		super(token);
		this.value = value;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}
}