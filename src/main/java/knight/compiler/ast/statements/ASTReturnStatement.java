package knight.compiler.ast.statements;

import knight.compiler.lexer.Token;
import knight.compiler.ast.expressions.ASTExpression;
import knight.compiler.ast.ASTVisitor;

public class ASTReturnStatement extends ASTStatement
{
    private ASTExpression returnExpr;

    public ASTReturnStatement(Token token, ASTExpression returnExpr)
    {
        super(token);
        this.returnExpr = returnExpr;
    }

    public ASTExpression getReturnExpr()
    {
        return returnExpr;
    }

    public void setReturnExpr(ASTExpression returnExpr)
    {
        this.returnExpr = returnExpr;
    }

    @Override
    public <R> R accept(ASTVisitor<R> v)
    {
        return v.visit(this);
    }
}
