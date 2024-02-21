package knight.compiler.ast;

import knight.compiler.lexer.Token;

public class Increment extends Expression
{
    private Expression expr;

    public Increment(Token token, Expression expr)
    {
        super(token);
        this.expr = expr;
    }

    public Expression getExpr()
    {
        return expr;
    }

    public void setExpr(Expression expr)
    {
        this.expr = expr;
    }

    @Override
    public <R> R accept(Visitor<R> v)
    {
        return v.visit(this);
    }
}
