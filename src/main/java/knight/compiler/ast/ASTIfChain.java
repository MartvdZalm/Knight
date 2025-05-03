package knight.compiler.ast;

import java.util.List;

import knight.compiler.lexer.Token;

public class ASTIfChain extends ASTStatement
{
	private ASTList<ASTConditionalBranch> branches;
	private ASTBody elseBody;

	public ASTIfChain(Token token, List<ASTConditionalBranch> branches, ASTBody elseBody)
	{
		super(token);
		this.branches = new ASTList<>(branches);
		this.elseBody = elseBody;
	}

	public ASTIfChain setBranches(List<ASTConditionalBranch> branches)
	{
		this.branches = new ASTList<>(branches);
		return this;
	}

	public List<ASTConditionalBranch> getBranches()
	{
		return branches.getList();
	}

	public ASTConditionalBranch getBranchAt(int index)
	{
		return branches.getAt(index);
	}

	public int getBranchListSize()
	{
		return branches.getSize();
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
