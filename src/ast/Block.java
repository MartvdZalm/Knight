package src.ast;

import java.util.List;

import src.lexer.Token;

public class Block extends Statement
{
	private List<Statement> body;

	public Block(Token token, List<Statement> body)
	{
		super(token);
		this.body = body;
	}

	public int getStatListSize()
	{
		return body.size();
	}

	public Statement getStatAt(int index)
	{
		if (index < body.size()) {
			return body.get(index);
		}
		return null;
	}

	public void setStatAt(int index, Statement stat)
	{
		if (index < body.size()) {
			body.set(index, stat);
		}
	}

	@Override
	public <R> R accept(Visitor<R> v)
	{
		return v.visit(this);
	}
}