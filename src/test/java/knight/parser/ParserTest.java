package knight.parser;

import java.io.*;
import static org.junit.Assert.*;
import org.junit.Test;

import knight.parser.*;
import knight.ast.*;
import knight.lexer.*;

public class ParserTest
{

	public ParserTest()
	{

	}

	@Test
	public void testParseIncludeValid()
	{
        StringReader input = new StringReader("include example");
        BufferedReader bufferedReader = new BufferedReader(input);
        
        Parser parser = new Parser(bufferedReader);
        Lexer lexer = parser.lexer;

        parser.token = lexer.nextToken();
        
        Include include = null;
        try {
            include = parser.parseInclude();
        } catch (ParseException e) {
            e.printStackTrace();
            fail("ParseException occurred during parsing");
        }
        
        assertNotNull(include);
        assertEquals("example", include.getId().toString());
	}

	@Test
	public void testParseIncludeInvalid()
	{

	}
}