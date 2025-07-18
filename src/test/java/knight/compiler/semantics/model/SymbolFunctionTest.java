package knight.compiler.semantics.model;

import knight.compiler.ast.types.ASTIntType;
import knight.compiler.ast.types.ASTStringType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SymbolFunctionTest
{
	@Test
	public void testFunction_Creation()
	{
		SymbolFunction symbolFunction = new SymbolFunction("print", new ASTStringType(null));

		assertEquals("print", symbolFunction.getId());
		assertInstanceOf(ASTStringType.class, symbolFunction.getType());
		assertEquals(0, symbolFunction.getParamsSize());
	}

	@Test
	public void testFunction_Parameters()
	{
		SymbolFunction func = new SymbolFunction("add", new ASTIntType(null));

		assertTrue(func.addParam("a", new ASTIntType(null)));
		assertTrue(func.addParam("b", new ASTIntType(null)));
		assertFalse(func.addParam("a", new ASTIntType(null)));

		assertEquals(2, func.getParamsSize());
		assertNotNull(func.getParam("a"));
		assertNotNull(func.getParamAt(1));
		assertNull(func.getParam("c"));
	}
}
