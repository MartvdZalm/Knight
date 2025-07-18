package knight.compiler.lexer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LexerTest
{
	@Test
	public void peekToken_should_not_consume_token()
	{
		BufferedReader bufferedReader = new BufferedReader(new StringReader("123 identifier"));
		Lexer lexer = new Lexer(bufferedReader);

		Token firstToken = lexer.nextToken();
		assertEquals("123", firstToken.getSymbol());

		Token peeked = lexer.peekToken();
		assertEquals("identifier", peeked.getSymbol());

		Token actual = lexer.nextToken();
		assertEquals("identifier", actual.getSymbol());
	}

	@ParameterizedTest
	@CsvSource({ "123, INTEGER", "identifier, IDENTIFIER", "\"string\", STRING", "+, PLUS", "==, EQUALS", "||, OR" })
	public void nextToken_should_return_correct_token_type(String input, Tokens expectedType)
	{
		BufferedReader bufferedReader = new BufferedReader(new StringReader(input));
		Lexer lexer = new Lexer(bufferedReader);
		Token token = lexer.nextToken();
		assertEquals(expectedType, token.getToken());
	}

	@Test
	public void nextToken_should_track_position_correctly() throws IOException
	{
		BufferedReader bufferedReader = new BufferedReader(new StringReader("123\nabc"));
		Lexer lexer = new Lexer(bufferedReader);

		Token first = lexer.nextToken();
		assertEquals(0, first.getRow());
		assertEquals(3, first.getCol());

		Token second = lexer.nextToken();
		assertEquals(1, second.getRow());
		assertEquals(3, second.getCol());
	}

	@Test
	public void nextToken_should_throw_on_invalid_operator()
	{
		BufferedReader bufferedReader = new BufferedReader(new StringReader("@"));
		Lexer lexer = new Lexer(bufferedReader);
		assertThrows(LexerException.class, lexer::nextToken);
	}

	@Test
	public void processString_should_handle_escaped_quotes()
	{
		BufferedReader bufferedReader = new BufferedReader(new StringReader("\"quote\\\"inside\""));
		Lexer lexer = new Lexer(bufferedReader);
		Token token = lexer.nextToken();

		assertEquals("\"quote\\\"inside\"", token.getSymbol());
		assertEquals(Tokens.STRING, token.getToken());
	}

	@Test
	public void processNumber_should_handle_large_numbers()
	{
		BufferedReader bufferedReader = new BufferedReader(new StringReader("1234567890"));
		Lexer lexer = new Lexer(bufferedReader);
		Token token = lexer.nextToken();

		assertEquals("1234567890", token.getSymbol());
		assertEquals(Tokens.INTEGER, token.getToken());
	}

	@Test
	public void processIdentifier_should_handle_unicode()
	{
		String input = "変数";
		BufferedReader bufferedReader = new BufferedReader(new StringReader("変数"));
		Lexer lexer = new Lexer(bufferedReader);
		Token token = lexer.nextToken();

		assertEquals("変数", token.getSymbol());
		assertEquals(Tokens.IDENTIFIER, token.getToken());
	}
}
