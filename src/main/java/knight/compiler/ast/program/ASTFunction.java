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
	private ASTIdentifier functionName;
	private ASTList<ASTArgument> argumentList;
	private ASTBody body;
	private boolean isAbstract;
	private boolean isStatic;
	private Scope scope;

	public ASTFunction(Token token, ASTType returnType, ASTIdentifier functionName, List<ASTArgument> argumentList,
			ASTBody body, boolean isAbstract, boolean isStatic)
	{
		super(token);

		this.returnType = returnType;
		this.functionName = functionName;
		this.argumentList = new ASTList<>(argumentList);
		this.body = body;
		this.isAbstract = isAbstract;
		this.isStatic = isStatic;
	}

	public ASTType getReturnType()
	{
		return returnType;
	}

	public ASTIdentifier getFunctionName()
	{
		return functionName;
	}

	public List<ASTArgument> getArgumentList()
	{
		return argumentList.getList();
	}

	public int getArgumentListSize()
	{
		return argumentList.getSize();
	}

	public ASTArgument getArgumentAt(int index)
	{
		return argumentList.getAt(index);
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
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}
}
