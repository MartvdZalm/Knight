package knight.builder.code;

import com.github.javafaker.Faker;

public class CodeBuilderVariableInit extends CodeBuilderVariable
{
	private String expression;

	public CodeBuilderVariableInit()
	{
		super.initialize();
	}

	public CodeBuilderVariableInit(String identifier)
	{
		super(identifier);
	}

	public CodeBuilderVariableInit mock()
	{
		super.mock();

		if (super.empty(this.expression)) {
			this.expression = super.faker.lorem().word();
		}

		return this;
	}

	@Override
	public String toString()
	{
		return String.format("%s %s = %s;", super.type, super.identifier, this.expression);
	}
}