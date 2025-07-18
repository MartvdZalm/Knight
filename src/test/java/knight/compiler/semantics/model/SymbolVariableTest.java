package knight.compiler.semantics.model;

import knight.compiler.ast.types.ASTIntType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SymbolVariableTest
{
	@Test
	public void testVariable_Creation()
	{
		SymbolVariable var = new SymbolVariable("x", new ASTIntType(null));

		assertEquals("x", var.getId());
		assertInstanceOf(ASTIntType.class, var.getType());
		assertEquals(-1, var.getLvIndex());

		var.setLvIndex(42);
		assertEquals(42, var.getLvIndex());
	}
}
