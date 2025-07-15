package knight.compiler.ast;

import knight.compiler.ast.types.ASTType;
import knight.compiler.lexer.Token;

public class ASTVariableInit extends ASTVariable
{
	private ASTExpression expression;

	public ASTVariableInit(Token token, ASTType type, ASTIdentifier identifier, ASTExpression expression,
			boolean isStatic)
	{
		super(token, type, identifier, isStatic);
		this.expression = expression;
	}

	public void setExpr(ASTExpression expression)
	{
		this.expression = expression;
	}

	public ASTExpression getExpr()
	{
		return expression;
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}
}
