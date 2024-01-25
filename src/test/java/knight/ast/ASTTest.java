package knight.ast;

import java.io.*;
import static org.junit.Assert.*;
import org.junit.Test;

import knight.parser.*;
import knight.ast.*;

public class ASTTest
{
    Program program;

	public ASTTest()
	{
		try {
			InputStream ioStream = this.getClass().getClassLoader().getResourceAsStream("ASTTest.knight");
			Reader reader = new InputStreamReader(ioStream);
			BufferedReader br = new BufferedReader(reader);

			Parser parser = new Parser(br);
			Tree tree = parser.parse();
            this.program = (Program) tree;
		} catch(ParseException e) {
			e.printStackTrace();
		}
	}

    @Test
    public void includeTest()
    {
        assertEquals(2, program.getIncludeListSize());

        assertEquals("print", program.getIncludeDeclAt(0).getId().toString());
        assertEquals("math", program.getIncludeDeclAt(1).getId().toString());
    }

    @Test
    public void classTest()
    {
        assertEquals(1, program.getClassListSize());

        assertEquals("person", program.getClassDeclAt(0).getId().toString());
    }

    @Test
    public void functionTest()
    {
        assertEquals(2, program.getFunctionListSize());

        assertEquals("calculate", program.getFunctionDeclAt(0).getId().toString());
        assertTrue(program.getFunctionDeclAt(0).getReturnType() instanceof IntType);

        assertEquals("main", program.getFunctionDeclAt(1).getId().toString());
        assertTrue(program.getFunctionDeclAt(1).getReturnType() instanceof IntType);
    }

    @Test
    public void variableTest()
    {
        assertEquals(2, program.getVariableListSize());

        assertEquals("num1", program.getVariableDeclAt(0).getId().toString());
        assertTrue(program.getVariableDeclAt(0) instanceof VariableInit);

        assertEquals("result", program.getVariableDeclAt(1).getId().toString());
        assertTrue(program.getVariableDeclAt(1) instanceof Variable);
    }

    @Test
    public void ifStatementTest()
    {
        Function mainFunction = program.getFunctionDeclAt(1);

        assertEquals(1, mainFunction.getStatementListSize());
        assertTrue(mainFunction.getStatementDeclAt(0) instanceof IfThenElse);
    }
}