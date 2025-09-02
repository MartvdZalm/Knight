package knight.compiler.semantics.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import knight.compiler.ast.types.ASTIntType;
import knight.compiler.ast.types.ASTType;

public class BindingTest
{
	@Test
	public void testBindingCreation()
	{
		ASTType astType = new ASTIntType(null);
		Binding binding = new SymbolVariable("test", astType);

		assertNotNull(binding.getType());
		assertEquals(astType, binding.getType());
		assertNotNull(binding.toString());
	}
}
