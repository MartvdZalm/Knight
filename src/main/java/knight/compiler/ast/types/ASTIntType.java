package knight.compiler.ast.types;

import knight.compiler.ast.ASTVisitor;
import knight.compiler.lexer.Token;

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
		return "int";
	}
}
