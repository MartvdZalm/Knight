package knight.lib;

import java.util.List;

public interface Library
{
	String getName();

	String getNamespace();

	List<FunctionSignature> getFunctions();

	String getSourceCode();
}
