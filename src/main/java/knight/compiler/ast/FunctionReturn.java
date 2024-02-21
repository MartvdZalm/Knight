package knight.compiler.ast;

import java.util.List;

import knight.compiler.lexer.Token;

public class FunctionReturn extends Function
{
    private Expression returnExpr;

    public FunctionReturn(Token token, Type returnType, Identifier id, List<Argument> argumentList, List<Variable> variableList, List<Statement> statementList, Expression returnExpr)
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
