package knight.builder.code;

import com.github.javafaker.Faker;

public class CodeBuilderStatement extends CodeBuilder
{
	private String identifier;
	private String expression;

	public CodeBuilderStatement()
	{
		super.initialize();
	}

	public CodeBuilderStatement mock()
	{
		this.identifier = super.faker.lorem().word();
		this.expression = super.faker.name().firstName();

		return this;
	}

	@Override
	public String toString()
	{
		return String.format("%s = %s;", this.identifier, this.expression);
	}
}