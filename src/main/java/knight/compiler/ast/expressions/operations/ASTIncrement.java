package knight.compiler.ast.expressions.operations;

import knight.compiler.lexer.Token;
import knight.compiler.ast.expressions.ASTExpression;
import knight.compiler.ast.ASTVisitor;

public class ASTIncrement extends ASTExpression
{
    private ASTExpression expr;

    public ASTIncrement(Token token, ASTExpression expr)
    {
        super(token);
        this.expr = expr;
    }

    public ASTExpression getExpr()
    {
        return expr;
    }

    public void setExpr(ASTExpression expr)
    {
        this.expr = expr;
    }

    @Override
    public <R> R accept(ASTVisitor<R> v)
    {
        return v.visit(this);
    }
}
