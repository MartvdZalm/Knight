package knight.compiler.ast;

import java.util.List;

import knight.compiler.lexer.Token;

/*
 * File: ASTIfChain.java
 * @author: Mart van der Zalm
 * Date: 2025-04-10
 */
public class ASTIfChain extends AST
{
	private List<ASTConditionalBranch> branches;
	private ASTBody elseBody;

	public ASTIfChain(Token token)
	{
		super(token);
	}

	public ASTIfChain setBranches(List<ASTConditionalBranch> branches)
	{
		this.branches = branches;
		return this;
	}

	public List<ASTConditionalBranch> getBranches()
	{
		return branches;
	}

	public ASTIfChain setElseBody(ASTBody elseBody)
	{
		this.elseBody = elseBody;
		return this;
	}

	public ASTBody getElseBody()
	{
		return elseBody;
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}
}
