package knight.compiler.codegen;

import knight.compiler.ast.types.ASTBooleanType;
import knight.compiler.ast.types.ASTFunctionType;
import knight.compiler.ast.types.ASTIdentifierType;
import knight.compiler.ast.types.ASTIntArrayType;
import knight.compiler.ast.types.ASTIntType;
import knight.compiler.ast.types.ASTStringArrayType;
import knight.compiler.ast.types.ASTStringType;
import knight.compiler.ast.types.ASTType;
import knight.compiler.ast.types.ASTVoidType;

public class TypeConverter
{
	private final HeaderManager headerManager;

	public TypeConverter(HeaderManager headerManager)
	{
		this.headerManager = headerManager;
	}

	public String convertType(ASTType type)
	{
		if (type instanceof ASTIntType)
			return "int";
		if (type instanceof ASTStringType) {
			headerManager.addRequiredHeader("string");
			return "std::string";
		}
		if (type instanceof ASTBooleanType)
			return "bool";
		if (type instanceof ASTVoidType)
			return "void";
		if (type instanceof ASTIntArrayType) {
			headerManager.addRequiredHeader("vector");
			return "std::vector<int>";
		}
		if (type instanceof ASTStringArrayType) {
			headerManager.addRequiredHeader("vector");
			headerManager.addRequiredHeader("string");
			return "std::vector<std::string>";
		}
		if (type instanceof ASTIdentifierType) {
			return ((ASTIdentifierType) type).getName();
		}
		if (type instanceof ASTFunctionType) {
			headerManager.addRequiredHeader("functional");
			return "std::function<void()>";
		}
		return "auto";
	}
}
