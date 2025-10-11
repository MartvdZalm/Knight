package knight.compiler.semantics;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import knight.compiler.ast.program.ASTClass;
import knight.compiler.ast.program.ASTIdentifier;
import knight.compiler.ast.program.ASTProgram;
import knight.compiler.ast.program.ASTProperty;
import knight.compiler.ast.types.ASTIntType;
import knight.compiler.ast.types.ASTType;
import knight.compiler.lexer.Token;
import knight.compiler.semantics.diagnostics.DiagnosticReporter;
import knight.compiler.semantics.model.SymbolClass;
import knight.compiler.semantics.model.SymbolFunction;
import knight.compiler.semantics.model.SymbolProgram;
import knight.compiler.semantics.model.SymbolProperty;
import knight.compiler.semantics.model.SymbolVariable;
import knight.compiler.semantics.utils.ScopeManager;

public class NameAnalyserTest
{
	@Mock
	private SymbolProgram symbolProgram;

	@Mock
	private ScopeManager scopeManager;

	@InjectMocks
	private NameAnalyser nameAnalyser;

	private Token dummyToken;

	@BeforeEach
	public void setUp()
	{
		MockitoAnnotations.openMocks(this);
		DiagnosticReporter.clear();
		dummyToken = new Token(null, 0, 0);
		nameAnalyser = new NameAnalyser(symbolProgram);
	}

	// Helper methods
	private ASTIdentifier createMockIdentifier(String name)
	{
		ASTIdentifier identifier = mock(ASTIdentifier.class);
		when(identifier.getName()).thenReturn(name);
		return identifier;
	}

	private SymbolVariable createMockSymbolVariable(String name, ASTType type)
	{
		SymbolVariable variable = mock(SymbolVariable.class);
		when(variable.getName()).thenReturn(name);
		when(variable.getType()).thenReturn(type);
		return variable;
	}

	private SymbolClass createMockSymbolClass(String name)
	{
		SymbolClass symbolClass = mock(SymbolClass.class);
		when(symbolClass.getName()).thenReturn(name);
		return symbolClass;
	}

	private SymbolFunction createMockSymbolFunction(String name)
	{
		SymbolFunction function = mock(SymbolFunction.class);
		when(function.getName()).thenReturn(name);
		return function;
	}

	private SymbolProperty createMockSymbolProperty(String name)
	{
		SymbolProperty property = mock(SymbolProperty.class);
		when(property.getName()).thenReturn(name);
		return property;
	}

	@Test
	public void testVisitASTProgram()
	{
		// Arrange
		ASTProgram program = mock(ASTProgram.class);
		ASTClass astClass = mock(ASTClass.class);
		when(program.getNodes()).thenReturn(Arrays.asList(astClass));

		// Act
		ASTType result = nameAnalyser.visit(program);

		// Assert
		assertNull(result);
		verify(astClass, times(1)).accept(nameAnalyser);
	}

	@Test
	public void testVisitASTClass_AlreadyProcessed()
	{
		// Arrange
		String className = "TestClass";
		ASTClass astClass = mock(ASTClass.class);
		ASTIdentifier identifier = createMockIdentifier(className);
		when(astClass.getIdentifier()).thenReturn(identifier);
		nameAnalyser.processedClasses.add(className);

		// Act
		ASTType result = nameAnalyser.visit(astClass);

		// Assert
		assertNull(result);
		assertTrue(DiagnosticReporter.getErrors().isEmpty());
	}

	@Test
	public void testVisitASTClass_NotFound()
	{
		// Arrange
		String className = "TestClass";
		ASTClass astClass = mock(ASTClass.class);
		ASTIdentifier identifier = createMockIdentifier(className);
		when(astClass.getIdentifier()).thenReturn(identifier);
		when(astClass.getToken()).thenReturn(dummyToken);
		when(symbolProgram.getClass(className)).thenReturn(null);

		// Act
		ASTType result = nameAnalyser.visit(astClass);

		// Assert
		assertNull(result);
		assertFalse(DiagnosticReporter.getErrors().isEmpty());
	}

	@Test
	public void testVisitASTProperty_Success()
	{
		// Arrange
		ASTProperty property = mock(ASTProperty.class);
		ASTIdentifier identifier = createMockIdentifier("testProperty");
		ASTIntType type = mock(ASTIntType.class);
		SymbolClass currentClass = createMockSymbolClass("TestClass");
		SymbolProperty symbolProperty = createMockSymbolProperty("testProperty");

		when(property.getIdentifier()).thenReturn(identifier);
		when(property.getType()).thenReturn(type);
		when(currentClass.getProperty("testProperty")).thenReturn(symbolProperty);

		nameAnalyser.getScopeManager().enterClass(currentClass);

		// Act
		ASTType result = nameAnalyser.visit(property);

		// Assert
		assertNull(result);
		verify(type, times(1)).accept(nameAnalyser);
		verify(identifier, times(1)).setBinding(symbolProperty);

		nameAnalyser.getScopeManager().exitClass();
	}
}
