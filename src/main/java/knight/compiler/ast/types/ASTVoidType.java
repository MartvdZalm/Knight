package knight.compiler.ast.types;

import knight.compiler.lexer.Token;
import knight.compiler.ast.ASTVisitor;

public class ASTVoidType extends ASTType
{
    public ASTVoidType(Token token)
	{
		super(token);
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}

	@Override
	public String toString()
	{
		return "void";
	}
}
