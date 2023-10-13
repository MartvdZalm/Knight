package src.ast;

import java.util.List;

import src.lexer.Token;

public class FunctionVoid extends FunctionDecl
{
    public FunctionVoid(Token token, Type returnType, Identifier functionName, List<ArgDecl> argList, List<Declaration> declList)
    {
       	super(token, returnType, functionName, argList, declList);
    }

    @Override
    public <R> R accept(Visitor<R> v)
    {
        return v.visit(this);
    }   
}
