package knight.parser;

import java.io.*;
import static org.junit.Assert.*;
import org.junit.Test;
import java.util.*;

import knight.parser.*;
import knight.ast.*;
import knight.ast.Class;
import knight.lexer.*;

import knight.helper.*;

public class ParserTest
{
	@Test
	public void testParseIncludeValid() throws ParseException
	{
        Parser parser = getParser("include example");
        Lexer lexer = parser.lexer;

        parser.token = lexer.nextToken();
        
        Include include = parser.parseInclude();
        
        assertNotNull(include);
        assertEquals("example", include.getId().toString());
	}

	@Test
	public void testParseIncludeInvalid() throws ParseException
	{
		Parser parser = getParser("include");
		Lexer lexer = parser.lexer;

		parser.token = lexer.nextToken();

		Exception exception = assertThrows(ParseException.class, () ->
			parser.parseInclude());
        assertEquals("0:0 Token is null, cannot perform operation.", exception.getMessage());
	}

	@Test
	public void testParseClassValid() throws ParseException
	{
		Parser parser = getParser("class HelloWorld {}");
		Lexer lexer = parser.lexer;

		parser.token = lexer.nextToken();

		Class classDecl = parser.parseClass();

		assertNotNull(classDecl);
		assertEquals("HelloWorld", classDecl.getId().toString());
		assertEquals(0, classDecl.getFunctionListSize());
		assertEquals(0, classDecl.getVariableListSize());
	}

	@Test
	public void testParseClassInvalid() throws ParseException
	{
		Parser parser = getParser("class");
		Lexer lexer = parser.lexer;

		parser.token = lexer.nextToken();

		Exception exception = assertThrows(ParseException.class, () -> 
			parser.parseClass());
		assertEquals("0:0 Token is null, cannot perform operation.", exception.getMessage());
	}

 	@Test
    public void testParseClassWithFunctionsAndVariables() throws ParseException
    {
    	Builder builder = new Builder();

        List<String> functions = Arrays.asList(
            builder.buildFunction("calculation", "int a, int b", "int", "a * b"),
            builder.buildFunction("sum", "int x, int y", "int", "x + y")
        );

        List<String> variables = Arrays.asList(
            builder.buildVariable("int", "myVar"),
            builder.buildVariable("string", "myString")
        );

        String input = builder.buildClass("HelloWorld", functions, variables);
        
        Parser parser = getParser(input);
        Lexer lexer = parser.lexer;

        parser.token = lexer.nextToken();

        Class classDecl = parser.parseClass();

        assertEquals(2, classDecl.getFunctionListSize());

      	assertEquals("calculation", classDecl.getFunctionDeclAt(0).getId().toString());
      	assertTrue(classDecl.getFunctionDeclAt(0).getReturnType() instanceof IntType);

      	assertEquals("sum", classDecl.getFunctionDeclAt(1).getId().toString());
      	assertTrue(classDecl.getFunctionDeclAt(1).getReturnType() instanceof IntType);

        assertEquals(2, classDecl.getVariableListSize());

      	assertEquals("myVar", classDecl.getVariableDeclAt(0).getId().toString());
      	assertTrue(classDecl.getVariableDeclAt(0).getType() instanceof IntType);

      	assertEquals("myString", classDecl.getVariableDeclAt(1).getId().toString());
      	assertTrue(classDecl.getVariableDeclAt(1).getType() instanceof StringType);
    }

    private Parser getParser(String input)
	{
	    if (input == null || input.isEmpty()) {
	        throw new IllegalArgumentException("Input must not be empty");
	    }

		StringReader stringReader = new StringReader(input);
		BufferedReader bufferedReader = new BufferedReader(stringReader);
		Parser parser = new Parser(bufferedReader);

		return parser;
	}
}