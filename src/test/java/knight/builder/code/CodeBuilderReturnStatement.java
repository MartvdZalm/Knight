package knight.builder.code;

import com.github.javafaker.Faker;

public class CodeBuilderStatement extends CodeBuilder
{
	private Faker faker;
	private String expression;

	public CodeBuilderReturnStatement()
	{
		this.faker = new Faker();
	}

	public CodeBuilderReturnStatement mock()
	{
		this.expression = this.faker.name().firstName();
	}

	@Override
	public String toString()
	{
		return String.format("ret %s;", this.expression);
	}
}