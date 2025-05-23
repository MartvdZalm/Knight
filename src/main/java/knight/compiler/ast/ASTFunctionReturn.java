package knight.compiler.ast;

import java.util.List;

import knight.compiler.ast.types.ASTType;
import knight.compiler.lexer.Token;

public class ASTFunctionReturn extends ASTFunction
{
	private ASTExpression returnExpr;

	public ASTFunctionReturn(Token token, ASTType returnType, ASTIdentifier id, List<ASTArgument> argumentList,
			ASTBody body)
	{
		super(token, returnType, id, argumentList, body);
		// this.returnExpr = returnExpr;
	}

	public ASTExpression getReturnExpr()
	{
		return returnExpr;
	}

	public void setReturnExpr(ASTExpression returnExpr)
	{
		this.returnExpr = returnExpr;
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}
}
