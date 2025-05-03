package knight.compiler.ast;

import knight.compiler.lexer.Token;

public class ASTArrayAssign extends ASTStatement
{
	private ASTIdentifier identifier;
	private ASTExpression expression1;
	private ASTExpression expression2;

	public ASTArrayAssign(Token token, ASTIdentifier identifier, ASTExpression expression1, ASTExpression expression2)
	{
		super(token);
		this.identifier = identifier;
		this.expression1 = expression1;
		this.expression2 = expression2;
	}

	public ASTIdentifier getId()
	{
		return identifier;
	}

	public void setIdentifier(ASTIdentifier identifier)
	{
		this.identifier = identifier;
	}

	public ASTExpression getExpression1()
	{
		return expression1;
	}

	public void setE1(ASTExpression expression1)
	{
		this.expression1 = expression1;
	}

	public ASTExpression getExpression2()
	{
		return expression2;
	}

	public void setE2(ASTExpression expression2)
	{
		this.expression2 = expression2;
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}
}