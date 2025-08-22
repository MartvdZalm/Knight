package knight.compiler.ast.program;

import knight.compiler.ast.AST;
import knight.compiler.ast.ASTVisitor;
import knight.compiler.lexer.Token;

public class ASTImport extends AST
{
	private ASTIdentifier identifier;

	public ASTImport(Token token, ASTIdentifier identifier)
	{
		super(token);
		this.identifier = identifier;
	}

	public ASTIdentifier getIdentifier()
	{
		return identifier;
	}

	@Override
	public <R> R accept(ASTVisitor<R> visitor)
	{
		return visitor.visit(this);
	}
}
