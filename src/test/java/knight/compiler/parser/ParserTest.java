package knight.compiler.parser;

import java.io.*;
import static org.junit.Assert.*;
import org.junit.Test;
import java.util.*;

import knight.compiler.parser.*;
import knight.compiler.ast.*;
import knight.compiler.ast.Class;
import knight.compiler.lexer.*;
import knight.builder.code.*;

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
    	CodeBuilderClass codeBuilderClass = new CodeBuilderClass("MyClass");

    	Map<String, Integer> data = new HashMap<>();
		data.put("function", 2);
		data.put("variable", 3);
    	codeBuilderClass.mock(data);

    	Parser parser = getParser(codeBuilderClass.toString());
    	Lexer lexer = parser.lexer;
    	parser.token = lexer.nextToken();
    	Class classDecl = parser.parseClass();

    	assertNotNull(classDecl);
    	assertEquals("MyClass", classDecl.getId().toString());
    	assertEquals(2, classDecl.getFunctionListSize());
    	assertEquals(3, classDecl.getVariableListSize());
    }

    @Test
    public void testParseFunctionReturnValid() throws ParseException
    {
    	CodeBuilderFunction codeBuilderFunction = new CodeBuilderFunction("MyFunction");
    	codeBuilderFunction.setReturnType(CodeBuilderType.INT);
    	codeBuilderFunction.mock();

    	Parser parser = getParser(codeBuilderFunction.toString());
    	Lexer lexer = parser.lexer;
    	parser.token = lexer.nextToken();
    	Function functionDecl = parser.parseFunction();

    	assertNotNull(functionDecl);
    	assertTrue("FunctionDecl is not instanceof FunctionReturn", functionDecl instanceof FunctionReturn);
    	assertEquals("MyFunction", functionDecl.getId().toString());
    	assertTrue("ReturnType is not instanceof IntType", functionDecl.getReturnType() instanceof IntType);
    	assertEquals(0, functionDecl.getVariableListSize());
    	assertEquals(0, functionDecl.getStatementListSize());
    	
    }

    // @Test
    // public void testParseFunctionValid() throws ParseException
    // {

    // }

    // @Test
    // public void parseFunctionInvalid() throws ParseException
    // {
    // 	Parser parser = getParser("fn");
	// 	Lexer lexer = parser.lexer;

	// 	parser.token = lexer.nextToken();

	// 	Exception exception = assertThrows(ParseException.class, () -> 
	// 		parser.parseFunction());
	// 	assertEquals("0:0 Token is null, cannot perform operation.", exception.getMessage());
    // }

    // @Test
    // public void parseFunctionWithVariablesAndStatements() throws ParseException
    // {
    // 	CodeBuilder codeBuilder = new CodeBuilder();

    // 	List<String> variables = Arrays.asList(
    // 		codeBuilder.buildVariable("int", "myVar")
    // 	);

    // 	List<String> statements = Arrays.asList(
    // 		codeBuilder.buildStatement("myVar", "10")
    // 	);
    // }


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