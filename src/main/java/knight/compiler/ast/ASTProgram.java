package knight.compiler.ast;

import java.util.List;
import knight.compiler.lexer.Token;

public class ASTProgram extends AST
{
	private ASTList<ASTImport> importList;
	private ASTList<AST> nodeList;

	public ASTProgram(Token token, List<ASTImport> importList, List<AST> nodeList)
	{
		super(token);

		this.importList = new ASTList<>(importList);
		this.nodeList = new ASTList<>(nodeList);
	}

	public void setImportList(List<ASTImport> importList)
	{
		this.importList = new ASTList<>(importList);
	}

	public List<ASTImport> getImportList()
	{
		return importList.getList();
	}

	public int getImportListSize()
	{
		return importList.getSize();
	}

	public AST getImportAt(int index)
	{
		return importList.getAt(index);
	}

	public void setNodeList(List<AST> nodeList)
	{
		this.nodeList = new ASTList<>(nodeList);
	}

	public List<AST> getNodeList()
	{
		return nodeList.getList();
	}

	public int getNodeListSize()
	{
		return nodeList.getSize();
	}

	public AST getNodeAt(int index)
	{
		return nodeList.getAt(index);
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}
}
