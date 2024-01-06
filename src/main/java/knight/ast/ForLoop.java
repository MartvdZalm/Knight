package knight.ast;

import knight.lexer.Token;

public class ForLoop extends Statement
{
	private Variable initialization;
	private Expression condition;
	private Statement increment;
	private Statement body;

	public ForLoop(Token token, Variable initialization, Expression condition, Statement increment, Statement body)
	{
		super(token);
		this.initialization = initialization;
		this.condition = condition;
		this.increment = increment;
		this.body = body;
	}

	public Statement getBody()
	{
		return body;
	}

	public void setBody(Statement body)
	{
		this.body = body;
	}

	@Override
	public <R> R accept(Visitor<R> v)
	{
		return v.visit(this);
	}
}