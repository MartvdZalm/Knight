package knight.compiler.ast.statements;

import java.util.List;

import knight.compiler.ast.AST;
import knight.compiler.ast.utils.ASTList;
import knight.compiler.ast.ASTVisitor;
import knight.compiler.lexer.Token;
import knight.compiler.semantics.model.Scope;

public class ASTBody extends ASTStatement
{
	private ASTList<AST> nodes;
	private Scope scope;

	public ASTBody(Token token, List<AST> nodes)
	{
		super(token);
		this.nodes = new ASTList<>(nodes);
	}

	public List<AST> getNodes()
	{
		return nodes.getList();
	}

	public AST getNode(int index)
	{
		return nodes.getAt(index);
	}

	public int getNodeCount()
	{
		return nodes.getSize();
	}

	public Scope getScope()
	{
		return scope;
	}

	public void setScope(Scope scope)
	{
		this.scope = scope;
	}

	@Override
	public <R> R accept(ASTVisitor<R> visitor)
	{
		return visitor.visit(this);
	}
}
