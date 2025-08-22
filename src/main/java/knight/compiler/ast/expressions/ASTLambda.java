package knight.compiler.ast.expressions;

import java.util.List;

import knight.compiler.ast.program.ASTArgument;
import knight.compiler.ast.utils.ASTList;
import knight.compiler.ast.ASTVisitor;
import knight.compiler.ast.statements.ASTBody;
import knight.compiler.ast.types.ASTType;
import knight.compiler.lexer.Token;

public class ASTLambda extends ASTExpression
{
	private ASTType returnType;
	private ASTList<ASTArgument> arguments;
	private ASTBody body;

	public ASTLambda(Token token, ASTType returnType, List<ASTArgument> arguments, ASTBody body)
	{
		super(token);
		this.returnType = returnType;
		this.arguments = new ASTList<>(arguments);
		this.body = body;
	}

	public ASTType getReturnType()
	{
		return returnType;
	}

	public void setReturnType(ASTType returnType)
	{
		this.returnType = returnType;
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

	public ASTBody getBody()
	{
		return body;
	}

	public void setBody(ASTBody body)
	{
		this.body = body;
	}

	@Override
	public <R> R accept(ASTVisitor<R> visitor)
	{
		return visitor.visit(this);
	}
}
