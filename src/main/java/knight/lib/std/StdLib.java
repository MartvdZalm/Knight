package knight.lib.std;

import knight.compiler.lexer.Tokens;
import knight.lib.Library;
import knight.lib.signatures.FunctionSignature;

import java.util.ArrayList;
import java.util.List;

public class StdLib extends Library
{
	private final List<FunctionSignature> functions = new ArrayList<>();

	public StdLib()
	{
		this.functions.add(new FunctionSignature("print", this.getType(Tokens.STRING),
				List.of(this.getType(Tokens.STRING), this.getType(Tokens.INTEGER))));
	}

	@Override
	public String getName()
	{
		return "std";
	}

	@Override
	public List<FunctionSignature> getFunctions()
	{
		return functions;
	}
}
