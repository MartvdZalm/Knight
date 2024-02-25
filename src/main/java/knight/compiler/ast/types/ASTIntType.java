package knight.compiler.ast.types;

import knight.compiler.lexer.Token;
import knight.compiler.ast.ASTVisitor;

public class ASTIntType extends ASTType
{
	public ASTIntType(Token token)
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
		return ".int";
	}
}