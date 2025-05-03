package knight.lib.std;

import java.util.ArrayList;
import java.util.List;
import knight.lib.FunctionSignature;
import knight.lib.Library;
import knight.compiler.lexer.Tokens;
import java.util.Arrays;
import knight.utils.FileContentLoader;

public class StdLib implements Library
{
	private final List<FunctionSignature> functions = new ArrayList<>();

	public StdLib()
	{
		this.functions.add(new FunctionSignature("print", Tokens.VOID, Arrays.asList(Tokens.STRING)));
	}

	@Override
	public String getName()
	{
		return "std";
	}

	@Override
	public String getNamespace()
	{
		return "knight::std::";
	}

	@Override
	public List<FunctionSignature> getFunctions()
	{
		return functions;
	}
}
