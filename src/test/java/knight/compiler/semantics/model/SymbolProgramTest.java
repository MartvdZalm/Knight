package knight.compiler.semantics.model;

import knight.compiler.ast.types.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SymbolProgramTest
{
	private SymbolProgram program;
	private ASTIntType intType;
	private ASTStringType stringType;
	private ASTBooleanType boolType;
	private ASTIntArrayType intArrayType;
	private ASTIdentifierType personType;
	private ASTIdentifierType employeeType;
	private ASTIdentifierType drawableType;

	@BeforeEach
	public void setUp()
	{
		this.program = new SymbolProgram();
		this.intType = new ASTIntType(null);
		this.stringType = new ASTStringType(null);
		this.boolType = new ASTBooleanType(null);
		this.intArrayType = new ASTIntArrayType(null);
		this.personType = new ASTIdentifierType(null, "Person");
		this.employeeType = new ASTIdentifierType(null, "Employee");
		this.drawableType = new ASTIdentifierType(null, "Drawable");

		this.program.addClass("Person", null);
		this.program.addClass("Employee", "Person");
		this.program.addInterface("Drawable");
	}

	@Test
	public void addClass_returnsFalse_forDuplicateClasses()
	{
		assertTrue(program.addClass("Animal", null));
		assertFalse(program.addClass("Animal", null));
	}

	@Test
	public void getClass_returnsNull_forNonexistentClass()
	{
		assertNull(program.getClass("Nonexistent"));
	}

	@Test
	public void addInterface_preventsDuplicateInterfaces()
	{
		assertTrue(program.addInterface("Serializable"));
		assertFalse(program.addInterface("Serializable"));
	}

	@Test
	public void getFunction_resolvesThroughClassHierarchy()
	{
		program.getClass("Person").addFunction("getName", stringType);
		SymbolFunction func = program.getFunction("getName", "Employee");
		assertNotNull(func);
		assertEquals("getName", func.getId());
	}

	@Test
	public void getVariable_resolvesThroughClassHierarchy()
	{
		program.getClass("Person").addVariable("id", intType);
		SymbolVariable var = program.getVariable("id", program.getClass("Employee"), null);
		assertNotNull(var);
		assertEquals("id", var.getId());
	}

	@Test
	public void compareTypes_handlesPrimitiveTypesCorrectly()
	{
		assertTrue(program.compareTypes(intType, intType));
		assertTrue(program.compareTypes(stringType, stringType));
		assertTrue(program.compareTypes(boolType, boolType));
		assertTrue(program.compareTypes(intArrayType, intArrayType));

		assertFalse(program.compareTypes(intType, stringType));
		assertFalse(program.compareTypes(boolType, intArrayType));
	}

	@Test
	public void compareTypes_handlesClassInheritance()
	{
		assertTrue(program.compareTypes(personType, employeeType));
		assertFalse(program.compareTypes(employeeType, personType));
	}

	@Test
	public void compareTypes_handlesInterfacesStrictly()
	{
		ASTIdentifierType drawable2 = new ASTIdentifierType(null, "Drawable");
		ASTIdentifierType serializable = new ASTIdentifierType(null, "Serializable");

		assertTrue(program.compareTypes(drawableType, drawable2));
		assertFalse(program.compareTypes(drawableType, serializable));
	}

	@Test
	public void containsVariable_returnsFalse_forNonexistentVariable()
	{
		assertFalse(program.containsVariable("nonexistent"));
	}

	@Test
	public void getFunctionType_returnsNull_forNonexistentFunction()
	{
		assertNull(program.getFunctionType("nonexistent", "Person"));
	}

	@Test
	public void interfaceExists_returnsFalse_forNonexistentInterface()
	{
		assertFalse(program.interfaceExists("NonexistentInterface"));
	}
}
