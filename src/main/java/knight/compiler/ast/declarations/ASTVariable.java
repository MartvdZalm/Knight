package knight.compiler.ast.declarations;

import knight.compiler.lexer.Token;
import knight.compiler.ast.AST;
import knight.compiler.ast.ASTVisitor;
import knight.compiler.ast.types.ASTType;

public class ASTVariable extends AST
{
    private ASTType type;
	private ASTIdentifier id;

    public ASTVariable(Token token, ASTType type, ASTIdentifier id)
    {
        super(token);
        this.type = type;
		this.id = id;
    }

    public ASTType getType()
	{
		return type;
	}

    public void setType(ASTType type)
	{
		this.type = type;
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
