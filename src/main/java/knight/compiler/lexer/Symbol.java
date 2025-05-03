package knight.compiler.lexer;

import java.util.HashMap;

public class Symbol
{
	private String symbol;
	private Tokens token;
	public static HashMap<String, Symbol> symbols = new HashMap<String, Symbol>();

	private Symbol(String symbol, Tokens token)
	{
		this.token = token;
		this.symbol = symbol;
	}

	public String getSymbol()
	{
		return symbol;
	}

	public Tokens getToken()
	{
		return token;
	}

	public static Symbol symbol(String newSymbol, Tokens token)
	{
		Symbol symbol = symbols.get(newSymbol);

		if (symbol == null) {
			if (token == Tokens.INVALID) {
				return null;
			}
			symbol = new Symbol(newSymbol, token);
			symbols.put(newSymbol, symbol);
		}
		return symbol;
	}
}