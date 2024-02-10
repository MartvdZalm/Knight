package knight.builder.code;

public enum CodeBuilderTypes
{
	INT("int"),
	STRING("string"),
	BOOLEAN("boolean");

	private final String type;
    
    private CodeBuilderTypes(String type)
    {
        this.type = type;
    }
    
    public String getType()
    {
        return type;
    }
}
