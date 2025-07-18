package knight.compiler.ast.expressions;

import java.util.List;

import knight.compiler.ast.utils.ASTList;
import knight.compiler.ast.ASTVisitor;
import knight.compiler.lexer.Token;

public class ASTArrayLiteral extends ASTExpression
{
	private ASTList<ASTExpression> expressions;

	public ASTArrayLiteral(Token token, List<ASTExpression> expressions)
	{
		super(token);
		this.expressions = new ASTList<>(expressions);
	}

	public List<ASTExpression> getExpressionList()
	{
		return expressions.getList();
	}

	public int getExpressionListSize()
	{
		return expressions.getSize();
	}

	public ASTExpression getExpressionAt(int index)
	{
		return expressions.getAt(index);
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}
}
