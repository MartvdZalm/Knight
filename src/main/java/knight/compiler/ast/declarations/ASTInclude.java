package knight.compiler.ast.declarations;

import knight.compiler.lexer.Token;
import knight.compiler.ast.AST;
import knight.compiler.ast.ASTVisitor;

public class ASTInclude extends AST
{
    private ASTIdentifier id;

    public ASTInclude(Token token, ASTIdentifier id)
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
