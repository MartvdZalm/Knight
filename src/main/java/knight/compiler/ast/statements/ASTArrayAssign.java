package knight.compiler.ast.statements;

import knight.compiler.ast.expressions.ASTExpression;
import knight.compiler.ast.program.ASTIdentifier;
import knight.compiler.ast.ASTVisitor;
import knight.compiler.lexer.Token;

public class ASTArrayAssign extends ASTStatement
{
	private ASTIdentifier identifier;
	private ASTExpression array;
	private ASTExpression value;

	public ASTArrayAssign(Token token, ASTIdentifier identifier, ASTExpression array, ASTExpression value)
	{
		super(token);
		this.identifier = identifier;
		this.array = array;
		this.value = value;
	}

	public ASTIdentifier getIdentifier()
	{
		return identifier;
	}

	public void setIdentifier(ASTIdentifier identifier)
	{
		this.identifier = identifier;
	}

	public ASTExpression getArray()
	{
		return array;
	}

	public void setArray(ASTExpression array)
	{
		this.array = array;
	}

	public ASTExpression getValue()
	{
		return value;
	}

	public void setValue(ASTExpression value)
	{
		this.value = value;
	}

	@Override
	public <R> R accept(ASTVisitor<R> visitor)
	{
		return visitor.visit(this);
	}
}
