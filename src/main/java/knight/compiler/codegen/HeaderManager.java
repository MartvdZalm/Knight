package knight.compiler.codegen;

import java.util.Set;
import java.util.HashSet;

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
