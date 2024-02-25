package knight.compiler.ast.statements;

import knight.compiler.lexer.Token;
import knight.compiler.ast.declarations.ASTIdentifier;
import knight.compiler.ast.expressions.ASTExpression;
import knight.compiler.ast.ASTVisitor;

public class ASTArrayAssign extends ASTStatement
{
	private ASTIdentifier identifier;
	private ASTExpression e1;
	private ASTExpression e2;

	public ASTArrayAssign(Token token, ASTIdentifier identifier, ASTExpression e1, ASTExpression e2)
	{
		super(token);
		this.identifier = identifier;
		this.e1 = e1;
		this.e2 = e2;
	}

	public ASTIdentifier getIdentifier()
	{
		return identifier;
	}

	public void setIdentifier(ASTIdentifier identifier)
	{
		this.identifier = identifier;
	}

	public ASTExpression getE1()
	{
		return e1;
	}

	public void setE1(ASTExpression e1)
	{
		this.e1 = e1;
	}

	public ASTExpression getE2()
	{
		return e2;
	}

	public void setE2(ASTExpression e2)
	{
		this.e2 = e2;
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}
}