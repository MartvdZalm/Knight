package src.ast;

import java.util.List;

import src.lexer.Token;

public class Block extends StatementDecl
{
	private List<StatementDecl> body;

	public Block(Token token, List<StatementDecl> body)
	{
		super(token);
		this.body = body;
	}

	public int getStatListSize()
	{
		return body.size();
	}

	public StatementDecl getStatAt(int index)
	{
		if (index < body.size()) {
			return body.get(index);
		}
		return null;
	}

	public void setStatAt(int index, StatementDecl stat)
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