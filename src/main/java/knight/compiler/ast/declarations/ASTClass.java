package knight.compiler.ast.declarations;

import java.util.List;

import knight.compiler.lexer.Token;
import knight.compiler.ast.AST;
import knight.compiler.ast.ASTVisitor;

public class ASTClass extends AST
{
	private ASTIdentifier id;
	private List<ASTFunction> functionList;
	private List<ASTVariable> variableList;

	public ASTClass(Token token, ASTIdentifier id, List<ASTFunction> functionList, List<ASTVariable> variableList)
	{
		super(token);
		this.id = id;
		this.functionList = functionList;
		this.variableList = variableList;
	}

	public ASTIdentifier getId()
	{
		return id;
	}

	public List<ASTFunction> getFunctionList()
	{
		return functionList;
	}

	public List<ASTVariable> getVariableList()
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

	public ASTFunction getFunctionDeclAt(int index)
	{
		if  (index < getFunctionListSize()) {
			return functionList.get(index);
		}
		return null;
	}

	public ASTVariable getVariableDeclAt(int index)
	{
		if (index < getVariableListSize()) {
			return variableList.get(index);
		}
		return null;
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}
}