package knight.compiler.ast.expressions;

import knight.compiler.lexer.Token;
import knight.compiler.ast.ASTVisitor;

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