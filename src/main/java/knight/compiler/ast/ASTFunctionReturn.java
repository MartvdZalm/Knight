package knight.compiler.ast;

import java.util.List;

import knight.compiler.lexer.Token;

/*
 * File: ASTFunctionReturn.java
 * @author: Mart van der Zalm
 * Date: 2025-04-10
 */
public class ASTFunctionReturn extends ASTFunction
{
	private ASTExpression returnExpr;

	public ASTFunctionReturn(Token token, ASTType returnType, ASTIdentifier id, List<ASTArgument> argumentList,
			ASTBody body, ASTExpression returnExpr)
	{
		super(token, returnType, id, argumentList, body);
		this.returnExpr = returnExpr;
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
