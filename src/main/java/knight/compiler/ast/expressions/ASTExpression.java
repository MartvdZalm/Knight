package knight.compiler.ast.expressions;

import knight.compiler.lexer.Token;
import knight.compiler.ast.AST;
import knight.compiler.ast.types.ASTType;

public abstract class ASTExpression extends AST
{
	private ASTType type;

	public ASTExpression(Token token)
	{
		super(token);
	}

	public ASTType getType()
	{
		return type;
	}

	public void setType(ASTType type)
	{
		this.type = type;
	}
};