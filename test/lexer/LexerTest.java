package test.lexer;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import src.lexer.*;

public class LexerTest
{
    private Token token;
    private Lexer lexer;

    @Before
    public void setUp()
    {
        lexer = new Lexer("example.knight");
        token = lexer.nextToken();
    }

    @Test
    public void nextTokenTest()
    {
        assertEquals("Start object found", Tokens.CLASS, token.getToken());
    }
}
