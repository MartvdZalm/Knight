package knight.compiler.ast.program;

import java.util.List;

import knight.compiler.ast.AST;
import knight.compiler.ast.utils.ASTList;
import knight.compiler.ast.ASTVisitor;
import knight.compiler.lexer.Token;

public class ASTProgram extends AST
{
	private ASTList<ASTImport> imports;
	private ASTList<AST> nodes;

	public ASTProgram(Token token, List<ASTImport> imports, List<AST> nodes)
	{
		super(token);
		this.imports = new ASTList<>(imports);
		this.nodes = new ASTList<>(nodes);
	}

	public void setImports(List<ASTImport> imports)
	{
		this.imports = new ASTList<>(imports);
	}

	public List<ASTImport> getImports()
	{
		return imports.getList();
	}

	public int getImportCount()
	{
		return imports.getSize();
	}

	public ASTImport getImport(int index)
	{
		return imports.getAt(index);
	}

	public void setNodes(List<AST> nodes)
	{
		this.nodes = new ASTList<>(nodes);
	}

	public List<AST> getNodes()
	{
		return nodes.getList();
	}

	public int getNodeCount()
	{
		return nodes.getSize();
	}

	public AST getNode(int index)
	{
		return nodes.getAt(index);
	}

	@Override
	public <R> R accept(ASTVisitor<R> visitor)
	{
		return visitor.visit(this);
	}
}
