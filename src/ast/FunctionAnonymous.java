package src.ast;

import java.util.List;

import src.lexer.Token;

public class FunctionAnonymous extends FunctionDecl
{
    private Expression returnExpr;

    public FunctionAnonymous(Token token, Token access, Type returnType, IdentifierExpr functionName, List<ArgDecl> argList, List<Declaration> declList, Expression returnExpr)
    {
		super(token, access, returnType, functionName, argList, declList);
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
