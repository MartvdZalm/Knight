package src.symbol;

import src.ast.Type;
import src.semantics.Binding;

public class Decl extends Binding
{
    private String id;

    public Decl(String id, Type type)
    {
        super(type);
        this.id = id;
    }

    public String getId()
	{
		return id;
	}

    @Override
	public Type getType()
	{
		return type;
	}

}
