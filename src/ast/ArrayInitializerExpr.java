package src.ast;

import src.lexer.Token;

/*
 * This class is for initializing a size to an array. 
 * For example 'int[] array = [10];' Here you're giving the array a size of 10.
 */
public class ArrayInitializerExpr extends Expression
{
    private Expression arraySize;

    public ArrayInitializerExpr(Token token, Expression arraySize)
    {
        super(token);
        this.arraySize = arraySize;
    }

    public Expression getArraySize()
    {
        return arraySize;
    }

    public void setArraySize(Expression arraySize)
    {
        this.arraySize = arraySize;
    }

    @Override
    public <R> R accept(Visitor<R> v)
    {
        return v.visit(this);
    }   
}
