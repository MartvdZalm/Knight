package knight.compiler.ast.expressions;

import knight.compiler.ast.ASTVisitor;
import knight.compiler.lexer.Token;

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
	public <R> R accept(ASTVisitor<R> visitor)
	{
		return visitor.visit(this);
	}
}
