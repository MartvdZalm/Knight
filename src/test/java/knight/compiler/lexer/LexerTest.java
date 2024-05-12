package knight.compiler.lexer;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import knight.compiler.lexer.Lexer;
import knight.compiler.lexer.Token;
import knight.compiler.lexer.Tokens;
import knight.compiler.lexer.SourceReader;

public class LexerTest
{
	@Test
	public void testPeekToken()
	{
		String input = "This is a string for testing my Lexer class!";
		BufferedReader bufferedReader = new BufferedReader(new StringReader(input));
		Lexer lexer = new Lexer(bufferedReader);
		Token token = lexer.peekToken();

		assertNotNull(token);
		assertEquals(Tokens.IDENTIFIER, token.getToken());
		assertEquals("This", token.getSymbol());
	}

	@Test
	public void testPeekTokenNextTokenNull()
	{
		BufferedReader bufferedReader = new BufferedReader(new StringReader(" "));
		Lexer lexer = new Lexer(bufferedReader);
		Token token = lexer.nextToken();

		assertNull(token);
		assertTrue(lexer.exception);
	}

    @Test
    public void testNextToken()
    {
    	assertToken(Tokens.LEFTPAREN, "(");
		assertToken(Tokens.RIGHTPAREN, ")");
		assertToken(Tokens.LEFTBRACE, "{");
		assertToken(Tokens.RIGHTBRACE, "}");
		assertToken(Tokens.LEFTBRACKET, "[");
		assertToken(Tokens.RIGHTBRACKET, "]");
		assertToken(Tokens.SEMICOLON, ";");
		assertToken(Tokens.COLON, ":");
		assertToken(Tokens.COMMA, ",");
		assertToken(Tokens.DOT, ".");
		assertToken(Tokens.IDENTIFIER, "name");
		assertToken(Tokens.INTEGER, "0");
		assertToken(Tokens.STRING, "string");
		assertToken(Tokens.BOOLEAN, "bool");
		assertToken(Tokens.TRUE, "true");
		assertToken(Tokens.FALSE, "false");
		assertToken(Tokens.PUBLIC, "public");
		assertToken(Tokens.PROTECTED, "protected");
		assertToken(Tokens.PRIVATE, "private");
		assertToken(Tokens.CLASS, "class");
		assertToken(Tokens.NEW, "new");
		assertToken(Tokens.INCLUDE, "include");
		assertToken(Tokens.FUNCTION, "fn");
		assertToken(Tokens.EXTENDS, "ext");
		assertToken(Tokens.IMPLEMENTS, "use");
		assertToken(Tokens.ASM, "asm");
		assertToken(Tokens.IF, "if");
		assertToken(Tokens.ELSE, "else");
		assertToken(Tokens.WHILE, "while");
		assertToken(Tokens.FOR, "for");
		assertToken(Tokens.ASSIGN, "=");
		assertToken(Tokens.EQUALS, "==");
		assertToken(Tokens.OR, "||");
		assertToken(Tokens.AND, "&&");
		assertToken(Tokens.LESSTHAN, "<");
		assertToken(Tokens.LESSTHANOREQUAL, "<=");
		assertToken(Tokens.GREATERTHAN, ">");
		assertToken(Tokens.GREATERTHANOREQUAL, ">=");
		assertToken(Tokens.PLUS, "+");
		assertToken(Tokens.MINUS, "-");
		assertToken(Tokens.TIMES, "*");
		assertToken(Tokens.DIV, "/");
		assertToken(Tokens.MODULUS, "%");
		assertToken(Tokens.RETURN, "ret");
		assertToken(Tokens.VOID, "void");
    }

    private void assertToken(Tokens expectedToken, String input)
    {
    	BufferedReader bufferedReader = new BufferedReader(new StringReader(input));
    	Lexer lexer = new Lexer(bufferedReader);
        Token token = lexer.nextToken();

        assertNotNull(token);
        assertEquals(expectedToken, token.getToken());
        assertEquals(input, token.getSymbol());
    }

    @Test
    public void testProcessIdentifier()
    {
    	BufferedReader bufferedReader = new BufferedReader(new StringReader("1010"));
    	Lexer lexer = new Lexer(bufferedReader);
    	Token token = lexer.processIdentifier();

    	assertNull(token);
    }
}