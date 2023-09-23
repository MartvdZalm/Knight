package src.ast;

import src.lexer.Token;

public class Implements extends Inheritance
{
    public Implements(Token token, Identifier id)
    {
        super(token, id);
    }

    @Override
    public <R> R accept(Visitor<R> v)
    {
        return v.visit(this);
    }
}
