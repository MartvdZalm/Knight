package knight.compiler.semantics.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import knight.compiler.ast.ASTIntType;
import knight.compiler.ast.ASTStringType;
import knight.compiler.ast.ASTType;
import knight.compiler.ast.ASTVoidType;
import knight.compiler.lexer.Symbol;
import knight.compiler.lexer.Token;

public class BuiltInFunctions
{
	private static final Map<String, FunctionSignature> STANDARD_LIB = new HashMap<>();

	static {
		STANDARD_LIB.put("print", new FunctionSignature(new ASTVoidType(generateToken("void")),
				Arrays.asList(new ASTStringType(generateToken("string")))));
		STANDARD_LIB.put("read_line",
				new FunctionSignature(new ASTStringType(generateToken("string")), Arrays.asList()));
		STANDARD_LIB.put("random", new FunctionSignature(new ASTIntType(generateToken("int")),
				Arrays.asList(new ASTIntType(generateToken("int")), new ASTIntType(generateToken("int")))));
		STANDARD_LIB.put("to_int", new FunctionSignature(new ASTIntType(generateToken("int")),
				Arrays.asList(new ASTStringType(generateToken("string")))));
		STANDARD_LIB.put("to_string", new FunctionSignature(new ASTStringType(generateToken("string")),
				Arrays.asList(new ASTIntType(generateToken("int")))));
	}

	public static Token generateToken(String token)
	{
		return new Token(Symbol.symbol(token, null), 0, 0);
	}

	public static boolean isBuiltIn(String name)
	{
		return STANDARD_LIB.containsKey(name);
	}

	public static FunctionSignature getSignature(String name)
	{
		return STANDARD_LIB.get(name);
	}

	public static class FunctionSignature
	{
		public final ASTType returnType;
		public final List<ASTType> parameterTypes;

		public FunctionSignature(ASTType returnType, List<ASTType> parameterTypes)
		{
			this.returnType = returnType;
			this.parameterTypes = parameterTypes;
		}
	}
}
