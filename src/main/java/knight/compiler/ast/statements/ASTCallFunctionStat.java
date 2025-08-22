package knight.compiler.ast.statements;

import java.util.List;

import knight.compiler.ast.*;
import knight.compiler.ast.expressions.ASTExpression;
import knight.compiler.ast.expressions.ASTIdentifierExpr;
import knight.compiler.ast.utils.ASTList;
import knight.compiler.lexer.Token;
import knight.compiler.ast.contracts.IASTCallFunction;

public class ASTCallFunctionStat extends ASTStatement implements IASTCallFunction
{
	private ASTIdentifierExpr instance;
	private ASTIdentifierExpr functionName;
	private ASTList<ASTExpression> arguments;

	public ASTCallFunctionStat(Token token, ASTIdentifierExpr instance, ASTIdentifierExpr functionName,
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

	public List<ASTExpression> getArguments()
	{
		return arguments.getList();
	}

	public void setArguments(List<ASTExpression> arguments)
	{
		this.arguments = new ASTList<>(arguments);
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
