package knight.compiler.ast;

import knight.compiler.lexer.Token;

public class ReturnStatement extends Statement
{
    private Expression returnExpr;

    public ReturnStatement(Token token, Expression returnExpr)
    {
        super(token);
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
