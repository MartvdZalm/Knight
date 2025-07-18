package knight.compiler.semantics.model;

import knight.compiler.ast.types.ASTIdentifierType;
import knight.compiler.ast.types.ASTStringType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SymbolInterfaceTest
{
	@Test
	public void testInterface_Creation()
	{
		SymbolInterface symbolInterface = new SymbolInterface("Drawable");

		assertEquals("Drawable", symbolInterface.getName());
		assertInstanceOf(ASTIdentifierType.class, symbolInterface.getType());
	}

	@Test
	public void testInterface_Functions()
	{
		SymbolInterface symbolInterface = new SymbolInterface("Serializable");

		assertTrue(symbolInterface.addFunction("serialize", new ASTStringType(null)));
		assertFalse(symbolInterface.addFunction("serialize", new ASTStringType(null)));

		assertTrue(symbolInterface.hasFunction("serialize"));
		assertNotNull(symbolInterface.getFunction("serialize"));
	}
}
