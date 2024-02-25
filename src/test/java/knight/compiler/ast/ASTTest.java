package knight.compiler.ast;

import java.io.*;
import static org.junit.Assert.*;
import org.junit.Test;

import knight.compiler.parser.*;

import knight.compiler.ast.declarations.*;
import knight.compiler.ast.expressions.*;
import knight.compiler.ast.expressions.operations.*;
import knight.compiler.ast.statements.*;
import knight.compiler.ast.statements.conditionals.*;
import knight.compiler.ast.types.*;
import knight.compiler.ast.*;

public class ASTTest
{
    ASTProgram program;

	public ASTTest()
	{
		try {
			InputStream ioStream = this.getClass().getClassLoader().getResourceAsStream("ASTTest.knight");
			Reader reader = new InputStreamReader(ioStream);
			BufferedReader br = new BufferedReader(reader);

			Parser parser = new Parser(br);
			AST tree = parser.parse();
            this.program = (ASTProgram) tree;
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
        assertTrue(program.getFunctionDeclAt(0).getReturnType() instanceof ASTIntType);

        assertEquals("main", program.getFunctionDeclAt(1).getId().toString());
        assertTrue(program.getFunctionDeclAt(1).getReturnType() instanceof ASTIntType);
    }

    @Test
    public void variableTest()
    {
        assertEquals(2, program.getVariableListSize());

        assertEquals("num1", program.getVariableDeclAt(0).getId().toString());
        assertTrue(program.getVariableDeclAt(0) instanceof ASTVariableInit);

        assertEquals("result", program.getVariableDeclAt(1).getId().toString());
        assertTrue(program.getVariableDeclAt(1) instanceof ASTVariable);
    }

    @Test
    public void ifStatementTest()
    {
        ASTFunction mainFunction = program.getFunctionDeclAt(1);

        assertEquals(1, mainFunction.getStatementListSize());
        assertTrue(mainFunction.getStatementDeclAt(0) instanceof ASTIfThenElse);
    }
}