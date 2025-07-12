package knight.compiler.parser;

import java.io.StringReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.provider.Arguments;
import knight.compiler.ast.*;
import knight.compiler.lexer.*;
import static org.mockito.Mockito.*;

public class ParserTest
{
	protected Parser parser;
	protected Lexer mockLexer;

	@BeforeEach
	public void setUp()
	{
		mockLexer = mock(Lexer.class);
		parser = new Parser(new BufferedReader(new StringReader(""))
		{
			{
				this.lexer = mockLexer;
			}
		});
	}

	protected void mockTokens(Token... tokens)
	{
		when(mockLexer.nextToken()).thenReturn(tokens[0], Stream.of(tokens).skip(1).toArray(Token[]::new))
				.thenReturn(null);
	}

	protected static Stream<Arguments> arithmeticOperatorProvider()
	{
		return Stream.of(Arguments.of(Tokens.PLUS, "+"), Arguments.of(Tokens.MINUS, "-"),
				Arguments.of(Tokens.TIMES, "*"), Arguments.of(Tokens.DIV, "/"), Arguments.of(Tokens.MODULUS, "%"));
	}

	protected static Stream<Arguments> comparisonOperatorProvider()
	{
		return Stream.of(Arguments.of(Tokens.EQUALS, "=="), Arguments.of(Tokens.NOTEQUALS, "!="),
				Arguments.of(Tokens.LESSTHAN, "<"), Arguments.of(Tokens.LESSTHANOREQUAL, "<="),
				Arguments.of(Tokens.GREATERTHAN, ">"), Arguments.of(Tokens.GREATERTHANOREQUAL, ">="));
	}
}
