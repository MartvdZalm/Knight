package knight.compiler.ast.declarations;

import java.util.List;

import knight.compiler.lexer.Token;
import knight.compiler.ast.AST;
import knight.compiler.ast.ASTVisitor;
import knight.compiler.ast.statements.ASTStatement;
import knight.compiler.ast.types.ASTType;

public class ASTFunction extends AST
{
    private ASTType returnType;
    private ASTIdentifier id;
    private List<ASTArgument> argumentList;
    private List<ASTVariable> variableList;
	private List<ASTStatement> statementList;

    public ASTFunction(Token token, ASTType returnType, ASTIdentifier id, List<ASTArgument> argumentList, List<ASTVariable> variableList, List<ASTStatement> statementList)
    {
        super(token);
        this.returnType = returnType;
        this.id = id;
        this.argumentList = argumentList;
        this.variableList = variableList;
        this.statementList = statementList;
    }

    public ASTType getReturnType()
    {
        return returnType;
    }

    public ASTIdentifier getId()
    {
    	return id;
    }

    public List<ASTArgument> getArgumentList()
    {
    	return argumentList;
    }

    public List<ASTVariable> getVariableList()
    {
    	return variableList;
    }

    public List<ASTStatement> getStatementList()
    {
    	return statementList;
    }

	public int getArgumentListSize()
	{
		return argumentList.size();
	}

	public int getVariableListSize()
	{
		return variableList.size();
	}

	public int getStatementListSize()
	{
		return statementList.size();
	}

	public ASTArgument getArgumentDeclAt(int index)
	{
		if (index < getArgumentListSize()) {
			return argumentList.get(index);
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

	public ASTStatement getStatementDeclAt(int index)
	{
		if (index < getStatementListSize()) {
			return statementList.get(index);
		}
		return null;
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}
}
