package knight.compiler.ast.statements;

import java.util.List;

import knight.compiler.lexer.Token;
import knight.compiler.ast.ASTVisitor;

public class ASTBlock extends ASTStatement
{
	private List<ASTStatement> body;

	public ASTBlock(Token token, List<ASTStatement> body)
	{
		super(token);
		this.body = body;
	}

	public int getStatListSize()
	{
		return body.size();
	}

	public ASTStatement getStatAt(int index)
	{
		if (index < body.size()) {
			return body.get(index);
		}
		return null;
	}

	public void setStatAt(int index, ASTStatement stat)
	{
		if (index < body.size()) {
			body.set(index, stat);
		}
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}
}