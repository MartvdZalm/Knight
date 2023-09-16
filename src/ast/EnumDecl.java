package src.ast;

import java.util.List;

import src.lexer.Token;

public class EnumDecl extends Tree
{
    private IdentifierExpr enumName;
    private List<Declaration> declList;

    public EnumDecl(Token token, IdentifierExpr enumName, List<Declaration> declList)
    {
        super(token);
        this.enumName = enumName;
        this.declList = declList;
    }

    public IdentifierExpr getId()
    {
        return enumName;
    }

    public void setId(IdentifierExpr enumName)
    {
        this.enumName = enumName;
    }

    public int getDeclListSize()
    {
        return declList.size();
    }

    public Declaration getDeclAt(int index)
    {
        if (index < declList.size()) {
            return declList.get(index);
        }
        return null;
    }

    @Override
    public <R> R accept(Visitor<R> v)
    {
        return v.visit(this);
    }
}
