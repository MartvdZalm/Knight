package knight.compiler.library;

import knight.compiler.ast.program.ASTProgram;
import knight.compiler.ast.AST;
import knight.compiler.ast.ASTSourceFileSetter;
import knight.compiler.parser.Parser;
import knight.compiler.lexer.Lexer;
import knight.compiler.semantics.BuildSymbolTree;
import knight.compiler.semantics.model.SymbolProgram;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;

public class LibraryManager
{
	private static final Map<String, LibraryFunction> builtinFunctions = new HashMap<>();

	static {
		registerBuiltIn("__builtin_print", "void", new String[] { "string" }, "std::cout << input << std::endl;");
		registerBuiltIn("__builtin_input", "string", new String[] {},
				"std::string input; std::getline(std::cin, input); return input;");
		registerBuiltIn("__builtin_to_string", "string", new String[] { "int" }, "return std::to_string(input);");
		registerBuiltIn("__builtin_to_int", "int", new String[] { "string" }, "return std::stoi(input);");
		registerBuiltIn("__builtin_length", "int", new String[] { "string" }, "return input.length();");
	}

	public static ASTProgram loadStandardLibrary()
	{
		ASTProgram astProgram = null;

		File file = new File("share/std.knight");
		try {
			if (file.exists()) {
				BufferedReader reader = new BufferedReader(new FileReader(file));
				Lexer lexer = new Lexer(reader);
				Parser parser = new Parser(lexer);
				AST ast = parser.parse();

				if (ast instanceof ASTProgram) {
					astProgram = (ASTProgram) ast;
				}
			}
		} catch (Exception e) {
			System.err.println("Error parsing file: " + file.getPath() + " - " + e.getMessage());
			e.printStackTrace();
		}

		return astProgram;
	}

	private static void registerBuiltIn(String name, String returnType, String[] paramTypes, String impl)
	{
		builtinFunctions.put(name, new LibraryFunction(name, returnType, paramTypes, impl));
	}

	public static LibraryFunction getBuiltIn(String name)
	{
		return builtinFunctions.get(name);
	}

	public static boolean isBuiltIn(String name)
	{
		return builtinFunctions.containsKey(name);
	}
}
