package knight.compiler.ast.types;

import knight.compiler.lexer.Token;
import knight.compiler.ast.AST;

public abstract class ASTType extends AST
{
	public ASTType(Token token)
	{
		super(token);
	}
}