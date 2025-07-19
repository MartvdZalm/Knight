package knight.compiler.codegen.lib;

import knight.compiler.ast.types.ASTIntType;
import knight.compiler.ast.types.ASTStringType;
import knight.compiler.ast.types.ASTType;
import knight.compiler.lexer.Symbol;
import knight.compiler.lexer.Token;
import knight.compiler.lexer.Tokens;
import knight.compiler.codegen.lib.signatures.FunctionSignature;

import java.util.List;

public abstract class Library
{
	public abstract String getName();

	public abstract List<FunctionSignature> getFunctions();

	public ASTType getType(Tokens tokens)
	{
		if (tokens == Tokens.INTEGER) {
			Token token = new Token(Symbol.symbol("", Tokens.INTEGER), 0, 0);
			return new ASTIntType(token);
		} else if (tokens == Tokens.STRING) {
			Token token = new Token(Symbol.symbol("", Tokens.STRING), 0, 0);
			return new ASTStringType(token);
		}

		return null;
	}
}
