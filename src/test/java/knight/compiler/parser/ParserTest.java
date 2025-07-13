package knight.compiler.parser;

import knight.compiler.lexer.Lexer;
import knight.compiler.lexer.Symbol;
import knight.compiler.lexer.Token;
import knight.compiler.lexer.Tokens;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ParserTest
{
	protected Parser parser;
	protected Lexer mockLexer;

	@BeforeEach
	public void setUp()
	{
		mockLexer = mock(Lexer.class);
		parser = new Parser(mockLexer);
	}

	protected Token createToken(String text, Tokens type)
	{
		return new Token(Symbol.symbol(text, type), 1, 1);
	}

	protected void mockTokens(Token... tokens)
	{
		when(mockLexer.nextToken()).thenReturn(tokens[0], Stream.of(tokens).skip(1).toArray(Token[]::new))
				.thenReturn(null);
	}

	protected void mockPeekToken(Token token)
	{
		when(mockLexer.peekToken()).thenReturn(token);
	}

	protected static Stream<Arguments> arithmeticOperatorProvider()
	{
		return Stream.of(Arguments.of(Tokens.PLUS, "+"), Arguments.of(Tokens.MINUS, "-"),
				Arguments.of(Tokens.TIMES, "*"), Arguments.of(Tokens.DIV, "/"), Arguments.of(Tokens.MODULUS, "%"));
	}
}
