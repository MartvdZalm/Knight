package knight.compiler.ast.types;

import knight.compiler.ast.AST;
import knight.compiler.lexer.Token;

public abstract class ASTType extends AST
{
	public ASTType(Token token)
	{
		super(token);
	}
}
