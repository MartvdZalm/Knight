package knight.compiler.ast.declarations;

import java.util.List;

import knight.compiler.lexer.Token;
import knight.compiler.ast.AST;
import knight.compiler.ast.ASTVisitor;

public class ASTEnumeration extends AST
{
    private ASTIdentifier id;

    public ASTEnumeration(Token token, ASTIdentifier id)
    {
        super(token);
        this.id = id;
    }

    public ASTIdentifier getId()
    {
        return id;
    }

    public void setId(ASTIdentifier id)
    {
        this.id = id;
    }

    @Override
    public <R> R accept(ASTVisitor<R> v)
    {
        return v.visit(this);
    }
}
