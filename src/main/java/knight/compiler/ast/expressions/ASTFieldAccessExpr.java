package knight.compiler.ast.expressions;

import knight.compiler.ast.ASTVisitor;
import knight.compiler.lexer.Token;

public class ASTFieldAccessExpr extends ASTExpression
{
	private final ASTIdentifierExpr instance;
	private final ASTIdentifierExpr field;

	public ASTFieldAccessExpr(Token token, ASTIdentifierExpr instance, ASTIdentifierExpr field)
	{
		super(token);
		this.instance = instance;
		this.field = field;
	}

	public ASTIdentifierExpr getInstance()
	{
		return instance;
	}

	public ASTIdentifierExpr getField()
	{
		return field;
	}

	@Override
	public <R> R accept(ASTVisitor<R> visitor)
	{
		return visitor.visit(this);
	}
}
