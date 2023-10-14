package src.ast;

import java.util.List;
import src.lexer.Token;

public class ClassDeclInheritance extends ClassDecl
{
	private List<InheritanceDecl> inheritanceList;

	public ClassDeclInheritance(Token token, Identifier id, List<FunctionDecl> functionList, List<VariableDecl> variableList, List<InheritanceDecl> inheritanceList)
	{
		super(token, id, functionList, variableList);
		this.inheritanceList = inheritanceList;
	}

	public List<InheritanceDecl> getInheritanceList()
	{
		return inheritanceList;
	}

	public int getInheritanceListSize()
	{
		return inheritanceList.size();
	}

	public InheritanceDecl getInheritanceAt(int index)
	{
		if (index < getInheritanceListSize()) {
			return inheritanceList.get(index);
		}
		return null;
	}

	@Override
	public <R> R accept(Visitor<R> v)
	{
		return v.visit(this);
	}
}