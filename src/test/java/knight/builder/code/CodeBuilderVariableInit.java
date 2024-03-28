package knight.builder.code;

public class CodeBuilderVariableInit extends CodeBuilderVariable
{
	private CodeBuilderExpression expression;

	public CodeBuilderVariableInit()
	{
		this.mock();	
	}

	protected CodeBuilderVariableInit mock()
	{
		this.expression = super.random.expression();
		
		return this;
	}

	public String toString()
	{
		return String.format("%s %s = %s;", super.type, super.id, this.expression);
	}
}