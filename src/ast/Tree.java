package src.ast;

import src.lexer.Token;

public abstract class Tree
{
    private Token token;

    public Tree(Token token)
    {
        this.token = token;
    }
    
    public Token getToken()
    {
        return token;
    }

    public void setToken(Token token)
    {
        this.token = token;
    }

    public abstract <R> R accept(Visitor<R> v);
}
