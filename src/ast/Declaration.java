package src.ast;

import src.lexer.Token;

public abstract class Declaration extends Tree
{
    public Declaration (Token token)
    {
        super(token);
    }
}
