package knight.compiler.ast.expressions;

import java.util.List;

import knight.compiler.ast.utils.ASTList;
import knight.compiler.ast.ASTVisitor;
import knight.compiler.lexer.Token;
import knight.compiler.ast.contracts.IASTCallFunction;

public class ASTCallFunctionExpr extends ASTExpression implements IASTCallFunction
{
	private ASTIdentifierExpr instance;
	private ASTIdentifierExpr functionName;
	private ASTList<ASTExpression> arguments;

	public ASTCallFunctionExpr(Token token, ASTIdentifierExpr instance, ASTIdentifierExpr functionName,
			List<ASTExpression> arguments)
	{
		super(token);
		this.instance = instance;
		this.functionName = functionName;
		this.arguments = new ASTList<>(arguments);
	}

	public ASTIdentifierExpr getInstance()
	{
		return instance;
	}

	public void setInstance(ASTIdentifierExpr instance)
	{
		this.instance = instance;
	}

	public ASTIdentifierExpr getFunctionName()
	{
		return functionName;
	}

	public void setFunctionName(ASTIdentifierExpr functionName)
	{
		this.functionName = functionName;
	}

	public void setArguments(List<ASTExpression> arguments)
	{
		this.arguments = new ASTList<>(arguments);
	}

	public List<ASTExpression> getArguments()
	{
		return arguments.getList();
	}

	public int getArgumentCount()
	{
		return arguments.getSize();
	}

	public ASTExpression getArgument(int index)
	{
		return arguments.getAt(index);
	}

	@Override
	public <R> R accept(ASTVisitor<R> visitor)
	{
		return visitor.visit(this);
	}
}
