package knight.compiler.codegen.intrinsics;

import java.util.*;

public class IntrinsicRegistry
{
	private static final Map<String, IntrinsicHandler> INTRINSICS = new HashMap<>();

	static {
		INTRINSICS.put("Out", new IOIntrinsics.OutHandler());
		INTRINSICS.put("In", new IOIntrinsics.InHandler());
		INTRINSICS.put("File", new FileIntrinsics.FileHandler());
		INTRINSICS.put("System", new SystemIntrinsics.SystemHandler());
	}

	public static Optional<String> generateIntrinsic(String className)
	{
		IntrinsicHandler handler = INTRINSICS.get(className);
		return handler != null ? Optional.of(handler.generate()) : Optional.empty();
	}

	public static Set<String> getRequiredHeaders(String className)
	{
		return switch (className)
		{
			case "Out", "In" -> Set.of("iostream");
			case "File" -> Set.of("fstream", "filesystem");
			case "System" -> Set.of("cstdlib", "cstring");
			default -> Set.of();
		};
	}
}
