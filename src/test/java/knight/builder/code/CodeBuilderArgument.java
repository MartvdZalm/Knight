package knight.builder.code;

import com.github.javafaker.Faker;

public class CodeBuilderArgument extends CodeBuilder
{
	private Faker faker;
	private CodeBuilderType type;
	private String identifier;

	public CodeBuilderArgument()
	{
		this.faker = new Faker();
	}

	public CodeBuilderArgument mock()
	{
		this.type = CodeBuilderType.random();
		this.identifier = this.faker.lorem().word();

		return this;
	}

	@Override
	public String toString()
	{
		return String.format("%s %s", this.type, this.identifier);
	}
}