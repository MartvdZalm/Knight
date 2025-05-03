package knight.compiler.ast;

import knight.compiler.lexer.Token;

public class ASTNewArray extends ASTExpression
{
	private ASTExpression arrayLength;

	public ASTNewArray(Token token, ASTExpression arrayLength)
	{
		super(token);
		this.arrayLength = arrayLength;
	}

	public ASTExpression getArrayLength()
	{
		return arrayLength;
	}

	public void setArrayLength(ASTExpression arrayLength)
	{
		this.arrayLength = arrayLength;
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}
}