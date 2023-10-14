package src.ast;

import java.util.List;

import src.lexer.Token;

public class FunctionDeclReturn extends FunctionDecl
{
    private Expression returnExpr;

    public FunctionDeclReturn(Token token, Type returnType, Identifier id, List<ArgumentDecl> argumentList, List<VariableDecl> variableList, List<StatementDecl> statementList, Expression returnExpr)
    {
		super(token, returnType, id, argumentList, variableList, statementList);
        this.returnExpr = returnExpr;
    }

    public Expression getReturnExpr()
    {
        return returnExpr;
    }

    public void setReturnExpr(Expression returnExpr)
    {
        this.returnExpr = returnExpr;
    }

    @Override
    public <R> R accept(Visitor<R> v)
    {
        return v.visit(this);
    }
}
