package knight.compiler.ast.program;

import java.util.List;

import knight.compiler.ast.*;
import knight.compiler.ast.statements.ASTBody;
import knight.compiler.ast.types.ASTType;
import knight.compiler.ast.utils.ASTList;
import knight.compiler.lexer.Token;
import knight.compiler.semantics.model.Scope;

public class ASTFunction extends AST
{
	private ASTType returnType;
	private ASTIdentifier identifier;
	private ASTList<ASTArgument> arguments;
	private ASTBody body;
	private boolean isAbstract;
	private boolean isStatic;
	private Scope scope;

	public ASTFunction(Token token, ASTType returnType, ASTIdentifier identifier, List<ASTArgument> arguments,
			ASTBody body, boolean isAbstract, boolean isStatic)
	{
		super(token);

		this.returnType = returnType;
		this.identifier = identifier;
		this.arguments = new ASTList<>(arguments);
		this.body = body;
		this.isAbstract = isAbstract;
		this.isStatic = isStatic;
	}

	public ASTType getReturnType()
	{
		return returnType;
	}

	public ASTIdentifier getIdentifier()
	{
		return identifier;
	}

	public void setIdentifier(ASTIdentifier identifier)
	{
		this.identifier = identifier;
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

	public boolean isAbstract()
	{
		return isAbstract;
	}

	public boolean isStatic()
	{
		return isStatic;
	}

	public void setScope(Scope scope)
	{
		this.scope = scope;
	}

	public Scope getScope()
	{
		return scope;
	}

	@Override
	public <R> R accept(ASTVisitor<R> visitor)
	{
		return visitor.visit(this);
	}
}
