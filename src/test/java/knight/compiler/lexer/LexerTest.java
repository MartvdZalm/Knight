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

import knight.helper.TestUtils;

public class LexerTest extends TestUtils
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
        assertToken(Tokens.ARROW, "->");
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
    public void testNextTokenKnightArguments()
    {
    	BufferedReader bufferedReader = new BufferedReader(new StringReader("(Person person, int index)"));
    	Lexer lexer = new Lexer(bufferedReader);

    	assertEquals(Tokens.LEFTPAREN, lexer.nextToken().getToken());
    	assertEquals(Tokens.IDENTIFIER, lexer.nextToken().getToken());
    	assertEquals(Tokens.IDENTIFIER, lexer.nextToken().getToken());
    	assertEquals(Tokens.COMMA, lexer.nextToken().getToken());
    	assertEquals(Tokens.INTEGER, lexer.nextToken().getToken());
    	assertEquals(Tokens.IDENTIFIER, lexer.nextToken().getToken());
    	assertEquals(Tokens.RIGHTPAREN, lexer.nextToken().getToken());
        assertNull(lexer.nextToken());
    }

    @Test
    public void testNextTokenKnightVariables()
    {
    	BufferedReader bufferedReader = new BufferedReader(new StringReader("int age = 18; string name = \"Mart van der Zalm\"; bool adult = true;"));
    	Lexer lexer = new Lexer(bufferedReader);

    	assertEquals(Tokens.INTEGER, lexer.nextToken().getToken());
    	assertEquals(Tokens.IDENTIFIER, lexer.nextToken().getToken());
    	assertEquals(Tokens.ASSIGN, lexer.nextToken().getToken());
    	assertEquals(Tokens.INTEGER, lexer.nextToken().getToken());
    	assertEquals(Tokens.SEMICOLON, lexer.nextToken().getToken());
    	assertEquals(Tokens.STRING, lexer.nextToken().getToken());
    	assertEquals(Tokens.IDENTIFIER, lexer.nextToken().getToken());
    	assertEquals(Tokens.ASSIGN, lexer.nextToken().getToken());
    	assertEquals(Tokens.STRING, lexer.nextToken().getToken());
    	assertEquals(Tokens.SEMICOLON, lexer.nextToken().getToken());
    	assertEquals(Tokens.BOOLEAN, lexer.nextToken().getToken());
    	assertEquals(Tokens.IDENTIFIER, lexer.nextToken().getToken());
    	assertEquals(Tokens.ASSIGN, lexer.nextToken().getToken());
    	assertEquals(Tokens.TRUE, lexer.nextToken().getToken());
    	assertEquals(Tokens.SEMICOLON, lexer.nextToken().getToken());
        assertNull(lexer.nextToken());
    }


    @Test
    public void testNextTokenKnightFunction()
    {
        BufferedReader bufferedReader = new BufferedReader(new StringReader("fn alias(int harum, string quo, bool corporis, Et velit): void { int minima; string voluptates; Sed nihil; bool dolore = true; Beatae repellat = officiis; while (sint) { quia = \"qui\"; } if (a < 10) { ut = \"maiores\"; } else { commodi = voluptates; } vel = \"dolorum\"; asm {  \"call length\"  \"movq $1, %rax\"  \"movq %rdi, %rsi\"  \"xor %rdi, %rdi\"  \"movq %rcx, %rdx\"  \"syscall\"  }  }"));
        Lexer lexer = new Lexer(bufferedReader);

        assertEquals(Tokens.FUNCTION, lexer.nextToken().getToken());
        assertEquals(Tokens.IDENTIFIER, lexer.nextToken().getToken());
        assertEquals(Tokens.LEFTPAREN, lexer.nextToken().getToken());
        assertEquals(Tokens.INTEGER, lexer.nextToken().getToken());
        assertEquals(Tokens.IDENTIFIER, lexer.nextToken().getToken());
        assertEquals(Tokens.COMMA, lexer.nextToken().getToken());
        assertEquals(Tokens.STRING, lexer.nextToken().getToken());
        assertEquals(Tokens.IDENTIFIER, lexer.nextToken().getToken());
        assertEquals(Tokens.COMMA, lexer.nextToken().getToken());
        assertEquals(Tokens.BOOLEAN, lexer.nextToken().getToken());
        assertEquals(Tokens.IDENTIFIER, lexer.nextToken().getToken());
        assertEquals(Tokens.COMMA, lexer.nextToken().getToken());
        assertEquals(Tokens.IDENTIFIER, lexer.nextToken().getToken());
        assertEquals(Tokens.IDENTIFIER, lexer.nextToken().getToken());
        assertEquals(Tokens.RIGHTPAREN, lexer.nextToken().getToken());
        assertEquals(Tokens.COLON, lexer.nextToken().getToken());
        assertEquals(Tokens.VOID, lexer.nextToken().getToken());
        assertEquals(Tokens.LEFTBRACE, lexer.nextToken().getToken());

        assertEquals(Tokens.INTEGER, lexer.nextToken().getToken());
        assertEquals(Tokens.IDENTIFIER, lexer.nextToken().getToken());
        assertEquals(Tokens.SEMICOLON, lexer.nextToken().getToken());

        assertEquals(Tokens.STRING, lexer.nextToken().getToken());
        assertEquals(Tokens.IDENTIFIER, lexer.nextToken().getToken());
        assertEquals(Tokens.SEMICOLON, lexer.nextToken().getToken());

        assertEquals(Tokens.IDENTIFIER, lexer.nextToken().getToken());
        assertEquals(Tokens.IDENTIFIER, lexer.nextToken().getToken());
        assertEquals(Tokens.SEMICOLON, lexer.nextToken().getToken());

        assertEquals(Tokens.BOOLEAN, lexer.nextToken().getToken());
        assertEquals(Tokens.IDENTIFIER, lexer.nextToken().getToken());
        assertEquals(Tokens.ASSIGN, lexer.nextToken().getToken());
        assertEquals(Tokens.TRUE, lexer.nextToken().getToken());
        assertEquals(Tokens.SEMICOLON, lexer.nextToken().getToken());

        assertEquals(Tokens.IDENTIFIER, lexer.nextToken().getToken());
        assertEquals(Tokens.IDENTIFIER, lexer.nextToken().getToken());
        assertEquals(Tokens.ASSIGN, lexer.nextToken().getToken());
        assertEquals(Tokens.IDENTIFIER, lexer.nextToken().getToken());
        assertEquals(Tokens.SEMICOLON, lexer.nextToken().getToken());

        assertEquals(Tokens.WHILE, lexer.nextToken().getToken());
        assertEquals(Tokens.LEFTPAREN, lexer.nextToken().getToken());
        assertEquals(Tokens.IDENTIFIER, lexer.nextToken().getToken());
        assertEquals(Tokens.RIGHTPAREN, lexer.nextToken().getToken());
        assertEquals(Tokens.LEFTBRACE, lexer.nextToken().getToken());
        assertEquals(Tokens.IDENTIFIER, lexer.nextToken().getToken());
        assertEquals(Tokens.ASSIGN, lexer.nextToken().getToken());
        assertEquals(Tokens.STRING, lexer.nextToken().getToken());
        assertEquals(Tokens.SEMICOLON, lexer.nextToken().getToken());
        assertEquals(Tokens.RIGHTBRACE, lexer.nextToken().getToken());

        assertEquals(Tokens.IF, lexer.nextToken().getToken());
        assertEquals(Tokens.LEFTPAREN, lexer.nextToken().getToken());
        assertEquals(Tokens.IDENTIFIER, lexer.nextToken().getToken());
        assertEquals(Tokens.LESSTHAN, lexer.nextToken().getToken());
        assertEquals(Tokens.INTEGER, lexer.nextToken().getToken());
        assertEquals(Tokens.RIGHTPAREN, lexer.nextToken().getToken());
        assertEquals(Tokens.LEFTBRACE, lexer.nextToken().getToken());
        assertEquals(Tokens.IDENTIFIER, lexer.nextToken().getToken());
        assertEquals(Tokens.ASSIGN, lexer.nextToken().getToken());
        assertEquals(Tokens.STRING, lexer.nextToken().getToken());
        assertEquals(Tokens.SEMICOLON, lexer.nextToken().getToken());
        assertEquals(Tokens.RIGHTBRACE, lexer.nextToken().getToken());
        assertEquals(Tokens.ELSE, lexer.nextToken().getToken());
        assertEquals(Tokens.LEFTBRACE, lexer.nextToken().getToken());
        assertEquals(Tokens.IDENTIFIER, lexer.nextToken().getToken());
        assertEquals(Tokens.ASSIGN, lexer.nextToken().getToken());
        assertEquals(Tokens.IDENTIFIER, lexer.nextToken().getToken());
        assertEquals(Tokens.SEMICOLON, lexer.nextToken().getToken());
        assertEquals(Tokens.RIGHTBRACE, lexer.nextToken().getToken());

        assertEquals(Tokens.IDENTIFIER, lexer.nextToken().getToken());
        assertEquals(Tokens.ASSIGN, lexer.nextToken().getToken());
        assertEquals(Tokens.STRING, lexer.nextToken().getToken());
        assertEquals(Tokens.SEMICOLON, lexer.nextToken().getToken());

        assertEquals(Tokens.ASM, lexer.nextToken().getToken());
        assertEquals(Tokens.LEFTBRACE, lexer.nextToken().getToken());
        assertEquals(Tokens.STRING, lexer.nextToken().getToken());
        assertEquals(Tokens.STRING, lexer.nextToken().getToken());
        assertEquals(Tokens.STRING, lexer.nextToken().getToken());
        assertEquals(Tokens.STRING, lexer.nextToken().getToken());
        assertEquals(Tokens.STRING, lexer.nextToken().getToken());
        assertEquals(Tokens.STRING, lexer.nextToken().getToken());
        assertEquals(Tokens.RIGHTBRACE, lexer.nextToken().getToken());
        assertEquals(Tokens.RIGHTBRACE, lexer.nextToken().getToken());
        assertNull(lexer.nextToken());
    }

    @Test
    public void testNextTokenKnightClass()
    {
    	BufferedReader bufferedReader = new BufferedReader(new StringReader("class Person { int age = 18; string name = \"Mart van der Zalm\"; fn getName(): string { ret name; } }"));
    	Lexer lexer = new Lexer(bufferedReader);

    	assertEquals(Tokens.CLASS, lexer.nextToken().getToken());
    	assertEquals(Tokens.IDENTIFIER, lexer.nextToken().getToken());
    	assertEquals(Tokens.LEFTBRACE, lexer.nextToken().getToken());
    	assertEquals(Tokens.INTEGER, lexer.nextToken().getToken());
    	assertEquals(Tokens.IDENTIFIER, lexer.nextToken().getToken());
    	assertEquals(Tokens.ASSIGN, lexer.nextToken().getToken());
    	assertEquals(Tokens.INTEGER, lexer.nextToken().getToken());
    	assertEquals(Tokens.SEMICOLON, lexer.nextToken().getToken());
    	assertEquals(Tokens.STRING, lexer.nextToken().getToken());
    	assertEquals(Tokens.IDENTIFIER, lexer.nextToken().getToken());
    	assertEquals(Tokens.ASSIGN, lexer.nextToken().getToken());
    	assertEquals(Tokens.STRING, lexer.nextToken().getToken());
    	assertEquals(Tokens.SEMICOLON, lexer.nextToken().getToken());
    	assertEquals(Tokens.FUNCTION, lexer.nextToken().getToken());
    	assertEquals(Tokens.IDENTIFIER, lexer.nextToken().getToken());
    	assertEquals(Tokens.LEFTPAREN, lexer.nextToken().getToken());
    	assertEquals(Tokens.RIGHTPAREN, lexer.nextToken().getToken());
    	assertEquals(Tokens.COLON, lexer.nextToken().getToken());
    	assertEquals(Tokens.STRING, lexer.nextToken().getToken());
    	assertEquals(Tokens.LEFTBRACE, lexer.nextToken().getToken());
    	assertEquals(Tokens.RETURN, lexer.nextToken().getToken());
       	assertEquals(Tokens.IDENTIFIER, lexer.nextToken().getToken());
       	assertEquals(Tokens.SEMICOLON, lexer.nextToken().getToken());
    	assertEquals(Tokens.RIGHTBRACE, lexer.nextToken().getToken());
	   	assertEquals(Tokens.RIGHTBRACE, lexer.nextToken().getToken());
        assertNull(lexer.nextToken());
	}

    @Test
    public void testProcessIdentifier()
    {
    	BufferedReader bufferedReader = new BufferedReader(new StringReader("myAge"));
    	Lexer lexer = new Lexer(bufferedReader);
    	Token token = lexer.nextToken();

    	assertEquals(Tokens.IDENTIFIER, token.getToken());
    }

    @Test
    public void testProcessNumber()
    {
    	BufferedReader bufferedReader = new BufferedReader(new StringReader("1010"));
    	Lexer lexer = new Lexer(bufferedReader);
    	Token token = lexer.nextToken();

    	assertEquals(Tokens.INTEGER, token.getToken());
    }

    @Test
    public void testProcessString()
    {
    	BufferedReader bufferedReader = new BufferedReader(new StringReader("\"Hello World String\""));
    	Lexer lexer = new Lexer(bufferedReader);
    	Token token = lexer.nextToken();

    	assertEquals(Tokens.STRING, token.getToken());
    }

    @Test
    public void testMakeToken()
    {
    	BufferedReader bufferedReader = new BufferedReader(new StringReader("*"));
    	Lexer lexer = new Lexer(bufferedReader);
    	Token token = lexer.nextToken();

    	assertEquals(Tokens.TIMES, token.getToken());
    }

    @Test
    public void testMakeTokenInvalid()
    {
    	BufferedReader bufferedReader = new BufferedReader(new StringReader("^"));
    	Lexer lexer = new Lexer(bufferedReader);
    	Token token = lexer.nextToken();

    	assertNull(token);
    	assertTrue(lexer.exception);
    }
}