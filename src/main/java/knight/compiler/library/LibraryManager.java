package knight.compiler.library;

import knight.compiler.ast.program.ASTProgram;
import knight.compiler.ast.AST;
import knight.compiler.ast.ASTSourceFileSetter;
import knight.compiler.parser.Parser;
import knight.compiler.lexer.Lexer;
import knight.compiler.semantics.BuildSymbolTree;
import knight.compiler.semantics.model.SymbolProgram;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

public class LibraryManager
{
	private static final Map<String, Library> libraries = new HashMap<>();
	private static final String LIBRARY_PATH = "share/";
	private static boolean librariesLoaded = false;

	static {
		// Libraries will be loaded when needed
	}

	public static void loadAllLibraries(SymbolProgram symbolProgram)
	{
		if (librariesLoaded) {
			return;
		}

		loadStandardLibrary("std", symbolProgram);

		librariesLoaded = true;
	}

	public static void loadStandardLibrary(String name, SymbolProgram symbolProgram)
	{
		try {
			String libraryPath = LIBRARY_PATH + name + ".knight";
			File libraryFile = new File(libraryPath);

			if (libraryFile.exists()) {
				BufferedReader reader = new BufferedReader(new FileReader(libraryFile));
				Lexer lexer = new Lexer(reader);
				Parser parser = new Parser(lexer);
				ASTProgram ast = (ASTProgram) parser.parse();

				setSourceFileRecursively(ast, libraryFile.getCanonicalPath());

				Library library = new Library(name, ast);
				libraries.put(name, library);

				BuildSymbolTree buildSymbolTree = new BuildSymbolTree(symbolProgram);
				buildSymbolTree.visit(ast);

				reader.close();
			}
		} catch (Exception e) {
			System.err.println("Failed to load library: " + name + " - " + e.getMessage());
		}
	}

	private static void setSourceFileRecursively(AST ast, String sourceFile)
	{
		if (ast == null) {
			return;
		}

		ASTSourceFileSetter setter = new ASTSourceFileSetter(sourceFile);
		setter.setSourceFileRecursively(ast);
	}

	public static Optional<Library> getLibrary(String name)
	{
		return Optional.ofNullable(libraries.get(name));
	}

	public static boolean hasLibrary(String name)
	{
		return libraries.containsKey(name);
	}

	public static void registerLibrary(String name, Library library)
	{
		libraries.put(name, library);
	}
}
