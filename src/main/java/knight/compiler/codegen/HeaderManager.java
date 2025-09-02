package knight.compiler.codegen;

import java.util.HashSet;
import java.util.Set;

public class HeaderManager
{
	private final Set<String> requiredHeaders = new HashSet<>();

	public void addRequiredHeader(String header)
	{
		requiredHeaders.add(header);
	}

	public String generateHeaderIncludes()
	{
		StringBuilder includes = new StringBuilder();
		for (String header : requiredHeaders) {
			includes.append("#include <").append(header).append(">\n");
		}
		return includes.toString();
	}
}
