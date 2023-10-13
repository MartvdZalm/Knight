package src.ast;

import java.util.List;

import src.lexer.Token;

public class FunctionReturn extends FunctionDecl
{
    private Expression returnExpr;

    public FunctionReturn(Token token, Type returnType, Identifier functionName, List<ArgDecl> argList, List<Declaration> declList, Expression returnExpr)
    {
		super(token, returnType, functionName, argList, declList);
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
