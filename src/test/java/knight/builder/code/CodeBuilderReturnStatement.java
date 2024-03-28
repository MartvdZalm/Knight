package knight.builder.code;

public class CodeBuilderReturnStatement extends CodeBuilder
{
	private String expression;

	public CodeBuilderReturnStatement()
	{
		this.mock();	
	}

	public void setExpr(String expression)
	{
		this.expression = expression;
	}

	public CodeBuilderReturnStatement mock()
	{
		this.expression = super.random.identifier();

		return this;
	}

	public String toString()
	{
		return String.format("ret %s;", this.expression);
	}
}