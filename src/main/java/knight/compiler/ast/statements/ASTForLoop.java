package knight.compiler.ast.statements;

import knight.compiler.lexer.Token;
import knight.compiler.ast.declarations.ASTVariable;
import knight.compiler.ast.expressions.ASTExpression;
import knight.compiler.ast.ASTVisitor;

public class ASTForLoop extends ASTStatement
{
	private ASTVariable initialization;
	private ASTExpression condition;
	private ASTStatement increment;
	private ASTStatement body;

	public ASTForLoop(Token token, ASTVariable initialization, ASTExpression condition, ASTStatement increment, ASTStatement body)
	{
		super(token);
		this.initialization = initialization;
		this.condition = condition;
		this.increment = increment;
		this.body = body;
	}

	public ASTStatement getBody()
	{
		return body;
	}

	public void setBody(ASTStatement body)
	{
		this.body = body;
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}
}