package knight.compiler.ast.declarations;

import java.util.List;

import knight.compiler.lexer.Token;
import knight.compiler.ast.expressions.ASTExpression;
import knight.compiler.ast.statements.ASTStatement;
import knight.compiler.ast.types.ASTType;
import knight.compiler.ast.ASTVisitor;

public class ASTFunctionReturn extends ASTFunction
{
    private ASTExpression returnExpr;

    public ASTFunctionReturn(Token token, ASTType returnType, ASTIdentifier id, List<ASTArgument> argumentList, List<ASTVariable> variableList, List<ASTStatement> statementList, ASTExpression returnExpr)
    {
		super(token, returnType, id, argumentList, variableList, statementList);
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
