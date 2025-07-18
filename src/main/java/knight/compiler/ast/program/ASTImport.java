package knight.compiler.ast.program;

import knight.compiler.ast.AST;
import knight.compiler.ast.ASTVisitor;
import knight.compiler.lexer.Token;

public class ASTImport extends AST
{
	private ASTIdentifier library;

	public ASTImport(Token token, ASTIdentifier library)
	{
		super(token);
		this.library = library;
	}

	public ASTIdentifier getLibrary()
	{
		return library;
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}
}