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
    public void tokenizationTest()
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
		assertToken(Tokens.INTERFACE, "inter");
		assertToken(Tokens.NEW, "new");
		assertToken(Tokens.INCLUDE, "include");
		assertToken(Tokens.FUNCTION, "fn");
		assertToken(Tokens.ENUMERATION, "enum");
		assertToken(Tokens.EXTENDS, "ext");
		assertToken(Tokens.IMPLEMENTS, "use");
		assertToken(Tokens.IF, "if");
		assertToken(Tokens.ELSE, "else");
		assertToken(Tokens.WHILE, "while");
		assertToken(Tokens.FOR, "for");
		assertToken(Tokens.ASSIGN, "=");
		assertToken(Tokens.EQUALS, "==");
		assertToken(Tokens.AND, "and");
		assertToken(Tokens.OR, "or");
		assertToken(Tokens.LESSTHAN, "<");
		assertToken(Tokens.LESSTHANOREQUAL, "<=");
		assertToken(Tokens.GREATERTHAN, ">");
		assertToken(Tokens.GREATERTHANOREQUAL, ">=");
		assertToken(Tokens.PLUS, "+");
		assertToken(Tokens.INCREMENT, "++");
		assertToken(Tokens.MINUS, "-");
		assertToken(Tokens.TIMES, "*");
		assertToken(Tokens.DIV, "/");
		assertToken(Tokens.MODULUS, "%");
		assertToken(Tokens.RETURN, "ret");
		assertToken(Tokens.VOID, "void");
    }

    private void assertToken(Tokens expectedToken, String input)
    {
        Token token = lexer.nextToken();

        assertNotNull(token);
        assertEquals(expectedToken, token.getToken());
        assertEquals(input, token.getSymbol());
    }
}