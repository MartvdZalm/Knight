package knight.compiler.ast;

import java.util.List;

import knight.compiler.lexer.Token;

/*
 * File: ASTProgram.java
 * @author: Mart van der Zalm
 * Date: 2025-04-10
 */
public class ASTProgram extends AST
{
	private ASTList<ASTClass> classList;
	private ASTList<ASTFunction> functionList;
	private ASTList<ASTVariable> variableList;

	public ASTProgram(Token token, List<ASTClass> classList, List<ASTFunction> functionList,
			List<ASTVariable> variableList)
	{
		super(token);

		this.classList = new ASTList<>(classList);
		this.functionList = new ASTList<>(functionList);
		this.variableList = new ASTList<>(variableList);
	}

	public ASTProgram setClassList(List<ASTClass> classList)
	{
		this.classList = new ASTList<>(classList);
		return this;
	}

	public List<ASTClass> getClassList()
	{
		return classList.getList();
	}

	public int getClassListSize()
	{
		return classList.getSize();
	}

	public ASTClass getClassDeclAt(int index)
	{
		return classList.getAt(index);
	}

	public ASTProgram setFunctionList(List<ASTFunction> functionList)
	{
		this.functionList = new ASTList<>(functionList);
		return this;
	}

	public List<ASTFunction> getFunctionList()
	{
		return functionList.getList();
	}

	public int getFunctionListSize()
	{
		return functionList.getSize();
	}

	public ASTFunction getFunctionDeclAt(int index)
	{
		return functionList.getAt(index);
	}

	public ASTProgram setVariableList(List<ASTVariable> variableList)
	{
		this.variableList = new ASTList<>(variableList);
		return this;
	}

	public List<ASTVariable> getVariableList()
	{
		return variableList.getList();
	}

	public int getVariableListSize()
	{
		return variableList.getSize();
	}

	public ASTVariable getVariableDeclAt(int index)
	{
		return variableList.getAt(index);
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}
}
