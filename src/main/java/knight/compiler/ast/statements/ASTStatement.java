package knight.compiler.ast.statements;

import knight.compiler.lexer.Token;
import knight.compiler.ast.AST;

public abstract class ASTStatement extends AST
{
    public ASTStatement(Token token)
	{
		super(token);
	}
}
