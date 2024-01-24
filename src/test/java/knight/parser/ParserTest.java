package knight.parser;

import java.io.*;
import static org.junit.Assert.*;
import org.junit.Test;

import knight.parser.*;
import knight.ast.*;
import knight.lexer.*;

public class ParserTest
{
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
        }
        
        assertNotNull(include);
        assertEquals("example", include.getId().toString());
	}

	@Test
	public void testParseIncludeInvalid() throws ParseException
	{
		StringReader input = new StringReader("include");
		BufferedReader bufferedReader = new BufferedReader(input);

		Parser parser = new Parser(bufferedReader);
		Lexer lexer = parser.lexer;

		parser.token = lexer.nextToken();

		Exception exception = assertThrows(ParseException.class, () ->
			parser.parseInclude());
        assertEquals("0:0 Token is null, cannot perform operation.", exception.getMessage());
	}
}