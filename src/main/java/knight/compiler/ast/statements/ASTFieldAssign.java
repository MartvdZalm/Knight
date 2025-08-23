package knight.compiler.ast.statements;

import knight.compiler.ast.expressions.ASTExpression;
import knight.compiler.ast.expressions.ASTIdentifierExpr;
import knight.compiler.ast.ASTVisitor;
import knight.compiler.lexer.Token;

public class ASTFieldAssign extends ASTStatement
{
	private ASTIdentifierExpr instance;
	private ASTIdentifierExpr field;
	private ASTExpression value;

	public ASTFieldAssign(Token token, ASTIdentifierExpr instance, ASTIdentifierExpr field, ASTExpression value)
	{
		super(token);
		this.instance = instance;
		this.field = field;
		this.value = value;
	}

	public ASTIdentifierExpr getInstance()
	{
		return instance;
	}

	public ASTIdentifierExpr getField()
	{
		return field;
	}

	public ASTExpression getValue()
	{
		return value;
	}

	@Override
	public <R> R accept(ASTVisitor<R> visitor)
	{
		return visitor.visit(this);
	}
}
