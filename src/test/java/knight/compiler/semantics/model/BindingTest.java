package knight.compiler.semantics.model;

import knight.compiler.ast.types.ASTIntType;
import knight.compiler.ast.types.ASTType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
