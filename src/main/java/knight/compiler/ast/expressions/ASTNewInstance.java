package knight.compiler.ast.expressions;

import knight.compiler.ast.program.ASTArgument;
import knight.compiler.ast.ASTVisitor;
import knight.compiler.lexer.Token;

import java.util.List;
import knight.compiler.ast.utils.ASTList;

public class ASTNewInstance extends ASTExpression
{
	private ASTIdentifierExpr className;
	private ASTList<ASTArgument> arguments;

	public ASTNewInstance(Token token, ASTIdentifierExpr className, List<ASTArgument> arguments)
	{
		super(token);
		this.className = className;
		this.arguments = new ASTList<>(arguments);
	}

	public ASTIdentifierExpr getClassName()
	{
		return className;
	}

	public void setClassName(ASTIdentifierExpr className)
	{
		this.className = className;
	}

	public List<ASTArgument> getArguments()
	{
		return arguments.getList();
	}

	public int getArgumentCount()
	{
		return arguments.getSize();
	}

	public ASTArgument getArgument(int index)
	{
		return arguments.getAt(index);
	}

	@Override
	public <R> R accept(ASTVisitor<R> visitor)
	{
		return visitor.visit(this);
	}
}
