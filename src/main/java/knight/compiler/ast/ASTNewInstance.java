package knight.compiler.ast;

import knight.compiler.lexer.Token;

import java.util.List;

public class ASTNewInstance extends ASTExpression
{
	private ASTIdentifierExpr className;
	private List<ASTArgument> arguments;

	public ASTNewInstance(Token token, ASTIdentifierExpr className, List<ASTArgument> arguments)
	{
		super(token);
		this.className = className;
		this.arguments = arguments;
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
		return arguments;
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}
}