package knight.compiler.ast.statements.conditionals;

import knight.compiler.lexer.Token;
import knight.compiler.ast.expressions.ASTExpression;
import knight.compiler.ast.statements.ASTStatement;
import knight.compiler.ast.ASTVisitor;

public class ASTIfThenElse extends ASTStatement
{
	private ASTExpression expr;
	private ASTStatement then;
	private ASTStatement elze; 

	public ASTIfThenElse(Token token, ASTExpression expr, ASTStatement then, ASTStatement elze)
	{
		super(token);
		this.expr = expr;
		this.then = then;
		this.elze = elze;
	}

	public ASTExpression getExpr()
	{
		return expr;
	}

	public void setExpr(ASTExpression expr)
	{
		this.expr = expr;
	}

	public ASTStatement getThen()
	{
		return then;
	}

	public void setThen(ASTStatement then)
	{
		this.then = then;
	}

	public ASTStatement getElze()
	{
		return elze;
	}

	public void setElze(ASTStatement elze)
	{
		this.elze = elze;
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}
}