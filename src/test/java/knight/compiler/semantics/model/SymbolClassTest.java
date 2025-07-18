package knight.compiler.semantics.model;

import knight.compiler.ast.types.ASTIdentifierType;
import knight.compiler.ast.types.ASTIntType;
import knight.compiler.ast.types.ASTStringType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SymbolClassTest
{
	@Test
	public void testClass_Creation()
	{
		SymbolClass symbolClass = new SymbolClass("Person", null);

		assertEquals("Person", symbolClass.getId());
		assertNull(symbolClass.parent());
		assertInstanceOf(ASTIdentifierType.class, symbolClass.type());
	}

	@Test
	public void testClass_Members()
	{
		SymbolClass symbolClass = new SymbolClass("Person", null);

		assertTrue(symbolClass.addFunction("getName", new ASTStringType(null)));
		assertFalse(symbolClass.addFunction("getName", new ASTStringType(null)));

		assertTrue(symbolClass.addVariable("age", new ASTIntType(null)));
		assertFalse(symbolClass.addVariable("age", new ASTIntType(null)));

		assertNotNull(symbolClass.getFunction("getName"));
		assertNotNull(symbolClass.getVariable("age"));
	}
}
