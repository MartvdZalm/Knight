package src.ast;

import src.lexer.Token;

public abstract class InheritanceDecl extends Tree
{
    private Identifier id;

    public InheritanceDecl(Token token, Identifier id)
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
}
