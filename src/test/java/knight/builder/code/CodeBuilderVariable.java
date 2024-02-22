package knight.builder.code;

import com.github.javafaker.Faker;

public class CodeBuilderVariable extends CodeBuilder
{
	protected CodeBuilderType type;
	protected String identifier;

	public CodeBuilderVariable()
	{
		super.initialize();
	}

	public CodeBuilderVariable(String identifier)
	{
		this.identifier = identifier;
		super.initialize();
	}

	public void setType(CodeBuilderType type)
	{
		this.type = type;
	}

	public CodeBuilderVariable mock()
	{
		if (this.type == null) {
			this.type = CodeBuilderType.random();
		}

		if (super.empty(this.identifier)) {
			this.identifier = super.faker.lorem().word();
		}

		return this;
	}

	@Override
	public String toString()
	{
		return String.format("%s %s;", this.type, this.identifier);
	}
}