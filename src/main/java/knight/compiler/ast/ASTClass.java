package knight.compiler.ast;

import java.util.List;

import knight.compiler.lexer.Token;

public class ASTClass extends AST
{
	private ASTIdentifier className;
	private ASTList<ASTFunction> functionList;
	private ASTList<ASTProperty> propertyList;

	public ASTClass(Token token, ASTIdentifier className, List<ASTProperty> propertyList,
			List<ASTFunction> functionList)
	{
		super(token);

		this.className = className;
		this.functionList = new ASTList<>(functionList);
		this.propertyList = new ASTList<>(propertyList);
	}

	public ASTClass setClassName(ASTIdentifier className)
	{
		this.className = className;
		return this;
	}

	public ASTIdentifier getClassName()
	{
		return className;
	}

	public ASTClass setFunctionList(List<ASTFunction> functionList)
	{
		this.functionList.setList(functionList);
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

	public ASTClass setPropertyList(List<ASTProperty> propertyList)
	{
		this.propertyList.setList(propertyList);
		return this;
	}

	public List<ASTProperty> getPropertyList()
	{
		return propertyList.getList();
	}

	public int getPropertyListSize()
	{
		return propertyList.getSize();
	}

	public ASTProperty getPropertyDeclAt(int index)
	{
		return propertyList.getAt(index);
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}
}
