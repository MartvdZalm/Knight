package knight.compiler.library;

public class LibraryCodeGenerator
{

	public static String generateOutClass()
	{
		StringBuilder cpp = new StringBuilder();

		cpp.append("class Out {\n");
		cpp.append("public:\n");
		cpp.append("    static void write(const std::string& input) {\n");
		cpp.append("        std::cout << input;\n");
		cpp.append("    }\n");
		cpp.append("    \n");
		cpp.append("    static void writeln(const std::string& input) {\n");
		cpp.append("        std::cout << input << std::endl;\n");
		cpp.append("    }\n");
		cpp.append("};\n");

		return cpp.toString();
	}

	public static String generateInClass()
	{
		StringBuilder cpp = new StringBuilder();

		cpp.append("class In {\n");
		cpp.append("public:\n");
		cpp.append("    static std::string read() {\n");
		cpp.append("        std::string temp;\n");
		cpp.append("        std::getline(std::cin, temp);\n");
		cpp.append("        return temp;\n");
		cpp.append("    }\n");
		cpp.append("    \n");
		cpp.append("    static int readInt() {\n");
		cpp.append("        int temp;\n");
		cpp.append("        std::cin >> temp;\n");
		cpp.append("        std::cin.ignore(); // Clear newline\n");
		cpp.append("        return temp;\n");
		cpp.append("    }\n");
		cpp.append("};\n");

		return cpp.toString();
	}

	public static String generateGlobalFunctions()
	{
		StringBuilder cpp = new StringBuilder();

		cpp.append("void print(const std::string& input) {\n");
		cpp.append("    Out::write(input);\n");
		cpp.append("}\n");
		cpp.append("\n");
		cpp.append("void println(const std::string& input) {\n");
		cpp.append("    Out::writeln(input);\n");
		cpp.append("}\n");
		cpp.append("\n");
		cpp.append("std::string read() {\n");
		cpp.append("    return In::read();\n");
		cpp.append("}\n");
		cpp.append("\n");
		cpp.append("int readInt() {\n");
		cpp.append("    return In::readInt();\n");
		cpp.append("}\n");

		return cpp.toString();
	}

	public static String generateStandardLibrary()
	{
		StringBuilder cpp = new StringBuilder();

		cpp.append("// Standard Library Implementation\n");
		cpp.append(generateOutClass());
		cpp.append("\n");
		cpp.append(generateInClass());
		cpp.append("\n");
		cpp.append(generateGlobalFunctions());

		return cpp.toString();
	}
}
