package knight.builder.code;

import com.github.javafaker.Faker;

public class CodeBuilderReturnStatement extends CodeBuilder
{
	private String expression;

	public CodeBuilderReturnStatement()
	{
		super.initialize();
	}

	public void setExpr(String expression)
	{
		this.expression = expression;
	}

	public CodeBuilderReturnStatement mock()
	{
		if (super.empty(this.expression)) {
			this.expression = super.faker.name().firstName();
		}

		return this;
	}

	@Override
	public String toString()
	{
		return String.format("ret %s;", this.expression);
	}
}