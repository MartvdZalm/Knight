package knight.compiler.ast;

import java.util.List;

import knight.compiler.lexer.Token;
import knight.compiler.passes.symbol.model.Scope;

/*
 * File: ASTBody.java
 * @author: Mart van der Zalm
 * Date: 2025-04-10
 */
public class ASTBody extends ASTStatement
{
	private ASTList<ASTStatement> statementList;
	private ASTList<ASTVariable> variableList;
	private Scope scope;

	public ASTBody(Token token, List<ASTVariable> variableList, List<ASTStatement> statementList)
	{
		super(token);

		this.variableList = new ASTList<>(variableList);
		this.statementList = new ASTList<>(statementList);
	}

	public List<ASTVariable> getVariableList()
	{
		return variableList.getList();
	}

	public List<ASTStatement> getStatementList()
	{
		return statementList.getList();
	}

	public int getVariableListSize()
	{
		return variableList.getSize();
	}

	public int getStatementListSize()
	{
		return statementList.getSize();
	}

	public ASTVariable getVariableAt(int index)
	{
		return variableList.getAt(index);
	}

	public ASTStatement getStatementAt(int index)
	{
		return statementList.getAt(index);
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
