package src.ast;

import java.util.List;
import src.lexer.Token;

public class ClassDecl extends Tree
{
	private Identifier id;
	private List<FunctionDecl> functionList;
	private List<VariableDecl> variableList;

	public ClassDecl(Token token, Identifier id, List<FunctionDecl> functionList, List<VariableDecl> variableList)
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

	public List<FunctionDecl> getFunctionList()
	{
		return functionList;
	}

	public List<VariableDecl> getVariableList()
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

	public FunctionDecl getFunctionDeclAt(int index)
	{
		if  (index < getFunctionListSize()) {
			return functionList.get(index);
		}
		return null;
	}

	public VariableDecl getVariableDeclAt(int index)
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