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
    	CodeBuilderClass codeBuilderClass = new CodeBuilderClass();

    	Map<String, Integer> data = new HashMap<>();
		data.put("functionCount", 2);

    	codeBuilderClass.mock(data);
    	System.out.println(codeBuilderClass);

    	// CodeBuilder codeBuilder = new CodeBuilder();

    	// List<String> arguments = Arrays.asList(
    	// 	codeBuilder.buildArgument(CodeBuilderTypes.INT, "a"),
    	// 	codeBuilder.buildArgument(CodeBuilderTypes.INT, "b")
    	// );

    	// List<String> variables = Arrays.asList(
    	// 	codeBuilder.buildVariable(CodeBuilderTypes.INT, "age"),
    	// 	codeBuilder.buildVariable(CodeBuilderTypes.STRING, "name")
    	// );

    	// List<String> statements = Arrays.asList(
    	// 	codeBuilder.buildStatement("age", "21"),
    	// 	codeBuilder.buildStatement("name", "john doe")
    	// );

    	// String returnExpr = codeBuilder.buildReturnExpression("0");

    	// System.out.println(codeBuilder.buildFunction("calculate", CodeBuilderTypes.INT, arguments, variables, statements, returnExpr));

        // String input = builder.buildClass("HelloWorld", functions, variables);
        
        // Parser parser = getParser(input);
        // Lexer lexer = parser.lexer;

        // parser.token = lexer.nextToken();

        // Class classDecl = parser.parseClass();

        // assertEquals(2, classDecl.getFunctionListSize());

      	// assertEquals("calculation", classDecl.getFunctionDeclAt(0).getId().toString());
      	// assertTrue(classDecl.getFunctionDeclAt(0).getReturnType() instanceof IntType);

      	// assertEquals("sum", classDecl.getFunctionDeclAt(1).getId().toString());
      	// assertTrue(classDecl.getFunctionDeclAt(1).getReturnType() instanceof IntType);

        // assertEquals(2, classDecl.getVariableListSize());

      	// assertEquals("myVar", classDecl.getVariableDeclAt(0).getId().toString());
      	// assertTrue(classDecl.getVariableDeclAt(0).getType() instanceof IntType);

      	// assertEquals("myString", classDecl.getVariableDeclAt(1).getId().toString());
      	// assertTrue(classDecl.getVariableDeclAt(1).getType() instanceof StringType);
    }

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