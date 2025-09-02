package knight.compiler.semantics.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import knight.compiler.ast.types.ASTIntType;
import knight.compiler.ast.types.ASTStringType;
import knight.compiler.lexer.Token;

public class ScopeTest
{
	private ASTIntType intType;
	private ASTStringType stringType;

	@BeforeEach
	public void setUp()
	{
		intType = new ASTIntType(new Token(null, 0, 0));
		stringType = new ASTStringType(new Token(null, 0, 0));
	}

	@Test
	public void addVariable_should_return_true_for_new_variable_in_root_scope()
	{
		Scope scope = new Scope(null);
		assertTrue(scope.addVariable("x", intType));
		assertEquals(1, scope.getVariableCount());
	}

	@Test
	public void addVariable_should_return_false_for_duplicate_in_same_scope()
	{
		Scope scope = new Scope(null);
		scope.addVariable("x", intType);
		assertFalse(scope.addVariable("x", stringType));
		assertEquals(1, scope.getVariableCount());
	}

	@Test
	public void addVariable_should_return_false_when_variable_exists_in_parent_scope()
	{
		Scope parentScope = new Scope(null);
		Scope childScope = new Scope(parentScope);

		parentScope.addVariable("x", intType);
		assertFalse(childScope.addVariable("x", stringType));
		assertEquals(0, childScope.getVariableCount());
	}

	@Test
	public void addVariable_should_allow_different_variables_in_different_scopes()
	{
		Scope parentScope = new Scope(null);
		Scope childScope = new Scope(parentScope);

		parentScope.addVariable("parentVar", intType);
		assertTrue(childScope.addVariable("childVar", stringType));
		assertEquals(1, parentScope.getVariableCount());
		assertEquals(1, childScope.getVariableCount());
	}

	@Test
	public void addVariable_should_return_true_for_same_name_in_unrelated_scopes()
	{
		Scope scope1 = new Scope(null);
		Scope scope2 = new Scope(null);

		assertTrue(scope1.addVariable("x", intType));
		assertTrue(scope2.addVariable("x", stringType));
	}

	@Test
	public void getVariable_should_find_variable_in_current_scope()
	{
		Scope scope = new Scope(null);

		scope.addVariable("x", intType);
		SymbolVariable variable = scope.getVariable("x");

		assertNotNull(variable);
		assertEquals("x", variable.getName());
		assertEquals(intType, variable.getType());
	}

	@Test
	public void getVariable_should_find_variable_in_parent_scope()
	{
		Scope parentScope = new Scope(null);
		Scope childScope = new Scope(parentScope);

		parentScope.addVariable("x", intType);
		SymbolVariable variable = childScope.getVariable("x");

		assertNotNull(variable);
		assertEquals("x", variable.getName());
		assertEquals(intType, variable.getType());
	}

	@Test
	public void getVariable_should_return_null_for_nonexistent_variable()
	{
		Scope parentScope = new Scope(null);
		Scope childScope = new Scope(parentScope);

		assertNull(parentScope.getVariable("nonexistent"));
		assertNull(childScope.getVariable("nonexistent"));
	}

	@Test
	public void containsVariable_should_return_true_for_variable_in_current_scope()
	{
		Scope scope = new Scope(null);
		scope.addVariable("x", intType);
		assertTrue(scope.containsVariable("x"));
	}

	@Test
	public void containsVariable_should_return_false_for_variable_only_in_parent_scope()
	{
		Scope parentScope = new Scope(null);
		Scope childScope = new Scope(parentScope);
		parentScope.addVariable("x", intType);
		assertFalse(childScope.containsVariable("x"));
	}

	@Test
	public void containsVariable_should_return_false_for_nonexistent_variable()
	{
		Scope parentScope = new Scope(null);
		Scope childScope = new Scope(parentScope);
		assertFalse(parentScope.containsVariable("nonexistent"));
		assertFalse(childScope.containsVariable("nonexistent"));
	}

	@Test
	public void nested_scopes_should_work_correctly()
	{
		Scope grandparent = new Scope(null);
		Scope parent = new Scope(grandparent);
		Scope child = new Scope(parent);

		grandparent.addVariable("a", intType);
		parent.addVariable("b", stringType);
		child.addVariable("c", intType);

		assertNotNull(child.getVariable("a"));
		assertNotNull(child.getVariable("b"));
		assertNotNull(child.getVariable("c"));

		assertFalse(child.addVariable("a", stringType));
		assertFalse(child.addVariable("b", intType));
		assertFalse(child.addVariable("c", stringType));
	}
}
