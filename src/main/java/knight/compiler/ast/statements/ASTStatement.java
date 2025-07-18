package knight.compiler.ast.statements;

import knight.compiler.ast.AST;
import knight.compiler.lexer.Token;

public abstract class ASTStatement extends AST
{
	public ASTStatement(Token token)
	{
		super(token);
	}
}
