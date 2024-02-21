package knight.compiler.ast;

import java.util.List;
import knight.compiler.lexer.Token;

public class Class extends Tree
{
	private Identifier id;
	private List<Function> functionList;
	private List<Variable> variableList;

	public Class(Token token, Identifier id, List<Function> functionList, List<Variable> variableList)
	{
		super(token);
		this.id = id;
		this.functionList = functionList;
		this.variableList = variableList;
	}

	public Identifier getId()
	{
		return id;
	}

	public List<Function> getFunctionList()
	{
		return functionList;
	}

	public List<Variable> getVariableList()
	{
		return variableList;
	}

	public int getFunctionListSize()
	{
		return functionList.size();
	}

	public int getVariableListSize()
	{
		return variableList.size();
	}

	public Function getFunctionDeclAt(int index)
	{
		if  (index < getFunctionListSize()) {
			return functionList.get(index);
		}
		return null;
	}

	public Variable getVariableDeclAt(int index)
	{
		if (index < getVariableListSize()) {
			return variableList.get(index);
		}
		return null;
	}

	@Override
	public <R> R accept(Visitor<R> v)
	{
		return v.visit(this);
	}
}