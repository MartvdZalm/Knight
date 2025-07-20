package knight.compiler.codegen.intrinsics;

import java.util.List;

public class IOIntrinsics
{
	public static class OutHandler implements IntrinsicHandler
	{
		@Override
		public String generate()
		{

			StringBuilder cpp = new StringBuilder();

			cpp.append("class Out {\n" + "public:\n" + "    static void write(const std::string& input) {\n"
					+ "        std::cout << input;\n" + "    }\n" + "    \n"
					+ "    static void writeln(const std::string& input) {\n"
					+ "        std::cout << input << std::endl;\n" + "    }\n" + "};");

			// switch (methodName)
			// {
			// case "write":
			// cpp.append("std::cout << ").append(args.get(0));
			// break;
			// case "writeLine":
			// cpp.append("std::cout << ").append(args.get(0)).append(" << std::endl");
			// break;
			// case "writeError":
			// cpp.append("std::cerr << ").append(args.get(0));
			// break;
			// }
			return cpp.toString();
		}
	}

	public static class InHandler implements IntrinsicHandler
	{
		@Override
		public String generate()
		{
			StringBuilder cpp = new StringBuilder();
			// switch (methodName)
			// {
			// case "read":
			// cpp.append("([&]() { std::string temp; std::getline(std::cin, temp); return
			// temp; })()");
			// break;
			// case "readChar":
			// cpp.append("std::cin.get()");
			// break;
			// case "readInt":
			// cpp.append("([&]() { int temp; std::cin >> temp; return temp; })()");
			// break;
			// case "readDouble":
			// cpp.append("([&]() { double temp; std::cin >> temp; return temp; })()");
			// break;
			// }

			return cpp.toString();
		}
	}
}
