package knight.compiler.ast;

import knight.compiler.lexer.Token;

public class ASTAssign extends ASTStatement
{
	private ASTIdentifier identifier;
	private ASTExpression expr;

	public ASTAssign(Token token, ASTIdentifier identifier, ASTExpression expr)
	{
		super(token);
		this.identifier = identifier;
		this.expr = expr;
	}

	public ASTIdentifier getIdentifier()
	{
		return identifier;
	}

	public void setIdentifier(ASTIdentifier identifier)
	{
		this.identifier = identifier;
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
