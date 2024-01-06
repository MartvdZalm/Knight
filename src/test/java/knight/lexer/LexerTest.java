package knight.lexer;

import java.io.*;
import static org.junit.Assert.*;
import org.junit.Test;

import knight.lexer.Lexer;
import knight.lexer.Token;
import knight.lexer.Tokens;

public class LexerTest
{
	Lexer lexer;

	public LexerTest()
	{   
		InputStream ioStream = this.getClass().getClassLoader().getResourceAsStream("LexerTest.txt");
		Reader reader = new InputStreamReader(ioStream);
		BufferedReader br = new BufferedReader(reader);

		this.lexer = new Lexer(br);
	}

    @Test
    public void testTokenization()
    {
        assertToken(Tokens.INCLUDE, "include");
        assertToken(Tokens.STRING, "print");
        assertToken(Tokens.SEMICOLON, ";");



        assertToken(Tokens.FUNCTION, "fn");
        assertToken(Tokens.IDENTIFIER, "main");
        assertToken(Tokens.LEFTPAREN, "(");
        assertToken(Tokens.RIGHTPAREN, ")");
        assertToken(Tokens.COLON, ":");
        assertToken(Tokens.INTEGER, "int");
        assertToken(Tokens.LEFTBRACE, "{");
        assertToken(Tokens.RETURN, "ret");
        assertToken(Tokens.INTEGER, "0");
        assertToken(Tokens.SEMICOLON, ";");
        assertToken(Tokens.RIGHTBRACE, "}");
    }

    private void assertToken(Tokens expectedToken, String input)
    {
        Token token = lexer.nextToken();

        assertNotNull(token);
        assertEquals(expectedToken, token.getToken());
        assertEquals(input, token.getSymbol());
    }
}