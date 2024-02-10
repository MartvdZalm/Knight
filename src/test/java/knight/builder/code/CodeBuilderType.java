package knight.builder.code;

import java.util.Random;

public enum CodeBuilderType
{
    INT("int"),
    STRING("string"),
    BOOLEAN("bool");

    private final String type;
    private static final Random random = new Random();

    private CodeBuilderType(String type)
    {
        this.type = type;
    }

    public String getType()
    {
        return type;
    }

    @Override
    public String toString()
    {
        return this.getType();
    }

    public static CodeBuilderType random()
    {
        return values()[random.nextInt(values().length)];
    }
}