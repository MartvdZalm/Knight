package src.ast;

import java.util.List;

import src.lexer.Token;

public class InterDecl extends Tree
{
    private Identifier id;

    public InterDecl(Token token, Identifier id)
    {
        super(token);
        this.id = id;
    }

    public Identifier getId()
    {
        return id;
    }

    public void setId(Identifier id)
    {
        this.id = id;
    }

    @Override
    public <R> R accept(Visitor<R> v)
    {
        return v.visit(this);
    }
}
