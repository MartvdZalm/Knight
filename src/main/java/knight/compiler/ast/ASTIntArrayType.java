package knight.compiler.ast;

import knight.compiler.lexer.Token;

public class ASTIntArrayType extends ASTType
{
	public ASTIntArrayType(Token token)
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
		return "int[]";
	}
}