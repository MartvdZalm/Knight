package knight.compiler.ast.statements;

import knight.compiler.ast.expressions.ASTExpression;
import knight.compiler.ast.program.ASTIdentifier;
import knight.compiler.ast.ASTVisitor;
import knight.compiler.lexer.Token;

public class ASTAssign extends ASTStatement
{
	private ASTIdentifier identifier;
	private ASTExpression expression;

	public ASTAssign(Token token, ASTIdentifier identifier, ASTExpression expression)
	{
		super(token);
		this.identifier = identifier;
		this.expression = expression;
	}

	public ASTIdentifier getIdentifier()
	{
		return identifier;
	}

	public void setIdentifier(ASTIdentifier identifier)
	{
		this.identifier = identifier;
	}

	public ASTExpression getExpression()
	{
		return expression;
	}

	public void setExpression(ASTExpression expression)
	{
		this.expression = expression;
	}

	@Override
	public <R> R accept(ASTVisitor<R> visitor)
	{
		return visitor.visit(this);
	}
}
