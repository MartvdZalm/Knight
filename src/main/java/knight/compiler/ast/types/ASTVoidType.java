package knight.compiler.ast.types;

import knight.compiler.ast.ASTVisitor;
import knight.compiler.lexer.Token;

public class ASTVoidType extends ASTType
{
	public ASTVoidType(Token token)
	{
		super(token);
	}

	@Override
	public <R> R accept(ASTVisitor<R> visitor)
	{
		return visitor.visit(this);
	}

	@Override
	public String toString()
	{
		return "void";
	}
}
