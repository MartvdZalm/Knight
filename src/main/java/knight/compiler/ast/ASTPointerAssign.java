package knight.compiler.ast;

import knight.compiler.lexer.Token;

/*
 * File: ASTPointerAssign.java
 * @author: Mart van der Zalm
 * Date: 2025-04-10
 */
public class ASTPointerAssign extends ASTStatement
{
	private ASTPointer pointer;
	private ASTIdentifier variable;
	private ASTExpression expression;

	public ASTPointerAssign(Token token, ASTPointer pointer, ASTIdentifier variable, ASTExpression expression)
	{
		super(token);
		this.pointer = pointer;
		this.variable = variable;
		this.expression = expression;
	}

	public ASTPointer getPointer()
	{
		return pointer;
	}

	public void setPointer(ASTPointer pointer)
	{
		this.pointer = pointer;
	}

	public ASTIdentifier getVariable()
	{
		return variable;
	}

	public void setVariable(ASTIdentifier variable)
	{
		this.variable = variable;
	}

	public ASTExpression getExpression()
	{
		return expression;
	}

	public void setExpression(ASTExpression expression)
	{
		this.expression = expression;
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}
}