package knight.compiler.ast;

import java.util.List;

import knight.compiler.lexer.Token;

/*
 * File: ASTArrayLiteral.java
 * @author: Mart van der Zalm
 * Date: 2025-04-28
 */
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
