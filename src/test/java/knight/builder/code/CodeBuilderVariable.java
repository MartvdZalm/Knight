package knight.builder.code;

public class CodeBuilderVariable extends CodeBuilder
{
	protected CodeBuilderType type;
	protected String id;

	public CodeBuilderVariable()
	{
		this.mock();
	}

	public CodeBuilderVariable setId(String id)
	{
		this.id = id;

		return this;
	}

	public CodeBuilderVariable setType(CodeBuilderType type)
	{
		this.type = type;

		return this;
	}

	protected CodeBuilderVariable mock()
	{
		this.type = CodeBuilderType.random();
		this.id = super.random.identifier();

		return this;
	}

	public String toString()
	{		
		return String.format("%s %s;", this.type, this.id);
	}
}