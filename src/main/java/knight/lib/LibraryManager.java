package knight.lib;

import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import knight.lib.std.StdLib;

public class LibraryManager
{
	private static final Map<String, Library> libraries = new HashMap<>();

	static {
		registerLibrary(new StdLib());
	}

	public static void registerLibrary(Library library)
	{
		libraries.put(library.getName(), library);
	}

	public static Optional<FunctionSignature> findFunction(String name)
	{
		return libraries.values().stream().flatMap(lib -> lib.getFunctions().stream())
				.filter(f -> f.getName().equals(name)).findFirst();
	}

	public static Optional<Library> findLibrary(String name)
	{
		return libraries.values().stream().filter(lib -> lib.getName().equals(name)).findFirst();
	}
}
