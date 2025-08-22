package knight.compiler.ast.controlflow;

import java.util.List;

import knight.compiler.ast.utils.ASTList;
import knight.compiler.ast.ASTVisitor;
import knight.compiler.ast.statements.ASTBody;
import knight.compiler.ast.statements.ASTStatement;
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

	public void setBranches(List<ASTConditionalBranch> branches)
	{
		this.branches = new ASTList<>(branches);
	}

	public List<ASTConditionalBranch> getBranches()
	{
		return branches.getList();
	}

	public ASTConditionalBranch getBranch(int index)
	{
		return branches.getAt(index);
	}

	public int getBranchCount()
	{
		return branches.getSize();
	}

	public void setElseBody(ASTBody elseBody)
	{
		this.elseBody = elseBody;
	}

	public ASTBody getElseBody()
	{
		return elseBody;
	}

	@Override
	public <R> R accept(ASTVisitor<R> visitor)
	{
		return visitor.visit(this);
	}
}
