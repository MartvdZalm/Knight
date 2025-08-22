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

	@Test
	public void peekNext_should_not_consume_characters()
	{
		BufferedReader bufferedReader = new BufferedReader(new StringReader("123 identifier hello"));
		Lexer lexer = new Lexer(bufferedReader);

		Token firstToken = lexer.nextToken();
		assertEquals("123", firstToken.getSymbol());

		char peeked1 = lexer.peekNext();
		assertEquals('i', peeked1);

		Token actual1 = lexer.nextToken();
		assertEquals("identifier", actual1.getSymbol());

		char peeked2 = lexer.peekNext();
		assertEquals('h', peeked2);

		Token actual2 = lexer.nextToken();
		assertEquals("hello", actual2.getSymbol());
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

	@Test
	public void comments_should_not_interfere_with_operators()
	{
		String input = "/ + * /";
		BufferedReader bufferedReader = new BufferedReader(new StringReader(input));
		Lexer lexer = new Lexer(bufferedReader);

		Token first = lexer.nextToken();
		assertEquals("/", first.getSymbol());

		Token third = lexer.nextToken();
		assertEquals("+", third.getSymbol());

		Token second = lexer.nextToken();
		assertEquals("*", second.getSymbol());

		Token fourth = lexer.nextToken();
		assertEquals("/", fourth.getSymbol());
	}

	@Test
	public void mixed_comments_should_work_together()
	{
		String input = """
				123 // line comment
				/* block comment */ 456
				// another line comment
				/* another
				block comment */
				789
				""";
		BufferedReader bufferedReader = new BufferedReader(new StringReader(input));
		Lexer lexer = new Lexer(bufferedReader);

		Token first = lexer.nextToken();
		assertEquals("123", first.getSymbol());
		assertEquals(Tokens.INTEGER, first.getToken());

		Token second = lexer.nextToken();
		assertEquals("456", second.getSymbol());
		assertEquals(Tokens.INTEGER, second.getToken());

		Token third = lexer.nextToken();
		assertEquals("789", third.getSymbol());
		assertEquals(Tokens.INTEGER, third.getToken());
	}

	@Test
	public void comments_should_not_affect_strings()
	{
		String input = "\"string with // comment syntax inside\"";
		BufferedReader bufferedReader = new BufferedReader(new StringReader(input));
		Lexer lexer = new Lexer(bufferedReader);

		Token token = lexer.nextToken();
		assertEquals("\"string with // comment syntax inside\"", token.getSymbol());
		assertEquals(Tokens.STRING, token.getToken());
	}

	@Test
	public void peekToken_should_work_with_comments()
	{
		String input = """
				123 /* comment */ 456
				""";
		BufferedReader bufferedReader = new BufferedReader(new StringReader(input));
		Lexer lexer = new Lexer(bufferedReader);

		Token first = lexer.nextToken();
		assertEquals("123", first.getSymbol());

		Token peeked = lexer.peekToken();
		assertEquals("456", peeked.getSymbol());

		Token actual = lexer.nextToken();
		assertEquals("456", actual.getSymbol());
	}

	@Test
	public void empty_line_comment_should_work()
	{
		String input = """
				123 //
				456
				""";
		BufferedReader bufferedReader = new BufferedReader(new StringReader(input));
		Lexer lexer = new Lexer(bufferedReader);

		Token first = lexer.nextToken();
		assertEquals("123", first.getSymbol());

		Token second = lexer.nextToken();
		assertEquals("456", second.getSymbol());
	}

	@Test
	public void empty_block_comment_should_work()
	{
		String input = "123 /**/ 456";
		BufferedReader bufferedReader = new BufferedReader(new StringReader(input));
		Lexer lexer = new Lexer(bufferedReader);

		Token first = lexer.nextToken();
		assertEquals("123", first.getSymbol());

		Token second = lexer.nextToken();
		assertEquals("456", second.getSymbol());
	}

	@Test
	public void unterminated_block_comment_should_throw()
	{
		String input = "123 /* unterminated...";
		BufferedReader bufferedReader = new BufferedReader(new StringReader(input));
		Lexer lexer = new Lexer(bufferedReader);
		lexer.nextToken();
		assertThrows(LexerException.class, lexer::nextToken);
	}

	@Test
	public void unterminated_string_should_throw()
	{
		String input = "\"unterminated string...";
		BufferedReader bufferedReader = new BufferedReader(new StringReader(input));
		Lexer lexer = new Lexer(bufferedReader);
		assertThrows(LexerException.class, lexer::nextToken);
	}

	@Test
	public void empty_input_should_return_EOF()
	{
		BufferedReader bufferedReader = new BufferedReader(new StringReader(""));
		Lexer lexer = new Lexer(bufferedReader);
		Token token = lexer.nextToken();
		assertEquals(Tokens.EOF, token.getToken());
	}

	@Test
	public void multiple_whitespace_should_be_ignored()
	{
		String input = "123     456";
		BufferedReader bufferedReader = new BufferedReader(new StringReader(input));
		Lexer lexer = new Lexer(bufferedReader);

		assertEquals("123", lexer.nextToken().getSymbol());
		assertEquals("456", lexer.nextToken().getSymbol());
	}

	@Test
	public void string_with_escape_sequences_should_work()
	{
		String input = "\"line1\\nline2\"";
		BufferedReader bufferedReader = new BufferedReader(new StringReader(input));
		Lexer lexer = new Lexer(bufferedReader);
		Token token = lexer.nextToken();

		assertEquals("\"line1\\nline2\"", token.getSymbol());
		assertEquals(Tokens.STRING, token.getToken());
	}

	@Test
	public void identifier_with_digits_should_work()
	{
		String input = "var123";
		BufferedReader bufferedReader = new BufferedReader(new StringReader(input));
		Lexer lexer = new Lexer(bufferedReader);
		Token token = lexer.nextToken();

		assertEquals("var123", token.getSymbol());
		assertEquals(Tokens.IDENTIFIER, token.getToken());
	}
}
