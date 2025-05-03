package knight.lib;

import java.util.List;

public interface Library
{
	public String getName();

	public String getNamespace();

	public List<FunctionSignature> getFunctions();
}
