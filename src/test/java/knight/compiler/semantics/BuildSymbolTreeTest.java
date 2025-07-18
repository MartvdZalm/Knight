package knight.compiler.semantics;

import knight.compiler.ast.*;
import knight.compiler.ast.types.*;
import knight.compiler.lexer.Token;
import knight.compiler.semantics.diagnostics.SemanticErrors;
import knight.compiler.semantics.model.SymbolClass;
import knight.compiler.semantics.model.SymbolFunction;
import knight.compiler.semantics.model.SymbolProgram;
import knight.lib.Library;
import knight.lib.LibraryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BuildSymbolTreeTest
{
	private BuildSymbolTree symbolTree;
	private Token token;

	@BeforeEach
	public void setUp()
	{
		SemanticErrors.clearErrors();
		this.symbolTree = new BuildSymbolTree();
		this.token = new Token(null, 0, 0);
	}

	@Test
	public void visitASTProgram_withEmptyProgram()
	{
		ASTProgram program = mock(ASTProgram.class);
		when(program.getImportList()).thenReturn(Collections.emptyList());
		when(program.getNodeList()).thenReturn(Collections.emptyList());

		assertNull(symbolTree.visit(program));
		verify(program).getImportList();
		verify(program).getNodeList();
	}

	@Test
	public void visitASTClass_withNewClass()
	{
		ASTClass astClass = mock(ASTClass.class);
		when(astClass.getClassName()).thenReturn(new ASTIdentifier(mock(Token.class), "TestClass"));
		when(astClass.getPropertyList()).thenReturn(Collections.emptyList());
		when(astClass.getFunctionList()).thenReturn(Collections.emptyList());

		assertNull(symbolTree.visit(astClass));
	}

	@Test
	public void visitASTClass_withDuplicateClass()
	{
		ASTClass astClass = mock(ASTClass.class);
		when(astClass.getClassName()).thenReturn(new ASTIdentifier(token, "TestClass"));
		when(astClass.getPropertyList()).thenReturn(Collections.emptyList());
		when(astClass.getFunctionList()).thenReturn(Collections.emptyList());
		when(astClass.getToken()).thenReturn(token);

		symbolTree.visit(astClass);
		symbolTree.visit(astClass);

		assertTrue(SemanticErrors.hasErrors());
	}

	@Test
	public void visitASTProperty_withValidProperty()
	{
		SymbolClass mockSymbolClass = mock(SymbolClass.class);
		when(mockSymbolClass.getId()).thenReturn("TestClass");
		when(mockSymbolClass.addVariable(anyString(), any())).thenReturn(true);
		symbolTree.setSymbolClass(mockSymbolClass);

		ASTProperty property = mock(ASTProperty.class);
		when(property.getType()).thenReturn(new ASTIntType(mock(Token.class)));
		when(property.getId()).thenReturn(new ASTIdentifier(mock(Token.class), "prop"));

		assertNull(symbolTree.visit(property));
		verify(mockSymbolClass).addVariable(anyString(), any());
	}

	@Test
	public void visitASTProperty_withDuplicateProperty()
	{
		SymbolClass mockSymbolClass = mock(SymbolClass.class);
		when(mockSymbolClass.getId()).thenReturn("TestClass");
		when(mockSymbolClass.addVariable(anyString(), any())).thenReturn(false);
		symbolTree.setSymbolClass(mockSymbolClass);

		ASTProperty property = mock(ASTProperty.class);
		when(property.getType()).thenReturn(new ASTIntType(mock(Token.class)));
		when(property.getId()).thenReturn(new ASTIdentifier(mock(Token.class), "prop"));

		symbolTree.visit(property);
		assertTrue(SemanticErrors.hasErrors());
	}

	@Test
	public void visitASTProperty_withPropertyOutsideClass()
	{
		ASTProperty astProperty = mock(ASTProperty.class);
		when(astProperty.getToken()).thenReturn(token);
		assertNull(symbolTree.visit(astProperty));
		assertTrue(SemanticErrors.hasErrors());
	}

	@Test
	public void visitASTFunction_withGlobalFunction()
	{
		ASTFunction globalFunc = mock(ASTFunction.class);
		when(globalFunc.getReturnType()).thenReturn(new ASTVoidType(mock(Token.class)));
		when(globalFunc.getFunctionName()).thenReturn(new ASTIdentifier(mock(Token.class), "globalFunc"));
		when(globalFunc.getArgumentListSize()).thenReturn(0);
		when(globalFunc.getBody()).thenReturn(mock(ASTBody.class));

		assertNull(symbolTree.visit(globalFunc));
	}

	@Test
	public void visitASTFunction_withClassMethod()
	{
		ASTClass astClass = mock(ASTClass.class);
		when(astClass.getClassName()).thenReturn(new ASTIdentifier(mock(Token.class), "TestClass"));
		when(astClass.getPropertyList()).thenReturn(Collections.emptyList());
		when(astClass.getFunctionList()).thenReturn(Collections.emptyList());
		symbolTree.visit(astClass);

		ASTFunction method = mock(ASTFunction.class);
		when(method.getReturnType()).thenReturn(new ASTIntType(mock(Token.class)));
		when(method.getFunctionName()).thenReturn(new ASTIdentifier(mock(Token.class), "method"));
		when(method.getArgumentListSize()).thenReturn(0);
		when(method.getBody()).thenReturn(mock(ASTBody.class));

		assertNull(symbolTree.visit(method));
	}

	@Test
	public void visitASTAssign_withValidAssignment()
	{
		ASTAssign assign = mock(ASTAssign.class);
		when(assign.getIdentifier()).thenReturn(mock(ASTIdentifier.class));
		when(assign.getExpr()).thenReturn(mock(ASTExpression.class));

		assertNull(symbolTree.visit(assign));
	}

	@Test
	public void visitASTBody_withEmptyBody()
	{
		ASTBody body = mock(ASTBody.class);
		when(body.getNodesList()).thenReturn(Collections.emptyList());

		assertNull(symbolTree.visit(body));
	}

	@Test
	public void visitASTWhile_withValidWhileLoop()
	{
		ASTWhile whileLoop = mock(ASTWhile.class);
		when(whileLoop.getCondition()).thenReturn(mock(ASTExpression.class));
		when(whileLoop.getBody()).thenReturn(mock(ASTBody.class));

		assertNull(symbolTree.visit(whileLoop));
	}

	@Test
	public void visitASTIntLiteral_withValidLiteral()
	{
		assertNull(symbolTree.visit(mock(ASTIntLiteral.class)));
	}

	@Test
	public void visitASTTrue_withValidLiteral()
	{
		assertNull(symbolTree.visit(mock(ASTTrue.class)));
	}

	@Test
	public void visitASTFalse_withValidLiteral()
	{
		assertNull(symbolTree.visit(mock(ASTFalse.class)));
	}

	@Test
	public void visitASTStringLiteral_withValidLiteral()
	{
		assertNull(symbolTree.visit(mock(ASTStringLiteral.class)));
	}

	@Test
	public void visitASTIdentifierExpr_withUndeclaredVariable()
	{
		ASTIdentifierExpr identifierExpr = mock(ASTIdentifierExpr.class);
		when(identifierExpr.getId()).thenReturn("undeclared");
		when(identifierExpr.getToken()).thenReturn(mock(Token.class));

		assertNull(symbolTree.visit(identifierExpr));
		assertTrue(SemanticErrors.hasErrors());
	}

	@Test
	public void visitASTNewArray_withValidArray()
	{
		ASTNewArray newArray = mock(ASTNewArray.class);
		when(newArray.getArrayLength()).thenReturn(mock(ASTExpression.class));

		assertNull(symbolTree.visit(newArray));
	}

	@Test
	public void visitASTNewInstance_withValidInstance()
	{
		ASTNewInstance newInstance = mock(ASTNewInstance.class);
		when(newInstance.getClassName()).thenReturn(new ASTIdentifierExpr(mock(Token.class), "TestClass"));
		when(newInstance.getArguments()).thenReturn(Collections.emptyList());

		assertNull(symbolTree.visit(newInstance));
	}

	@Test
	public void visitASTNewInstance_withUnknownClass()
	{
		ASTNewInstance newInstance = mock(ASTNewInstance.class);
		when(newInstance.getClassName()).thenReturn(new ASTIdentifierExpr(mock(Token.class), "UnknownClass"));
		when(newInstance.getArguments()).thenReturn(Collections.emptyList());

		assertNull(symbolTree.visit(newInstance));
		assertTrue(SemanticErrors.hasErrors());
	}

	@Test
	public void visitASTCallFunctionExpr_withValidCall()
	{
		ASTCallFunctionExpr call = mock(ASTCallFunctionExpr.class);
		when(call.getArgumentList()).thenReturn(Collections.emptyList());

		assertNull(symbolTree.visit(call));
	}

	@Test
	public void visitASTCallFunctionStat_withValidCall()
	{
		ASTCallFunctionStat call = mock(ASTCallFunctionStat.class);
		when(call.getArgumentList()).thenReturn(Collections.emptyList());

		assertNull(symbolTree.visit(call));
	}

	@Test
	public void visitASTReturnStatement_withExpression()
	{
		ASTReturnStatement returnStmt = mock(ASTReturnStatement.class);
		when(returnStmt.getReturnExpr()).thenReturn(mock(ASTExpression.class));

		assertNull(symbolTree.visit(returnStmt));
	}

	@Test
	public void visitASTReturnStatement_withoutExpression()
	{
		ASTReturnStatement returnStmt = mock(ASTReturnStatement.class);
		when(returnStmt.getReturnExpr()).thenReturn(null);

		assertNull(symbolTree.visit(returnStmt));
	}

	@Test
	public void visitASTFunctionType_withValidType()
	{
		ASTFunctionType funcType = mock(ASTFunctionType.class);
		assertEquals(funcType, symbolTree.visit(funcType));
	}

	@Test
	public void visitASTIntType_withValidType()
	{
		ASTIntType intType = mock(ASTIntType.class);
		assertEquals(intType, symbolTree.visit(intType));
	}

	@Test
	public void visitASTStringType_withValidType()
	{
		ASTStringType stringType = mock(ASTStringType.class);
		assertEquals(stringType, symbolTree.visit(stringType));
	}

	@Test
	public void visitASTVoidType_withValidType()
	{
		ASTVoidType voidType = mock(ASTVoidType.class);
		assertEquals(voidType, symbolTree.visit(voidType));
	}

	@Test
	public void visitASTBooleanType_withValidType()
	{
		ASTBooleanType boolType = mock(ASTBooleanType.class);
		assertEquals(boolType, symbolTree.visit(boolType));
	}

	@Test
	public void visitASTIntArrayType_withValidType()
	{
		ASTIntArrayType arrayType = mock(ASTIntArrayType.class);
		assertEquals(arrayType, symbolTree.visit(arrayType));
	}

	@Test
	public void visitASTIdentifierType_withValidType()
	{
		ASTIdentifierType idType = mock(ASTIdentifierType.class);
		assertEquals(idType, symbolTree.visit(idType));
	}

	@Test
	public void visitASTVariable_withGlobalVariable()
	{
		ASTVariable variable = mock(ASTVariable.class);
		when(variable.getType()).thenReturn(new ASTIntType(mock(Token.class)));
		when(variable.getId()).thenReturn(new ASTIdentifier(mock(Token.class), "var"));

		assertNull(symbolTree.visit(variable));
	}

	@Test
	public void visitASTVariableInit_withInitialization()
	{
		ASTVariableInit variable = mock(ASTVariableInit.class);
		when(variable.getType()).thenReturn(new ASTIntType(mock(Token.class)));
		when(variable.getId()).thenReturn(new ASTIdentifier(mock(Token.class), "var"));

		assertNull(symbolTree.visit(variable));
	}

	@Test
	public void visitASTIdentifier_withValidIdentifier()
	{
		ASTIdentifier identifier = mock(ASTIdentifier.class);
		assertNull(symbolTree.visit(identifier));
	}

	@Test
	public void visitASTArrayIndexExpr_withValidExpression()
	{
		ASTArrayIndexExpr arrayExpr = mock(ASTArrayIndexExpr.class);
		when(arrayExpr.getArray()).thenReturn(mock(ASTExpression.class));
		when(arrayExpr.getIndex()).thenReturn(mock(ASTExpression.class));

		assertNull(symbolTree.visit(arrayExpr));
	}

	@Test
	public void visitASTArrayAssign_withValidAssignment()
	{
		ASTArrayAssign arrayAssign = mock(ASTArrayAssign.class);
		when(arrayAssign.getId()).thenReturn(mock(ASTIdentifier.class));
		when(arrayAssign.getExpression1()).thenReturn(mock(ASTExpression.class));
		when(arrayAssign.getExpression2()).thenReturn(mock(ASTExpression.class));

		assertNull(symbolTree.visit(arrayAssign));
	}

	@Test
	public void visitASTIfChain_withValidChain()
	{
		ASTIfChain ifChain = mock(ASTIfChain.class);
		when(ifChain.getBranches()).thenReturn(Collections.emptyList());
		when(ifChain.getElseBody()).thenReturn(null);

		assertNull(symbolTree.visit(ifChain));
	}

	@Test
	public void visitASTConditionalBranch_withValidBranch()
	{
		ASTConditionalBranch branch = mock(ASTConditionalBranch.class);
		when(branch.getCondition()).thenReturn(mock(ASTExpression.class));
		when(branch.getBody()).thenReturn(mock(ASTBody.class));

		assertNull(symbolTree.visit(branch));
	}

	@Test
	public void visitASTArgument_withValidArgument()
	{
		SymbolFunction mockSymbolFunction = mock(SymbolFunction.class);
		when(mockSymbolFunction.getId()).thenReturn("testFunction");
		when(mockSymbolFunction.addParam(anyString(), any())).thenReturn(true);
		symbolTree.setSymbolFunction(mockSymbolFunction);

		ASTArgument arg = mock(ASTArgument.class);
		when(arg.getType()).thenReturn(new ASTIntType(mock(Token.class)));
		when(arg.getIdentifier()).thenReturn(new ASTIdentifier(mock(Token.class), "arg"));

		assertNull(symbolTree.visit(arg));
	}

	@Test
	public void visitASTNotEquals_withValidExpression()
	{
		ASTNotEquals expr = mock(ASTNotEquals.class);
		when(expr.getLeftSide()).thenReturn(mock(ASTExpression.class));
		when(expr.getRightSide()).thenReturn(mock(ASTExpression.class));

		assertNull(symbolTree.visit(expr));
	}

	@Test
	public void visitASTPlus_withValidExpression()
	{
		ASTPlus expr = mock(ASTPlus.class);
		when(expr.getLeftSide()).thenReturn(mock(ASTExpression.class));
		when(expr.getRightSide()).thenReturn(mock(ASTExpression.class));

		assertNull(symbolTree.visit(expr));
	}

	@Test
	public void visitASTOr_withValidExpression()
	{
		ASTOr expr = mock(ASTOr.class);
		when(expr.getLeftSide()).thenReturn(mock(ASTExpression.class));
		when(expr.getRightSide()).thenReturn(mock(ASTExpression.class));

		assertNull(symbolTree.visit(expr));
	}

	@Test
	public void visitASTAnd_withValidExpression()
	{
		ASTAnd expr = mock(ASTAnd.class);
		when(expr.getLeftSide()).thenReturn(mock(ASTExpression.class));
		when(expr.getRightSide()).thenReturn(mock(ASTExpression.class));

		assertNull(symbolTree.visit(expr));
	}

	@Test
	public void visitASTEquals_withValidExpression()
	{
		ASTEquals expr = mock(ASTEquals.class);
		when(expr.getLeftSide()).thenReturn(mock(ASTExpression.class));
		when(expr.getRightSide()).thenReturn(mock(ASTExpression.class));

		assertNull(symbolTree.visit(expr));
	}

	@Test
	public void visitASTLessThan_withValidExpression()
	{
		ASTLessThan expr = mock(ASTLessThan.class);
		when(expr.getLeftSide()).thenReturn(mock(ASTExpression.class));
		when(expr.getRightSide()).thenReturn(mock(ASTExpression.class));

		assertNull(symbolTree.visit(expr));
	}

	@Test
	public void visitASTLessThanOrEqual_withValidExpression()
	{
		ASTLessThanOrEqual expr = mock(ASTLessThanOrEqual.class);
		when(expr.getLeftSide()).thenReturn(mock(ASTExpression.class));
		when(expr.getRightSide()).thenReturn(mock(ASTExpression.class));

		assertNull(symbolTree.visit(expr));
	}

	@Test
	public void visitASTGreaterThan_withValidExpression()
	{
		ASTGreaterThan expr = mock(ASTGreaterThan.class);
		when(expr.getLeftSide()).thenReturn(mock(ASTExpression.class));
		when(expr.getRightSide()).thenReturn(mock(ASTExpression.class));

		assertNull(symbolTree.visit(expr));
	}

	@Test
	public void visitASTGreaterThanOrEqual_withValidExpression()
	{
		ASTGreaterThanOrEqual expr = mock(ASTGreaterThanOrEqual.class);
		when(expr.getLeftSide()).thenReturn(mock(ASTExpression.class));
		when(expr.getRightSide()).thenReturn(mock(ASTExpression.class));

		assertNull(symbolTree.visit(expr));
	}

	@Test
	public void visitASTMinus_withValidExpression()
	{
		ASTMinus expr = mock(ASTMinus.class);
		when(expr.getLeftSide()).thenReturn(mock(ASTExpression.class));
		when(expr.getRightSide()).thenReturn(mock(ASTExpression.class));

		assertNull(symbolTree.visit(expr));
	}

	@Test
	public void visitASTTimes_withValidExpression()
	{
		ASTTimes expr = mock(ASTTimes.class);
		when(expr.getLeftSide()).thenReturn(mock(ASTExpression.class));
		when(expr.getRightSide()).thenReturn(mock(ASTExpression.class));

		assertNull(symbolTree.visit(expr));
	}

	@Test
	public void visitASTDivision_withValidExpression()
	{
		ASTDivision expr = mock(ASTDivision.class);
		when(expr.getLeftSide()).thenReturn(mock(ASTExpression.class));
		when(expr.getRightSide()).thenReturn(mock(ASTExpression.class));

		assertNull(symbolTree.visit(expr));
	}

	@Test
	public void visitASTModulus_withValidExpression()
	{
		ASTModulus expr = mock(ASTModulus.class);
		when(expr.getLeftSide()).thenReturn(mock(ASTExpression.class));
		when(expr.getRightSide()).thenReturn(mock(ASTExpression.class));

		assertNull(symbolTree.visit(expr));
	}

	@Test
	public void visitASTStringArrayType_withValidType()
	{
		ASTStringArrayType type = mock(ASTStringArrayType.class);
		assertEquals(type, symbolTree.visit(type));
	}

	@Test
	public void visitASTArrayLiteral_withValidLiteral()
	{
		ASTArrayLiteral array = mock(ASTArrayLiteral.class);
		when(array.getExpressionList()).thenReturn(Collections.emptyList());

		assertNull(symbolTree.visit(array));
	}

	@Test
	public void visitASTForeach_withValidLoop()
	{
		ASTForeach loop = mock(ASTForeach.class);
		when(loop.getIterable()).thenReturn(mock(ASTExpression.class));
		when(loop.getVariable()).thenReturn(mock(ASTVariable.class));
		when(loop.getBody()).thenReturn(mock(ASTBody.class));

		assertNull(symbolTree.visit(loop));
	}

	@Test
	public void visitASTLambda_withValidLambda()
	{
		ASTLambda lambda = mock(ASTLambda.class);
		when(lambda.getArgumentList()).thenReturn(Collections.emptyList());
		when(lambda.getReturnType()).thenReturn(mock(ASTType.class));
		when(lambda.getBody()).thenReturn(mock(ASTBody.class));

		assertNull(symbolTree.visit(lambda));
	}

	@Test
	public void visitASTImport_withValidLibrary()
	{
		ASTImport astImport = mock(ASTImport.class);
		when(astImport.getLibrary()).thenReturn(mock(ASTIdentifier.class));
		Library mockLibrary = mock(Library.class);

		try (MockedStatic<LibraryManager> mocked = mockStatic(LibraryManager.class)) {
			mocked.when(() -> LibraryManager.findLibrary(anyString())).thenReturn(Optional.of(mockLibrary));

			assertNull(symbolTree.visit(astImport));
		}
	}

	@Test
	public void visitASTImport_withInvalidLibrary()
	{
		ASTImport astImport = mock(ASTImport.class);
		when(astImport.getLibrary()).thenReturn(mock(ASTIdentifier.class));
		when(astImport.getToken()).thenReturn(mock(Token.class));

		try (MockedStatic<LibraryManager> mocked = mockStatic(LibraryManager.class)) {
			mocked.when(() -> LibraryManager.findLibrary(anyString())).thenReturn(Optional.empty());
		}

		assertNull(symbolTree.visit(astImport));
		assertTrue(SemanticErrors.hasErrors());
	}

	@Test
	public void visitASTParameterizedType_withValidType()
	{
		ASTParameterizedType type = mock(ASTParameterizedType.class);
		assertEquals(type, symbolTree.visit(type));
	}

	@Test
	public void visitASTInterface_withValidInterface()
	{
		ASTInterface astInterface = mock(ASTInterface.class);
		when(astInterface.getName()).thenReturn(new ASTIdentifier(mock(Token.class), "TestInterface"));
		when(astInterface.getExtendedInterfaces()).thenReturn(Collections.emptyList());
		when(astInterface.getMethodSignatures()).thenReturn(Collections.emptyList());

		assertNull(symbolTree.visit(astInterface));
	}

	@Test
	public void visitASTInterface_withDuplicateInterface()
	{
		SymbolProgram mockSymbolProgram = mock(SymbolProgram.class);
		when(mockSymbolProgram.addInterface(anyString())).thenReturn(false);
		symbolTree.setSymbolProgram(mockSymbolProgram);

		ASTInterface astInterface = mock(ASTInterface.class);
		when(astInterface.getName()).thenReturn(new ASTIdentifier(mock(Token.class), "TestInterface"));
		when(astInterface.getExtendedInterfaces()).thenReturn(Collections.emptyList());
		when(astInterface.getMethodSignatures()).thenReturn(Collections.emptyList());
		when(astInterface.getToken()).thenReturn(token);

		symbolTree.visit(astInterface);
		assertTrue(SemanticErrors.hasErrors());
	}
}