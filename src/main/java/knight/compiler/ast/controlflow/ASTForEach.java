package knight.compiler.ast.controlflow;

import knight.compiler.ast.program.ASTVariable;
import knight.compiler.ast.ASTVisitor;
import knight.compiler.ast.expressions.ASTExpression;
import knight.compiler.ast.statements.ASTBody;
import knight.compiler.ast.statements.ASTStatement;
import knight.compiler.lexer.Token;

public class ASTForEach extends ASTStatement
{
	private ASTVariable variable;
	private ASTExpression iterable;
	private ASTBody body;

	public ASTForEach(Token token, ASTVariable variable, ASTExpression iterable, ASTBody body)
	{
		super(token);
		this.variable = variable;
		this.iterable = iterable;
		this.body = body;
	}

	public ASTVariable getVariable()
	{
		return variable;
	}

	public void setVariable(ASTVariable variable)
	{
		this.variable = variable;
	}

	public ASTExpression getIterable()
	{
		return iterable;
	}

	public void setIterable(ASTExpression iterable)
	{
		this.iterable = iterable;
	}

	public ASTBody getBody()
	{
		return body;
	}

	public void setBody(ASTBody body)
	{
		this.body = body;
	}

	@Override
	public <R> R accept(ASTVisitor<R> visitor)
	{
		return visitor.visit(this);
	}
}
