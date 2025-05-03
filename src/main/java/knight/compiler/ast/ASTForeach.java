package knight.compiler.ast;

import knight.compiler.lexer.Token;

public class ASTForeach extends ASTStatement
{
	private ASTVariable variable;
	private ASTExpression iterable;
	private ASTBody body;

	public ASTForeach(Token token, ASTVariable variable, ASTExpression iterable, ASTBody body)
	{
		super(token);
		this.variable = variable;
		this.iterable = iterable;
		this.body = body;
	}

	public ASTVariable getVariable()
	{
		return variable;
	}

	public void setVariable(ASTVariable variable)
	{
		this.variable = variable;
	}

	public ASTExpression getIterable()
	{
		return iterable;
	}

	public void setIterable(ASTExpression iterable)
	{
		this.iterable = iterable;
	}

	public ASTBody getBody()
	{
		return body;
	}

	public void setBody(ASTBody body)
	{
		this.body = body;
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}
}
