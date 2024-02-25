package knight.compiler.ast.declarations;

import java.util.List;

import knight.compiler.lexer.Token;
import knight.compiler.ast.AST;
import knight.compiler.ast.ASTVisitor;

public class ASTProgram extends AST
{
	private List<ASTInclude> includeList;
	private List<ASTEnumeration> enumList;
	private List<ASTInterface> interList;
	private List<ASTClass> classList;
	private List<ASTFunction> functionList;
	private List<ASTVariable> variableList;

	public ASTProgram(Token token, List<ASTInclude> includeList, List<ASTEnumeration> enumList, List<ASTInterface> interList, List<ASTClass> classList, List<ASTFunction> functionList, List<ASTVariable> variableList)
	{
		super(token);
		this.includeList = includeList;
		this.enumList = enumList;
		this.interList = interList;
		this.classList = classList;
		this.functionList = functionList;
		this.variableList = variableList;
	}

	public List<ASTInclude> getIncludeList()
	{
		return includeList;
	}

	public List<ASTEnumeration> getEnumList()
	{
		return enumList;
	}

	public List<ASTInterface> getInterList()
	{
		return interList;
	}

	public List<ASTClass> getClassList()
	{
		return classList;
	}

	public List<ASTFunction> getFunctionList()
	{
		return functionList;
	}

	public List<ASTVariable> getVariableList()
	{
		return variableList;
	}

	public int getIncludeListSize()
	{
		return includeList.size();
	}

	public int getEnumListSize()
	{
		return enumList.size();
	}

	public int getInterListSize()
	{
		return interList.size();
	}

	public int getClassListSize()
	{
		return classList.size();
	}

	public int getFunctionListSize()
	{
		return functionList.size();
	}

	public int getVariableListSize()
	{
		return variableList.size();
	}

	public ASTInclude getIncludeDeclAt(int index)
	{
		if (index < getIncludeListSize()) {
			return includeList.get(index);
		}
		return null;
	}

	public ASTEnumeration getEnumDeclAt(int index)
	{
		if (index < getEnumListSize()) {
			return enumList.get(index);
		}
		return null;
	}

	public ASTInterface getInterDeclAt(int index)
	{
		if (index < getInterListSize()) {
			return interList.get(index);
		}
		return null;
	}

	public ASTClass getClassDeclAt(int index)
	{
		if (index < getClassListSize()) {
			return classList.get(index);
		}
		return null;
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
