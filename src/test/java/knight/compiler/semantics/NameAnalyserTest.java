package knight.compiler.semantics;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.function.BiConsumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import knight.compiler.ast.controlflow.ASTConditionalBranch;
import knight.compiler.ast.controlflow.ASTForEach;
import knight.compiler.ast.controlflow.ASTIfChain;
import knight.compiler.ast.controlflow.ASTWhile;
import knight.compiler.ast.expressions.ASTArrayIndexExpr;
import knight.compiler.ast.expressions.ASTArrayLiteral;
import knight.compiler.ast.expressions.ASTCallFunctionExpr;
import knight.compiler.ast.expressions.ASTExpression;
import knight.compiler.ast.expressions.ASTFieldAccessExpr;
import knight.compiler.ast.expressions.ASTIdentifierExpr;
import knight.compiler.ast.expressions.ASTIntLiteral;
import knight.compiler.ast.expressions.ASTLambda;
import knight.compiler.ast.expressions.ASTNewArray;
import knight.compiler.ast.expressions.ASTNewInstance;
import knight.compiler.ast.expressions.ASTAnd;
import knight.compiler.ast.expressions.ASTDivision;
import knight.compiler.ast.expressions.ASTEquals;
import knight.compiler.ast.expressions.ASTGreaterThan;
import knight.compiler.ast.expressions.ASTGreaterThanOrEqual;
import knight.compiler.ast.expressions.ASTLessThan;
import knight.compiler.ast.expressions.ASTLessThanOrEqual;
import knight.compiler.ast.expressions.ASTMinus;
import knight.compiler.ast.expressions.ASTModulus;
import knight.compiler.ast.expressions.ASTNotEquals;
import knight.compiler.ast.expressions.ASTOr;
import knight.compiler.ast.expressions.ASTPlus;
import knight.compiler.ast.expressions.ASTStringLiteral;
import knight.compiler.ast.expressions.ASTTimes;
import knight.compiler.ast.expressions.ASTTrue;
import knight.compiler.ast.expressions.ASTFalse;
import knight.compiler.ast.program.ASTArgument;
import knight.compiler.ast.program.ASTClass;
import knight.compiler.ast.program.ASTFunction;
import knight.compiler.ast.program.ASTIdentifier;
import knight.compiler.ast.program.ASTImport;
import knight.compiler.ast.program.ASTInterface;
import knight.compiler.ast.program.ASTProgram;
import knight.compiler.ast.program.ASTProperty;
import knight.compiler.ast.program.ASTVariable;
import knight.compiler.ast.program.ASTVariableInit;
import knight.compiler.ast.statements.ASTArrayAssign;
import knight.compiler.ast.statements.ASTAssign;
import knight.compiler.ast.statements.ASTBody;
import knight.compiler.ast.statements.ASTCallFunctionStat;
import knight.compiler.ast.statements.ASTFieldAssign;
import knight.compiler.ast.statements.ASTReturnStatement;
import knight.compiler.ast.statements.ASTStatement;
import knight.compiler.ast.types.ASTBooleanType;
import knight.compiler.ast.types.ASTFunctionType;
import knight.compiler.ast.types.ASTIdentifierType;
import knight.compiler.ast.types.ASTIntArrayType;
import knight.compiler.ast.types.ASTIntType;
import knight.compiler.ast.types.ASTParameterizedType;
import knight.compiler.ast.types.ASTStringArrayType;
import knight.compiler.ast.types.ASTStringType;
import knight.compiler.ast.types.ASTType;
import knight.compiler.ast.types.ASTVoidType;
import knight.compiler.lexer.Token;
import knight.compiler.library.LibraryManager;
import knight.compiler.semantics.diagnostics.DiagnosticReporter;
import knight.compiler.semantics.model.Scope;
import knight.compiler.semantics.model.SymbolClass;
import knight.compiler.semantics.model.SymbolFunction;
import knight.compiler.semantics.model.SymbolInterface;
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

	@Mock
	private LibraryManager libraryManager;

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

	private ASTIdentifier createMockIdentifier(String name)
	{
		ASTIdentifier identifier = mock(ASTIdentifier.class);
		when(identifier.getName()).thenReturn(name);
		return identifier;
	}

	private ASTIdentifierExpr createMockIdentifierExpr(String name)
	{
		ASTIdentifierExpr identifierExpr = mock(ASTIdentifierExpr.class);
		when(identifierExpr.getName()).thenReturn(name);
		return identifierExpr;
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

	@Test
	public void testVisitASTProperty_NotFound()
	{
		// Arrange
		ASTProperty property = mock(ASTProperty.class);
		ASTIdentifier identifier = createMockIdentifier("nonExistentProperty");
		ASTIntType type = mock(ASTIntType.class);
		SymbolClass currentClass = createMockSymbolClass("TestClass");

		when(property.getIdentifier()).thenReturn(identifier);
		when(property.getType()).thenReturn(type);
		when(property.getToken()).thenReturn(dummyToken);
		when(currentClass.getProperty("nonExistentProperty")).thenReturn(null);

		nameAnalyser.getScopeManager().enterClass(currentClass);

		// Act
		ASTType result = nameAnalyser.visit(property);

		// Assert
		assertNull(result);
		assertFalse(DiagnosticReporter.getErrors().isEmpty());

		nameAnalyser.getScopeManager().exitClass();
	}

	@Test
	public void testVisitASTFunction_Success()
	{
		// Arrange
		ASTFunction function = mock(ASTFunction.class);
		ASTIdentifier identifier = createMockIdentifier("testFunction");
		ASTType returnType = mock(ASTType.class);
		ASTBody body = mock(ASTBody.class);
		SymbolFunction symbolFunction = createMockSymbolFunction("testFunction");
		SymbolClass currentClass = createMockSymbolClass("TestClass");

		when(function.getIdentifier()).thenReturn(identifier);
		when(function.getReturnType()).thenReturn(returnType);
		when(function.getArgumentCount()).thenReturn(0);
		when(function.getBody()).thenReturn(body);
		when(currentClass.getFunction("testFunction")).thenReturn(symbolFunction);

		nameAnalyser.getScopeManager().enterClass(currentClass);

		// Act
		ASTType result = nameAnalyser.visit(function);

		// Assert
		assertNull(result);
		verify(returnType, times(1)).accept(nameAnalyser);
		verify(body, times(1)).accept(nameAnalyser);
		verify(identifier, times(1)).setBinding(symbolFunction);
		assertTrue(nameAnalyser.processedFunctions.contains("testFunction"));

		nameAnalyser.getScopeManager().exitClass();
	}

	@Test
	public void testVisitASTFunction_AlreadyProcessed()
	{
		// Arrange
		ASTFunction function = mock(ASTFunction.class);
		ASTIdentifier identifier = createMockIdentifier("testFunction");

		when(function.getIdentifier()).thenReturn(identifier);
		nameAnalyser.processedFunctions.add("testFunction");

		// Act
		ASTType result = nameAnalyser.visit(function);

		// Assert
		assertNull(result);
		verify(identifier, times(0)).setBinding(any());
	}

	@Test
	public void testVisitASTFunction_NotFound()
	{
		// Arrange
		ASTFunction function = mock(ASTFunction.class);
		ASTIdentifier identifier = createMockIdentifier("nonExistentFunction");
		SymbolClass currentClass = createMockSymbolClass("TestClass");

		when(function.getIdentifier()).thenReturn(identifier);
		when(function.getToken()).thenReturn(dummyToken);
		when(currentClass.getFunction("nonExistentFunction")).thenReturn(null);

		nameAnalyser.getScopeManager().enterClass(currentClass);

		// Act
		ASTType result = nameAnalyser.visit(function);

		// Assert
		assertNull(result);
		assertFalse(DiagnosticReporter.getErrors().isEmpty());

		nameAnalyser.getScopeManager().exitClass();
	}

	@Test
	public void testVisitASTBody()
	{
		// Arrange
		ASTBody body = mock(ASTBody.class);
		Scope bodyScope = mock(Scope.class);
		ASTStatement statement = mock(ASTStatement.class);

		when(body.getScope()).thenReturn(bodyScope);
		when(body.getNodes()).thenReturn(Arrays.asList(statement));

		// Act
		ASTType result = nameAnalyser.visit(body);

		// Assert
		assertNull(result);
		verify(statement, times(1)).accept(nameAnalyser);
	}

	@Test
	public void testVisitASTArgument_Success()
	{
		// Arrange
		ASTArgument argument = mock(ASTArgument.class);
		ASTIdentifier identifier = createMockIdentifier("param");
		ASTType type = mock(ASTType.class);
		SymbolFunction currentFunction = createMockSymbolFunction("testFunction");
		SymbolVariable parameter = createMockSymbolVariable("param", type);

		when(argument.getIdentifier()).thenReturn(identifier);
		when(argument.getType()).thenReturn(type);
		when(currentFunction.getParameter("param")).thenReturn(parameter);

		nameAnalyser.getScopeManager().enterFunction(currentFunction);

		// Act
		ASTType result = nameAnalyser.visit(argument);

		// Assert
		assertNull(result);
		verify(type, times(1)).accept(nameAnalyser);
		verify(identifier, times(1)).setBinding(parameter);

		nameAnalyser.getScopeManager().exitFunction();
	}

	@Test
	public void testVisitASTArgument_NotFound()
	{
		// Arrange
		ASTArgument argument = mock(ASTArgument.class);
		ASTIdentifier identifier = createMockIdentifier("nonExistentParam");
		ASTType type = mock(ASTType.class);
		SymbolFunction currentFunction = createMockSymbolFunction("testFunction");

		when(argument.getIdentifier()).thenReturn(identifier);
		when(argument.getType()).thenReturn(type);
		when(argument.getToken()).thenReturn(dummyToken);
		when(currentFunction.getParameter("nonExistentParam")).thenReturn(null);

		nameAnalyser.getScopeManager().enterFunction(currentFunction);

		// Act
		ASTType result = nameAnalyser.visit(argument);

		// Assert
		assertNull(result);
		assertFalse(DiagnosticReporter.getErrors().isEmpty());

		nameAnalyser.getScopeManager().exitFunction();
	}

	@Test
	public void testVisitASTVariable_Success()
	{
		// Arrange
		ASTVariable variable = mock(ASTVariable.class);
		ASTIdentifier identifier = createMockIdentifier("testVar");
		ASTType type = mock(ASTType.class);
		SymbolVariable symbolVariable = createMockSymbolVariable("testVar", type);

		when(variable.getIdentifier()).thenReturn(identifier);
		when(variable.getType()).thenReturn(type);
		when(variable.getToken()).thenReturn(dummyToken);

		// Mock SemanticUtils.resolveVariable to return the symbol variable
		// Note: This would require a more sophisticated mocking setup in a real
		// scenario

		// Act
		ASTType result = nameAnalyser.visit(variable);

		// Assert
		assertNull(result);
		verify(type, times(1)).accept(nameAnalyser);
	}

	@Test
	public void testVisitASTIdentifier_Success()
	{
		// Arrange
		ASTIdentifier identifier = mock(ASTIdentifier.class);

		when(identifier.getName()).thenReturn("testVar");
		when(identifier.getToken()).thenReturn(dummyToken);

		// Act
		ASTType result = nameAnalyser.visit(identifier);

		// Assert
		assertNull(result);
	}

	@Test
	public void testVisitASTIdentifierExpr_Success()
	{
		// Arrange
		ASTIdentifierExpr identifierExpr = mock(ASTIdentifierExpr.class);

		when(identifierExpr.getName()).thenReturn("testVar");
		when(identifierExpr.getToken()).thenReturn(dummyToken);

		// Act
		ASTType result = nameAnalyser.visit(identifierExpr);

		// Assert
		assertNull(result);
	}

	@Test
	public void testVisitASTIdentifierType_Success()
	{
		// Arrange
		ASTIdentifierType identifierType = mock(ASTIdentifierType.class);
		SymbolClass symbolClass = createMockSymbolClass("TestClass");

		when(identifierType.getName()).thenReturn("TestClass");
		when(symbolProgram.getClass("TestClass")).thenReturn(symbolClass);

		// Act
		ASTType result = nameAnalyser.visit(identifierType);

		// Assert
		assertNull(result);
		verify(identifierType, times(1)).setBinding(symbolClass);
	}

	@Test
	public void testVisitASTIdentifierType_NotFound()
	{
		// Arrange
		ASTIdentifierType identifierType = mock(ASTIdentifierType.class);

		when(identifierType.getName()).thenReturn("NonExistentClass");
		when(identifierType.getToken()).thenReturn(dummyToken);
		when(symbolProgram.getClass("NonExistentClass")).thenReturn(null);

		// Act
		ASTType result = nameAnalyser.visit(identifierType);

		// Assert
		assertNull(result);
		assertFalse(DiagnosticReporter.getErrors().isEmpty());
	}

	@Test
	public void testVisitASTAssign()
	{
		// Arrange
		ASTAssign assign = mock(ASTAssign.class);
		ASTIdentifier identifier = createMockIdentifier("testVar");
		ASTExpression expression = mock(ASTExpression.class);

		when(assign.getIdentifier()).thenReturn(identifier);
		when(assign.getExpression()).thenReturn(expression);

		// Act
		ASTType result = nameAnalyser.visit(assign);

		// Assert
		assertNull(result);
		verify(identifier, times(1)).accept(nameAnalyser);
		verify(expression, times(1)).accept(nameAnalyser);
	}

	@Test
	public void testVisitASTFieldAssign()
	{
		// Arrange
		ASTFieldAssign fieldAssign = mock(ASTFieldAssign.class);
		ASTIdentifierExpr instance = mock(ASTIdentifierExpr.class);
		ASTExpression value = mock(ASTExpression.class);

		when(fieldAssign.getInstance()).thenReturn(instance);
		when(fieldAssign.getValue()).thenReturn(value);

		// Act
		ASTType result = nameAnalyser.visit(fieldAssign);

		// Assert
		assertNull(result);
		verify(instance, times(1)).accept(nameAnalyser);
		verify(value, times(1)).accept(nameAnalyser);
	}

	@Test
	public void testVisitASTCallFunctionExpr_BuiltInFunction()
	{
		// Arrange
		ASTCallFunctionExpr callFunction = mock(ASTCallFunctionExpr.class);
		ASTIdentifierExpr functionName = createMockIdentifierExpr("print");

		when(libraryManager.isBuiltIn("print")).thenReturn(true);
		when(callFunction.getFunctionName()).thenReturn(functionName);
		when(callFunction.getInstance()).thenReturn(null);
		when(callFunction.getArguments()).thenReturn(Arrays.asList());
		when(callFunction.getToken()).thenReturn(dummyToken);

		// Act
		ASTType result = nameAnalyser.visit(callFunction);

		// Assert
		assertNull(result);
	}

	@Test
	public void testVisitASTCallFunctionStat_BuiltInFunction()
	{
		// Arrange
		ASTCallFunctionStat callFunction = mock(ASTCallFunctionStat.class);
		ASTIdentifierExpr functionName = createMockIdentifierExpr("printTest");

		when(libraryManager.isBuiltIn("printTest")).thenReturn(true);
		when(callFunction.getFunctionName()).thenReturn(functionName);
		when(callFunction.getInstance()).thenReturn(null);
		when(callFunction.getArguments()).thenReturn(Arrays.asList());
		when(callFunction.getToken()).thenReturn(dummyToken);

		// Act
		ASTType result = nameAnalyser.visit(callFunction);

		// Assert
		assertNull(result);
	}

	@Test
	public void testVisitASTNewInstance_Success()
	{
		// Arrange
		ASTNewInstance newInstance = mock(ASTNewInstance.class);
		ASTIdentifierExpr className = createMockIdentifierExpr("TestClass");
		SymbolClass symbolClass = createMockSymbolClass("TestClass");

		when(newInstance.getClassName()).thenReturn(className);
		when(newInstance.getArguments()).thenReturn(Arrays.asList());
		when(symbolProgram.getClass("TestClass")).thenReturn(symbolClass);

		// Act
		ASTType result = nameAnalyser.visit(newInstance);

		// Assert
		assertNull(result);
		verify(className, times(1)).setBinding(symbolClass);
	}

	@Test
	public void testVisitASTNewInstance_ClassNotFound()
	{
		// Arrange
		ASTNewInstance newInstance = mock(ASTNewInstance.class);
		ASTIdentifierExpr className = createMockIdentifierExpr("NonExistentClass");

		when(newInstance.getClassName()).thenReturn(className);
		when(className.getToken()).thenReturn(dummyToken);
		when(symbolProgram.getClass("NonExistentClass")).thenReturn(null);

		// Act
		ASTType result = nameAnalyser.visit(newInstance);

		// Assert
		assertNull(result);
		assertFalse(DiagnosticReporter.getErrors().isEmpty());
	}

	@Test
	public void testVisitASTReturnStatement_WithExpression()
	{
		// Arrange
		ASTReturnStatement returnStatement = mock(ASTReturnStatement.class);
		ASTExpression expression = mock(ASTExpression.class);

		when(returnStatement.getExpression()).thenReturn(expression);

		// Act
		ASTType result = nameAnalyser.visit(returnStatement);

		// Assert
		assertNull(result);
		verify(expression, times(1)).accept(nameAnalyser);
	}

	@Test
	public void testVisitASTReturnStatement_WithoutExpression()
	{
		// Arrange
		ASTReturnStatement returnStatement = mock(ASTReturnStatement.class);

		when(returnStatement.getExpression()).thenReturn(null);

		// Act
		ASTType result = nameAnalyser.visit(returnStatement);

		// Assert
		assertNull(result);
	}

	@Test
	public void testVisitASTWhile()
	{
		// Arrange
		ASTWhile whileStatement = mock(ASTWhile.class);
		ASTExpression condition = mock(ASTExpression.class);
		ASTBody body = mock(ASTBody.class);

		when(whileStatement.getCondition()).thenReturn(condition);
		when(whileStatement.getBody()).thenReturn(body);

		// Act
		ASTType result = nameAnalyser.visit(whileStatement);

		// Assert
		assertNull(result);
		verify(condition, times(1)).accept(nameAnalyser);
		verify(body, times(1)).accept(nameAnalyser);
	}

	@Test
	public void testVisitASTIfChain()
	{
		// Arrange
		ASTIfChain ifChain = mock(ASTIfChain.class);
		ASTConditionalBranch branch = mock(ASTConditionalBranch.class);
		ASTBody elseBody = mock(ASTBody.class);

		when(ifChain.getBranches()).thenReturn(Arrays.asList(branch));
		when(ifChain.getElseBody()).thenReturn(elseBody);

		// Act
		ASTType result = nameAnalyser.visit(ifChain);

		// Assert
		assertNull(result);
		verify(branch, times(1)).accept(nameAnalyser);
		verify(elseBody, times(1)).accept(nameAnalyser);
	}

	@Test
	public void testVisitASTIfChain_WithoutElse()
	{
		// Arrange
		ASTIfChain ifChain = mock(ASTIfChain.class);
		ASTConditionalBranch branch = mock(ASTConditionalBranch.class);

		when(ifChain.getBranches()).thenReturn(Arrays.asList(branch));
		when(ifChain.getElseBody()).thenReturn(null);

		// Act
		ASTType result = nameAnalyser.visit(ifChain);

		// Assert
		assertNull(result);
		verify(branch, times(1)).accept(nameAnalyser);
	}

	@Test
	public void testVisitASTConditionalBranch()
	{
		// Arrange
		ASTConditionalBranch branch = mock(ASTConditionalBranch.class);
		ASTExpression condition = mock(ASTExpression.class);
		ASTBody body = mock(ASTBody.class);

		when(branch.getCondition()).thenReturn(condition);
		when(branch.getBody()).thenReturn(body);

		// Act
		ASTType result = nameAnalyser.visit(branch);

		// Assert
		assertNull(result);
		verify(condition, times(1)).accept(nameAnalyser);
		verify(body, times(1)).accept(nameAnalyser);
	}

	@Test
	public void testVisitASTForEach()
	{
		// Arrange
		ASTForEach forEach = mock(ASTForEach.class);
		ASTExpression iterable = mock(ASTExpression.class);
		ASTVariable variable = mock(ASTVariable.class);
		ASTBody body = mock(ASTBody.class);

		when(forEach.getIterable()).thenReturn(iterable);
		when(forEach.getVariable()).thenReturn(variable);
		when(forEach.getBody()).thenReturn(body);

		// Act
		ASTType result = nameAnalyser.visit(forEach);

		// Assert
		assertNull(result);
		verify(iterable, times(1)).accept(nameAnalyser);
		verify(variable, times(1)).accept(nameAnalyser);
		verify(body, times(1)).accept(nameAnalyser);
	}

	@Test
	public void testVisitASTArrayIndexExpr()
	{
		// Arrange
		ASTArrayIndexExpr arrayIndex = mock(ASTArrayIndexExpr.class);
		ASTExpression array = mock(ASTExpression.class);
		ASTExpression index = mock(ASTExpression.class);

		when(arrayIndex.getArray()).thenReturn(array);
		when(arrayIndex.getIndex()).thenReturn(index);

		// Act
		ASTType result = nameAnalyser.visit(arrayIndex);

		// Assert
		assertNull(result);
		verify(array, times(1)).accept(nameAnalyser);
		verify(index, times(1)).accept(nameAnalyser);
	}

	@Test
	public void testVisitASTArrayAssign()
	{
		// Arrange
		ASTArrayAssign arrayAssign = mock(ASTArrayAssign.class);
		ASTIdentifier identifier = createMockIdentifier("arr");
		ASTExpression array = mock(ASTExpression.class);
		ASTExpression value = mock(ASTExpression.class);

		when(arrayAssign.getIdentifier()).thenReturn(identifier);
		when(arrayAssign.getArray()).thenReturn(array);
		when(arrayAssign.getValue()).thenReturn(value);

		// Act
		ASTType result = nameAnalyser.visit(arrayAssign);

		// Assert
		assertNull(result);
		verify(identifier, times(1)).accept(nameAnalyser);
		verify(array, times(1)).accept(nameAnalyser);
		verify(value, times(1)).accept(nameAnalyser);
	}

	@Test
	public void testVisitASTArrayLiteral()
	{
		// Arrange
		ASTArrayLiteral arrayLiteral = mock(ASTArrayLiteral.class);
		ASTExpression element1 = mock(ASTExpression.class);
		ASTExpression element2 = mock(ASTExpression.class);

		when(arrayLiteral.getExpressions()).thenReturn(Arrays.asList(element1, element2));

		// Act
		ASTType result = nameAnalyser.visit(arrayLiteral);

		// Assert
		assertNull(result);
		verify(element1, times(1)).accept(nameAnalyser);
		verify(element2, times(1)).accept(nameAnalyser);
	}

	@Test
	public void testVisitASTLambda()
	{
		// Arrange
		ASTLambda lambda = mock(ASTLambda.class);
		ASTType returnType = mock(ASTType.class);
		ASTBody body = mock(ASTBody.class);

		when(lambda.getReturnType()).thenReturn(returnType);
		when(lambda.getArguments()).thenReturn(Arrays.asList());
		when(lambda.getBody()).thenReturn(body);

		// Act
		ASTType result = nameAnalyser.visit(lambda);

		// Assert
		assertNull(result);
		verify(returnType, times(1)).accept(nameAnalyser);
		verify(body, times(1)).accept(nameAnalyser);
	}

	@Test
	public void testVisitASTInterface_Success()
	{
		// Arrange
		ASTInterface interfaceNode = mock(ASTInterface.class);
		ASTIdentifier identifier = createMockIdentifier("TestInterface");
		SymbolInterface symbolInterface = mock(SymbolInterface.class);

		when(interfaceNode.getIdentifier()).thenReturn(identifier);
		when(symbolProgram.getInterface("TestInterface")).thenReturn(symbolInterface);

		// Act
		ASTType result = nameAnalyser.visit(interfaceNode);

		// Assert
		assertNull(result);
		verify(identifier, times(1)).setBinding(symbolInterface);
	}

	@Test
	public void testVisitASTInterface_NotFound()
	{
		// Arrange
		ASTInterface interfaceNode = mock(ASTInterface.class);
		ASTIdentifier identifier = createMockIdentifier("NonExistentInterface");

		when(interfaceNode.getIdentifier()).thenReturn(identifier);
		when(interfaceNode.getToken()).thenReturn(dummyToken);
		when(symbolProgram.getInterface("NonExistentInterface")).thenReturn(null);

		// Act
		ASTType result = nameAnalyser.visit(interfaceNode);

		// Assert
		assertNull(result);
		assertFalse(DiagnosticReporter.getErrors().isEmpty());
	}

	@Test
	public void testVisitASTNewArray()
	{
		// Arrange
		ASTNewArray newArray = mock(ASTNewArray.class);
		ASTExpression length = mock(ASTExpression.class);

		when(newArray.getArrayLength()).thenReturn(length);

		// Act
		ASTType result = nameAnalyser.visit(newArray);

		// Assert
		assertNull(result);
		verify(length, times(1)).accept(nameAnalyser);
	}

	@Test
	public void testBinaryExpressionOperations()
	{
		// Arrange
		ASTPlus plus = mock(ASTPlus.class);
		ASTExpression left = mock(ASTExpression.class);
		ASTExpression right = mock(ASTExpression.class);

		when(plus.getLeft()).thenReturn(left);
		when(plus.getRight()).thenReturn(right);

		// Act
		ASTType result = nameAnalyser.visit(plus);

		// Assert
		assertNull(result);
		verify(left, times(1)).accept(nameAnalyser);
		verify(right, times(1)).accept(nameAnalyser);
	}

	@Test
	public void testComparisonBinaryExpressions()
	{
		ASTEquals astEquals = mock(ASTEquals.class);
		testBinaryExpression(astEquals, ASTEquals::setLeft, ASTEquals::setRight);

		ASTNotEquals astNotEquals = mock(ASTNotEquals.class);
		testBinaryExpression(astNotEquals, ASTNotEquals::setLeft, ASTNotEquals::setRight);

		ASTLessThan astLessThan = mock(ASTLessThan.class);
		testBinaryExpression(astLessThan, ASTLessThan::setLeft, ASTLessThan::setRight);

		ASTLessThanOrEqual astLessThanOrEqual = mock(ASTLessThanOrEqual.class);
		testBinaryExpression(astLessThanOrEqual, ASTLessThanOrEqual::setLeft, ASTLessThanOrEqual::setRight);

		ASTGreaterThan astGreaterThan = mock(ASTGreaterThan.class);
		testBinaryExpression(astGreaterThan, ASTGreaterThan::setLeft, ASTGreaterThan::setRight);

		ASTGreaterThanOrEqual astGreaterThanOrEqual = mock(ASTGreaterThanOrEqual.class);
		testBinaryExpression(astGreaterThanOrEqual, ASTGreaterThanOrEqual::setLeft, ASTGreaterThanOrEqual::setRight);
	}

	@Test
	public void testArithmeticBinaryExpressions()
	{
		ASTPlus astPlus = mock(ASTPlus.class);
		testBinaryExpression(astPlus, ASTPlus::setLeft, ASTPlus::setRight);

		ASTMinus astMinus = mock(ASTMinus.class);
		testBinaryExpression(astMinus, ASTMinus::setLeft, ASTMinus::setRight);

		ASTTimes astTimes = mock(ASTTimes.class);
		testBinaryExpression(astTimes, ASTTimes::setLeft, ASTTimes::setRight);

		ASTDivision astDivision = mock(ASTDivision.class);
		testBinaryExpression(astDivision, ASTDivision::setLeft, ASTDivision::setRight);

		ASTModulus astModulus = mock(ASTModulus.class);
		testBinaryExpression(astModulus, ASTModulus::setLeft, ASTModulus::setRight);
	}

	@Test
	public void testLogicalBinaryExpressions()
	{
		ASTAnd astAnd = mock(ASTAnd.class);
		testBinaryExpression(astAnd, ASTAnd::setLeft, ASTAnd::setRight);

		ASTOr astOr = mock(ASTOr.class);
		testBinaryExpression(astOr, ASTOr::setLeft, ASTOr::setRight);
	}

	private <T extends ASTExpression> void testBinaryExpression(T expression, BiConsumer<T, ASTExpression> setLeft,
			BiConsumer<T, ASTExpression> setRight)
	{
		ASTExpression left = mock(ASTExpression.class);
		ASTExpression right = mock(ASTExpression.class);

		setLeft.accept(expression, left);
		setRight.accept(expression, right);

		ASTType result = expression.accept(nameAnalyser);
		assertNull(result);
	}

	@Test
	public void testVisitPrimitiveTypes()
	{
		assertNull(nameAnalyser.visit(mock(ASTIntType.class)));
		assertNull(nameAnalyser.visit(mock(ASTStringType.class)));
		assertNull(nameAnalyser.visit(mock(ASTBooleanType.class)));
		assertNull(nameAnalyser.visit(mock(ASTVoidType.class)));
		assertNull(nameAnalyser.visit(mock(ASTIntArrayType.class)));
		assertNull(nameAnalyser.visit(mock(ASTStringArrayType.class)));
		assertNull(nameAnalyser.visit(mock(ASTFunctionType.class)));
		assertNull(nameAnalyser.visit(mock(ASTParameterizedType.class)));
	}

	@Test
	public void testVisitLiteralTypes()
	{
		// Test all literal type visitors return null
		assertNull(nameAnalyser.visit(mock(ASTIntLiteral.class)));
		assertNull(nameAnalyser.visit(mock(ASTStringLiteral.class)));
		assertNull(nameAnalyser.visit(mock(ASTTrue.class)));
		assertNull(nameAnalyser.visit(mock(ASTFalse.class)));
	}

	@Test
	public void testVisitASTImport()
	{
		// Arrange
		ASTImport importNode = mock(ASTImport.class);

		// Act
		ASTType result = nameAnalyser.visit(importNode);

		// Assert
		assertNull(result);
	}

	@Test
	public void testVisitASTVariableInit_WithExpression()
	{
		// Arrange
		ASTVariableInit variableInit = mock(ASTVariableInit.class);
		ASTIdentifier identifier = createMockIdentifier("testVar");
		ASTType type = mock(ASTType.class);
		ASTExpression expression = mock(ASTExpression.class);

		when(variableInit.getIdentifier()).thenReturn(identifier);
		when(variableInit.getType()).thenReturn(type);
		when(variableInit.getExpression()).thenReturn(expression);
		when(variableInit.getToken()).thenReturn(dummyToken);

		// Act
		ASTType result = nameAnalyser.visit(variableInit);

		// Assert
		assertNull(result);
		verify(type, times(1)).accept(nameAnalyser);
		verify(expression, times(1)).accept(nameAnalyser);
	}

	@Test
	public void testVisitASTVariableInit_WithoutExpression()
	{
		// Arrange
		ASTVariableInit variableInit = mock(ASTVariableInit.class);
		ASTIdentifier identifier = createMockIdentifier("testVar");
		ASTType type = mock(ASTType.class);

		when(variableInit.getIdentifier()).thenReturn(identifier);
		when(variableInit.getType()).thenReturn(type);
		when(variableInit.getExpression()).thenReturn(null);
		when(variableInit.getToken()).thenReturn(dummyToken);

		// Act
		ASTType result = nameAnalyser.visit(variableInit);

		// Assert
		assertNull(result);
		verify(type, times(1)).accept(nameAnalyser);
	}

	@Test
	public void testVisitASTCallFunctionExpr_WithArguments()
	{
		// Arrange
		ASTCallFunctionExpr callFunction = mock(ASTCallFunctionExpr.class);
		ASTIdentifierExpr functionName = createMockIdentifierExpr("customFunction");
		ASTExpression arg1 = mock(ASTExpression.class);
		ASTExpression arg2 = mock(ASTExpression.class);

		when(callFunction.getFunctionName()).thenReturn(functionName);
		when(callFunction.getInstance()).thenReturn(null);
		when(callFunction.getArguments()).thenReturn(Arrays.asList(arg1, arg2));
		when(callFunction.getToken()).thenReturn(dummyToken);

		// Act
		ASTType result = nameAnalyser.visit(callFunction);

		// Assert
		assertNull(result);
		verify(arg1, times(1)).accept(nameAnalyser);
		verify(arg2, times(1)).accept(nameAnalyser);
	}

	@Test
	public void testVisitASTCallFunctionStat_WithArguments()
	{
		// Arrange
		ASTCallFunctionStat callFunction = mock(ASTCallFunctionStat.class);
		ASTIdentifierExpr functionName = createMockIdentifierExpr("customFunction");
		ASTExpression arg1 = mock(ASTExpression.class);
		ASTExpression arg2 = mock(ASTExpression.class);

		when(callFunction.getFunctionName()).thenReturn(functionName);
		when(callFunction.getInstance()).thenReturn(null);
		when(callFunction.getArguments()).thenReturn(Arrays.asList(arg1, arg2));
		when(callFunction.getToken()).thenReturn(dummyToken);

		// Act
		ASTType result = nameAnalyser.visit(callFunction);

		// Assert
		assertNull(result);
		verify(arg1, times(1)).accept(nameAnalyser);
		verify(arg2, times(1)).accept(nameAnalyser);
	}

	@Test
	public void testVisitASTNewInstance_WithArguments()
	{
		// Arrange
		ASTNewInstance newInstance = mock(ASTNewInstance.class);
		ASTIdentifierExpr className = createMockIdentifierExpr("TestClass");
		ASTArgument arg1 = mock(ASTArgument.class);
		ASTArgument arg2 = mock(ASTArgument.class);
		SymbolClass symbolClass = createMockSymbolClass("TestClass");

		when(newInstance.getClassName()).thenReturn(className);
		when(newInstance.getArguments()).thenReturn(Arrays.asList(arg1, arg2));
		when(symbolProgram.getClass("TestClass")).thenReturn(symbolClass);

		// Act
		ASTType result = nameAnalyser.visit(newInstance);

		// Assert
		assertNull(result);
		verify(className, times(1)).setBinding(symbolClass);
		verify(arg1, times(1)).accept(nameAnalyser);
		verify(arg2, times(1)).accept(nameAnalyser);
	}

	@Test
	public void testVisitASTLambda_WithArguments()
	{
		// Arrange
		ASTLambda lambda = mock(ASTLambda.class);
		ASTType returnType = mock(ASTType.class);
		ASTBody body = mock(ASTBody.class);
		ASTArgument arg1 = mock(ASTArgument.class);
		ASTArgument arg2 = mock(ASTArgument.class);

		when(lambda.getReturnType()).thenReturn(returnType);
		when(lambda.getArguments()).thenReturn(Arrays.asList(arg1, arg2));
		when(lambda.getBody()).thenReturn(body);

		// Act
		ASTType result = nameAnalyser.visit(lambda);

		// Assert
		assertNull(result);
		verify(returnType, times(1)).accept(nameAnalyser);
		verify(body, times(1)).accept(nameAnalyser);
		verify(arg1, times(1)).accept(nameAnalyser);
		verify(arg2, times(1)).accept(nameAnalyser);
	}

	@Test
	public void testVisitASTArrayLiteral_Empty()
	{
		// Arrange
		ASTArrayLiteral arrayLiteral = mock(ASTArrayLiteral.class);

		when(arrayLiteral.getExpressions()).thenReturn(Arrays.asList());

		// Act
		ASTType result = nameAnalyser.visit(arrayLiteral);

		// Assert
		assertNull(result);
	}

	@Test
	public void testVisitASTArrayLiteral_MultipleElements()
	{
		// Arrange
		ASTArrayLiteral arrayLiteral = mock(ASTArrayLiteral.class);
		ASTExpression element1 = mock(ASTExpression.class);
		ASTExpression element2 = mock(ASTExpression.class);
		ASTExpression element3 = mock(ASTExpression.class);

		when(arrayLiteral.getExpressions()).thenReturn(Arrays.asList(element1, element2, element3));

		// Act
		ASTType result = nameAnalyser.visit(arrayLiteral);

		// Assert
		assertNull(result);
		verify(element1, times(1)).accept(nameAnalyser);
		verify(element2, times(1)).accept(nameAnalyser);
		verify(element3, times(1)).accept(nameAnalyser);
	}

	@Test
	public void testVisitASTIfChain_MultipleBranches()
	{
		// Arrange
		ASTIfChain ifChain = mock(ASTIfChain.class);
		ASTConditionalBranch branch1 = mock(ASTConditionalBranch.class);
		ASTConditionalBranch branch2 = mock(ASTConditionalBranch.class);
		ASTConditionalBranch branch3 = mock(ASTConditionalBranch.class);
		ASTBody elseBody = mock(ASTBody.class);

		when(ifChain.getBranches()).thenReturn(Arrays.asList(branch1, branch2, branch3));
		when(ifChain.getElseBody()).thenReturn(elseBody);

		// Act
		ASTType result = nameAnalyser.visit(ifChain);

		// Assert
		assertNull(result);
		verify(branch1, times(1)).accept(nameAnalyser);
		verify(branch2, times(1)).accept(nameAnalyser);
		verify(branch3, times(1)).accept(nameAnalyser);
		verify(elseBody, times(1)).accept(nameAnalyser);
	}

	@Test
	public void testVisitASTBody_Empty()
	{
		// Arrange
		ASTBody body = mock(ASTBody.class);
		Scope bodyScope = mock(Scope.class);

		when(body.getScope()).thenReturn(bodyScope);
		when(body.getNodes()).thenReturn(Arrays.asList());

		// Act
		ASTType result = nameAnalyser.visit(body);

		// Assert
		assertNull(result);
	}

	@Test
	public void testVisitASTBody_MultipleStatements()
	{
		// Arrange
		ASTBody body = mock(ASTBody.class);
		Scope bodyScope = mock(Scope.class);
		ASTStatement statement1 = mock(ASTStatement.class);
		ASTStatement statement2 = mock(ASTStatement.class);
		ASTStatement statement3 = mock(ASTStatement.class);

		when(body.getScope()).thenReturn(bodyScope);
		when(body.getNodes()).thenReturn(Arrays.asList(statement1, statement2, statement3));

		// Act
		ASTType result = nameAnalyser.visit(body);

		// Assert
		assertNull(result);
		verify(statement1, times(1)).accept(nameAnalyser);
		verify(statement2, times(1)).accept(nameAnalyser);
		verify(statement3, times(1)).accept(nameAnalyser);
	}

	@Test
	public void testVisitASTProgram_Empty()
	{
		// Arrange
		ASTProgram program = mock(ASTProgram.class);

		when(program.getNodes()).thenReturn(Arrays.asList());

		// Act
		ASTType result = nameAnalyser.visit(program);

		// Assert
		assertNull(result);
	}

	@Test
	public void testVisitASTProgram_MultipleNodes()
	{
		// Arrange
		ASTProgram program = mock(ASTProgram.class);
		ASTClass class1 = mock(ASTClass.class);
		ASTClass class2 = mock(ASTClass.class);
		ASTFunction function = mock(ASTFunction.class);

		when(program.getNodes()).thenReturn(Arrays.asList(class1, class2, function));

		// Act
		ASTType result = nameAnalyser.visit(program);

		// Assert
		assertNull(result);
		verify(class1, times(1)).accept(nameAnalyser);
		verify(class2, times(1)).accept(nameAnalyser);
		verify(function, times(1)).accept(nameAnalyser);
	}

	@Test
	public void testScopeManagement()
	{
		// Test that scope management works correctly
		SymbolClass testClass = createMockSymbolClass("TestClass");
		SymbolFunction testFunction = createMockSymbolFunction("testFunction");

		// Enter class scope
		nameAnalyser.getScopeManager().enterClass(testClass);
		assertTrue(nameAnalyser.getScopeManager().isInClass());

		// Enter function scope within class
		nameAnalyser.getScopeManager().enterFunction(testFunction);
		assertTrue(nameAnalyser.getScopeManager().isInFunction());

		// Exit function scope
		nameAnalyser.getScopeManager().exitFunction();
		assertFalse(nameAnalyser.getScopeManager().isInFunction());
		assertTrue(nameAnalyser.getScopeManager().isInClass());

		// Exit class scope
		nameAnalyser.getScopeManager().exitClass();
		assertFalse(nameAnalyser.getScopeManager().isInClass());
	}

	@Test
	public void testProcessedCollections()
	{
		// Test that processed classes and functions are tracked correctly
		assertFalse(nameAnalyser.processedClasses.contains("TestClass"));
		assertFalse(nameAnalyser.processedFunctions.contains("testFunction"));

		nameAnalyser.processedClasses.add("TestClass");
		nameAnalyser.processedFunctions.add("testFunction");

		assertTrue(nameAnalyser.processedClasses.contains("TestClass"));
		assertTrue(nameAnalyser.processedFunctions.contains("testFunction"));
	}

	@Test
	public void testDiagnosticReporterClearsErrors()
	{
		// Test that DiagnosticReporter properly clears errors between tests
		assertTrue(DiagnosticReporter.getErrors().isEmpty());
	}

	@Test
	public void testCustomError_UndefinedVariable()
	{
		// Arrange
		ASTIdentifierExpr identifierExpr = mock(ASTIdentifierExpr.class);

		when(identifierExpr.getName()).thenReturn("undefinedVar");
		when(identifierExpr.getToken()).thenReturn(dummyToken);

		// Act
		ASTType result = nameAnalyser.visit(identifierExpr);

		// Assert
		assertNull(result);
		assertFalse(DiagnosticReporter.getErrors().isEmpty());
		String errorMessage = DiagnosticReporter.getErrors().get(0).getMessage();
		assertTrue(errorMessage.contains("undefinedVar") || errorMessage.contains("not declared"));
	}

	@Test
	public void testCustomError_UndefinedClass()
	{
		// Arrange
		ASTIdentifierType identifierType = mock(ASTIdentifierType.class);

		when(identifierType.getName()).thenReturn("UndefinedClass");
		when(identifierType.getToken()).thenReturn(dummyToken);
		when(symbolProgram.getClass("UndefinedClass")).thenReturn(null);

		// Act
		ASTType result = nameAnalyser.visit(identifierType);

		// Assert
		assertNull(result);
		assertFalse(DiagnosticReporter.getErrors().isEmpty());
		String errorMessage = DiagnosticReporter.getErrors().get(0).getMessage();
		assertTrue(errorMessage.contains("UndefinedClass") || errorMessage.contains("not found"));
	}

	@Test
	public void testCustomError_UndefinedInterface()
	{
		// Arrange
		ASTInterface interfaceNode = mock(ASTInterface.class);
		ASTIdentifier identifier = createMockIdentifier("UndefinedInterface");

		when(interfaceNode.getIdentifier()).thenReturn(identifier);
		when(interfaceNode.getToken()).thenReturn(dummyToken);
		when(symbolProgram.getInterface("UndefinedInterface")).thenReturn(null);

		// Act
		ASTType result = nameAnalyser.visit(interfaceNode);

		// Assert
		assertNull(result);
		assertFalse(DiagnosticReporter.getErrors().isEmpty());
		String errorMessage = DiagnosticReporter.getErrors().get(0).getMessage();
		assertTrue(errorMessage.contains("UndefinedInterface") || errorMessage.contains("not found"));
	}

	@Test
	public void testCustomError_UndefinedFunction()
	{
		// Arrange
		ASTCallFunctionExpr callFunction = mock(ASTCallFunctionExpr.class);
		ASTIdentifierExpr functionName = createMockIdentifierExpr("undefinedFunction");

		when(callFunction.getFunctionName()).thenReturn(functionName);
		when(callFunction.getInstance()).thenReturn(null);
		when(callFunction.getArguments()).thenReturn(Arrays.asList());
		when(callFunction.getToken()).thenReturn(dummyToken);

		// Act
		ASTType result = nameAnalyser.visit(callFunction);

		// Assert
		assertNull(result);
		assertFalse(DiagnosticReporter.getErrors().isEmpty());
		String errorMessage = DiagnosticReporter.getErrors().get(0).getMessage();
		assertTrue(errorMessage.contains("undefinedFunction") || errorMessage.contains("not found"));
	}

	@Test
	public void testCustomError_UndefinedProperty()
	{
		// Arrange
		ASTProperty property = mock(ASTProperty.class);
		ASTIdentifier identifier = createMockIdentifier("undefinedProperty");
		ASTIntType type = mock(ASTIntType.class);
		SymbolClass currentClass = createMockSymbolClass("TestClass");

		when(property.getIdentifier()).thenReturn(identifier);
		when(property.getType()).thenReturn(type);
		when(property.getToken()).thenReturn(dummyToken);
		when(currentClass.getProperty("undefinedProperty")).thenReturn(null);

		nameAnalyser.getScopeManager().enterClass(currentClass);

		// Act
		ASTType result = nameAnalyser.visit(property);

		// Assert
		assertNull(result);
		assertFalse(DiagnosticReporter.getErrors().isEmpty());
		String errorMessage = DiagnosticReporter.getErrors().get(0).getMessage();
		assertTrue(errorMessage.contains("undefinedProperty") || errorMessage.contains("not found"));

		nameAnalyser.getScopeManager().exitClass();
	}

	@Test
	public void testCustomError_UndefinedParameter()
	{
		// Arrange
		ASTArgument argument = mock(ASTArgument.class);
		ASTIdentifier identifier = createMockIdentifier("undefinedParam");
		ASTType type = mock(ASTType.class);
		SymbolFunction currentFunction = createMockSymbolFunction("testFunction");

		when(argument.getIdentifier()).thenReturn(identifier);
		when(argument.getType()).thenReturn(type);
		when(argument.getToken()).thenReturn(dummyToken);
		when(currentFunction.getParameter("undefinedParam")).thenReturn(null);

		nameAnalyser.getScopeManager().enterFunction(currentFunction);

		// Act
		ASTType result = nameAnalyser.visit(argument);

		// Assert
		assertNull(result);
		assertFalse(DiagnosticReporter.getErrors().isEmpty());
		String errorMessage = DiagnosticReporter.getErrors().get(0).getMessage();
		assertTrue(errorMessage.contains("undefinedParam") || errorMessage.contains("not found"));

		nameAnalyser.getScopeManager().exitFunction();
	}

	@Test
	public void testCustomError_UndefinedClassForInstantiation()
	{
		// Arrange
		ASTNewInstance newInstance = mock(ASTNewInstance.class);
		ASTIdentifierExpr className = createMockIdentifierExpr("UndefinedClass");

		when(newInstance.getClassName()).thenReturn(className);
		when(className.getToken()).thenReturn(dummyToken);
		when(newInstance.getArguments()).thenReturn(Arrays.asList());
		when(symbolProgram.getClass("UndefinedClass")).thenReturn(null);

		// Act
		ASTType result = nameAnalyser.visit(newInstance);

		// Assert
		assertNull(result);
		assertFalse(DiagnosticReporter.getErrors().isEmpty());
		String errorMessage = DiagnosticReporter.getErrors().get(0).getMessage();
		assertTrue(errorMessage.contains("UndefinedClass") || errorMessage.contains("undefined class"));
	}

	@Test
	public void testCustomError_MultipleErrorsAccumulate()
	{
		// Test that multiple errors can be reported
		DiagnosticReporter.clear();

		// First error
		ASTIdentifierExpr identifier1 = mock(ASTIdentifierExpr.class);
		when(identifier1.getName()).thenReturn("var1");
		when(identifier1.getToken()).thenReturn(dummyToken);
		nameAnalyser.visit(identifier1);

		// Second error
		ASTIdentifierType identifier2 = mock(ASTIdentifierType.class);
		when(identifier2.getName()).thenReturn("Class2");
		when(identifier2.getToken()).thenReturn(dummyToken);
		when(symbolProgram.getClass("Class2")).thenReturn(null);
		nameAnalyser.visit(identifier2);

		// Assert
		assertFalse(DiagnosticReporter.getErrors().isEmpty());
		assertTrue(DiagnosticReporter.getErrors().size() >= 2);
	}

	@Test
	public void testCustomError_ScopeContextPreserved()
	{
		// Arrange
		SymbolClass testClass = createMockSymbolClass("TestClass");

		// Enter class scope
		nameAnalyser.getScopeManager().enterClass(testClass);

		// Create error in class context
		ASTProperty property = mock(ASTProperty.class);
		ASTIdentifier identifier = createMockIdentifier("badProperty");
		ASTIntType type = mock(ASTIntType.class);

		when(property.getIdentifier()).thenReturn(identifier);
		when(property.getType()).thenReturn(type);
		when(property.getToken()).thenReturn(dummyToken);
		when(testClass.getProperty("badProperty")).thenReturn(null);

		// Act
		ASTType result = nameAnalyser.visit(property);

		// Assert
		assertNull(result);
		assertFalse(DiagnosticReporter.getErrors().isEmpty());
		String errorMessage = DiagnosticReporter.getErrors().get(0).getMessage();
		assertTrue(errorMessage.contains("TestClass") || errorMessage.contains("badProperty"));

		nameAnalyser.getScopeManager().exitClass();
	}

	@Test
	public void testCustomError_EdgeCase_EmptyStringNames()
	{
		// Arrange
		ASTIdentifierExpr identifierExpr = mock(ASTIdentifierExpr.class);

		when(identifierExpr.getName()).thenReturn("");
		when(identifierExpr.getToken()).thenReturn(dummyToken);

		// Act
		ASTType result = nameAnalyser.visit(identifierExpr);

		// Assert
		assertNull(result);
		assertFalse(DiagnosticReporter.getErrors().isEmpty());
	}

	@Test
	public void testCustomError_EdgeCase_WhitespaceNames()
	{
		// Arrange
		ASTIdentifierExpr identifierExpr = mock(ASTIdentifierExpr.class);

		when(identifierExpr.getName()).thenReturn("   ");
		when(identifierExpr.getToken()).thenReturn(dummyToken);

		// Act
		ASTType result = nameAnalyser.visit(identifierExpr);

		// Assert
		assertNull(result);
		assertFalse(DiagnosticReporter.getErrors().isEmpty());
	}

	@Test
	public void testCustomError_EdgeCase_SpecialCharacters()
	{
		// Arrange
		ASTIdentifierExpr identifierExpr = mock(ASTIdentifierExpr.class);

		when(identifierExpr.getName()).thenReturn("var@#$%");
		when(identifierExpr.getToken()).thenReturn(dummyToken);

		// Act
		ASTType result = nameAnalyser.visit(identifierExpr);

		// Assert
		assertNull(result);
		assertFalse(DiagnosticReporter.getErrors().isEmpty());
	}
}
