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
	private List<ASTExpression> expressions;

	public ASTArrayLiteral(Token token, List<ASTExpression> expressions)
	{
		super(token);
		this.expressions = expressions;
	}

	public List<ASTExpression> getExpressions()
	{
		return expressions;
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}
}
