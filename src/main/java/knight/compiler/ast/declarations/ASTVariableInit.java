package knight.compiler.ast.declarations;

import knight.compiler.lexer.Token;
import knight.compiler.ast.ASTVisitor;
import knight.compiler.ast.types.ASTType;
import knight.compiler.ast.expressions.ASTExpression;

public class ASTVariableInit extends ASTVariable
{
	private ASTExpression expr;

    public ASTVariableInit(Token token, ASTType type, ASTIdentifier id, ASTExpression expr)
	{
		super(token, type, id);
		this.expr = expr;
	}

	public void setExpr(ASTExpression expr)
	{
		this.expr = expr;
	}

    public ASTExpression getExpr()
	{
		return expr;
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
    {
		return v.visit(this);
	}
}
