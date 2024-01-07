package knight.parser;

import java.io.*;
import static org.junit.Assert.*;
import org.junit.Test;

import knight.parser.*;
import knight.ast.*;

public class ParserTest
{
	Tree tree;

	public ParserTest()
	{
		try {
			InputStream ioStream = this.getClass().getClassLoader().getResourceAsStream("ParserTest.txt");
			Reader reader = new InputStreamReader(ioStream);
			BufferedReader br = new BufferedReader(reader);

			Parser parser = new Parser(br);
			this.tree = parser.parse();
		} catch(ParseException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void programTest()
	{
        Program program = (Program) this.tree;

        // Test include list
        assertEquals(2, program.getIncludeListSize());
        assertEquals("print", program.getIncludeDeclAt(0).getId().toString());
        assertEquals("math", program.getIncludeDeclAt(1).getId().toString());

        // Test variable list
        assertEquals(2, program.getVariableListSize());

        // Test variable 1
        Variable var1 = program.getVariableDeclAt(0);
        assertEquals("num1", var1.getId().toString());
        assertTrue(var1 instanceof VariableInit);

        // Test variable 1 type
        assertTrue(var1.getType() instanceof IntType);

        // Test variable 1 expression
        VariableInit varInit1 = (VariableInit) var1;
        Expression expr1 = varInit1.getExpr();
        assertTrue(expr1 instanceof IntLiteral);

        // Test variable 1 value
        IntLiteral intLit1 = (IntLiteral) expr1;
        assertEquals(20, intLit1.getValue());

        // Test variable 2
        Variable var2 = program.getVariableDeclAt(1);
        assertEquals("result", var2.getId().toString());
        assertTrue(var2 instanceof Variable);

        // Test variable 2 type
        assertTrue(var2.getType() instanceof IntType);

	}
}