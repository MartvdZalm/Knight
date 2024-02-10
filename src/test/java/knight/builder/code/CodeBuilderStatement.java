package knight.builder.code;

import com.github.javafaker.Faker;

public class CodeBuilderStatement extends CodeBuilder
{
	private Faker faker;
	private String identifier;
	private String expression;

	public CodeBuilderStatement()
	{
		this.faker = new Faker();
	}

	public CodeBuilderStatement mock()
	{
		this.identifier = this.faker.lorem().word();
		this.expression = this.faker.name().firstName();

		return this;
	}

	@Override
	public String toString()
	{
		return String.format("%s = %s;", this.identifier, this.expression);
	}
}