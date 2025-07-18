package knight.compiler.semantics.model;

import knight.compiler.ast.types.ASTIntType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ScopeTest
{
	@Test
	public void testScope_Hierarchy()
	{
		Scope global = new Scope(null);
		Scope local = new Scope(global);

		assertTrue(global.addVariable("x", new ASTIntType(null)));
		assertTrue(local.addVariable("y", new ASTIntType(null)));

		assertNotNull(global.getVariable("x"));
		assertNotNull(local.getVariable("y"));
		assertNotNull(local.getVariable("x"));
		assertNull(global.getVariable("y"));

		assertTrue(local.addVariable("x", new ASTIntType(null)));
	}
}
