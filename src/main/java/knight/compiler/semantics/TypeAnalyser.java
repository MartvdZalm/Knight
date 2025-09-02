package knight.compiler.semantics;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import knight.compiler.ast.AST;
import knight.compiler.ast.ASTVisitor;
import knight.compiler.ast.contracts.IASTCallFunction;
import knight.compiler.ast.controlflow.ASTConditionalBranch;
import knight.compiler.ast.controlflow.ASTForEach;
import knight.compiler.ast.controlflow.ASTIfChain;
import knight.compiler.ast.controlflow.ASTWhile;
import knight.compiler.ast.expressions.ASTAnd;
import knight.compiler.ast.expressions.ASTArrayIndexExpr;
import knight.compiler.ast.expressions.ASTArrayLiteral;
import knight.compiler.ast.expressions.ASTBinaryExpression;
import knight.compiler.ast.expressions.ASTCallFunctionExpr;
import knight.compiler.ast.expressions.ASTDivision;
import knight.compiler.ast.expressions.ASTEquals;
import knight.compiler.ast.expressions.ASTExpression;
import knight.compiler.ast.expressions.ASTFalse;
import knight.compiler.ast.expressions.ASTFieldAccessExpr;
import knight.compiler.ast.expressions.ASTGreaterThan;
import knight.compiler.ast.expressions.ASTGreaterThanOrEqual;
import knight.compiler.ast.expressions.ASTIdentifierExpr;
import knight.compiler.ast.expressions.ASTIntLiteral;
import knight.compiler.ast.expressions.ASTLambda;
import knight.compiler.ast.expressions.ASTLessThan;
import knight.compiler.ast.expressions.ASTLessThanOrEqual;
import knight.compiler.ast.expressions.ASTMinus;
import knight.compiler.ast.expressions.ASTModulus;
import knight.compiler.ast.expressions.ASTNewArray;
import knight.compiler.ast.expressions.ASTNewInstance;
import knight.compiler.ast.expressions.ASTNotEquals;
import knight.compiler.ast.expressions.ASTOr;
import knight.compiler.ast.expressions.ASTPlus;
import knight.compiler.ast.expressions.ASTStringLiteral;
import knight.compiler.ast.expressions.ASTTimes;
import knight.compiler.ast.expressions.ASTTrue;
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
import knight.compiler.library.LibraryManager;
import knight.compiler.semantics.diagnostics.DiagnosticReporter;
import knight.compiler.semantics.model.Binding;
import knight.compiler.semantics.model.SymbolClass;
import knight.compiler.semantics.model.SymbolFunction;
import knight.compiler.semantics.model.SymbolProgram;
import knight.compiler.semantics.model.SymbolProperty;
import knight.compiler.semantics.model.SymbolVariable;
import knight.compiler.semantics.utils.ScopeManager;

public class TypeAnalyser implements ASTVisitor<ASTType>
{
	private SymbolProgram symbolProgram;
	private final ScopeManager scopeManager;
	private final Set<String> processedClasses = new HashSet<>();
	private final Set<String> processedFunctions = new HashSet<>();

	public TypeAnalyser(SymbolProgram symbolProgram)
	{
		this.symbolProgram = symbolProgram;
		this.scopeManager = new ScopeManager();
	}

	@Override
	public ASTType visit(ASTProgram astProgram)
	{
		for (AST node : astProgram.getNodes()) {
			node.accept(this);
		}

		return null;
	}

	@Override
	public ASTType visit(ASTClass astClass)
	{
		String className = astClass.getIdentifier().getName();

		if (processedClasses.contains(className)) {
			return null;
		}
		processedClasses.add(className);

		SymbolClass symbolClass = (SymbolClass) astClass.getIdentifier().getBinding();
		scopeManager.enterClass(symbolClass);
		processedFunctions.clear();

		try {
			for (ASTProperty astProperty : astClass.getProperties()) {
				astProperty.accept(this);
			}

			for (ASTFunction astFunction : astClass.getFunctions()) {
				astFunction.accept(this);
			}
		} finally {
			scopeManager.exitClass();
		}

		return null;
	}

	@Override
	public ASTType visit(ASTFunction astFunction)
	{
		String functionName = astFunction.getIdentifier().getName();

		if (processedFunctions.contains(functionName)) {
			return astFunction.getReturnType();
		}
		processedFunctions.add(functionName);

		SymbolFunction symbolFunction = (SymbolFunction) astFunction.getIdentifier().getBinding();
		scopeManager.enterFunction(symbolFunction);

		try {
			for (ASTArgument astArgument : astFunction.getArguments()) {
				astArgument.accept(this);
			}
			astFunction.getBody().accept(this);

			// TODO: Validate return type matches function declaration
		} finally {
			scopeManager.exitFunction();
		}

		return null;
	}

	@Override
	public ASTType visit(ASTBody astBody)
	{
		scopeManager.enterBlock();

		try {
			for (AST node : astBody.getNodes()) {
				node.accept(this);
			}
		} finally {
			scopeManager.exitBlock();
		}

		return null;
	}

	@Override
	public ASTType visit(ASTAssign astAssign)
	{
		ASTType leftType = astAssign.getIdentifier().accept(this);
		ASTType rightType = astAssign.getExpression().accept(this);

		if (!isCompatible(leftType, rightType)) {
			DiagnosticReporter.error(astAssign, "Cannot assign " + rightType + " to " + leftType);
		} else {
			astAssign.getExpression().setType(rightType);
		}

		return leftType;
	}

	@Override
	public ASTType visit(ASTFieldAssign astFieldAssign)
	{
		ASTType instanceType = astFieldAssign.getInstance().accept(this);
		ASTType valueType = astFieldAssign.getValue().accept(this);
		String fieldName = astFieldAssign.getField().getName();

		if (instanceType instanceof ASTIdentifierType) {
			String className = ((ASTIdentifierType) instanceType).getName();
			SymbolClass symbolClass = symbolProgram.getClass(className);

			if (symbolClass != null) {
				SymbolProperty property = symbolClass.getProperty(fieldName);

				if (property != null) {
					if (!isCompatible(property.getType(), valueType)) {
						DiagnosticReporter.error(astFieldAssign.getValue(), "Cannot assign " + valueType + " to field '"
								+ fieldName + "' of type " + property.getType() + " in class " + className);
					}

					astFieldAssign.getField().setType(property.getType());
					return property.getType();
				} else {
					DiagnosticReporter.error(astFieldAssign.getField(),
							"Field '" + fieldName + "' not found in class '" + className + "'");
				}
			} else {
				DiagnosticReporter.error(astFieldAssign.getInstance(), "Unknown class '" + className + "'");
			}
		} else if (instanceType != null) {
			DiagnosticReporter.error(astFieldAssign.getInstance(),
					"Field access requires class instance, found: " + instanceType);
		}

		return null;
	}

	@Override
	public ASTType visit(ASTVariableInit astVariableInit)
	{
		ASTType declaredType = astVariableInit.getType();
		ASTType expressionType = astVariableInit.getExpression().accept(this);

		if (!isCompatible(declaredType, expressionType)) {
			DiagnosticReporter.error(astVariableInit, "Cannot initialize " + declaredType + " with " + expressionType);
		} else {
			astVariableInit.getExpression().setType(expressionType);
		}

		return declaredType;
	}

	@Override
	public ASTType visit(ASTPlus astPlus)
	{
		ASTType leftType = astPlus.getLeft().accept(this);
		ASTType rightType = astPlus.getRight().accept(this);

		if (leftType instanceof ASTStringType || rightType instanceof ASTStringType) {
			ASTType resultType = new ASTStringType(astPlus.getToken());
			astPlus.setType(resultType);

			if (!(leftType instanceof ASTStringType) || !(rightType instanceof ASTStringType)) {
				DiagnosticReporter.warning(astPlus, "Implicit conversion to string in concatenation");
			}
			return resultType;
		}

		validateBinaryOperation(astPlus, leftType, rightType, ASTIntType.class, "+");
		ASTType resultType = new ASTIntType(astPlus.getToken());
		astPlus.setType(resultType);
		return resultType;
	}

	@Override
	public ASTType visit(ASTMinus astMinus)
	{
		return handleBinaryArithmetic(astMinus, astMinus.getLeft(), astMinus.getRight(), "-");
	}

	@Override
	public ASTType visit(ASTTimes astTimes)
	{
		return handleBinaryArithmetic(astTimes, astTimes.getLeft(), astTimes.getRight(), "*");
	}

	@Override
	public ASTType visit(ASTDivision astDivision)
	{
		return handleBinaryArithmetic(astDivision, astDivision.getLeft(), astDivision.getRight(), "/");
	}

	@Override
	public ASTType visit(ASTModulus astModulus)
	{
		return handleBinaryArithmetic(astModulus, astModulus.getLeft(), astModulus.getRight(), "%");
	}

	@Override
	public ASTType visit(ASTEquals astEquals)
	{
		return handleComparison(astEquals, astEquals.getLeft(), astEquals.getRight(), "==");
	}

	@Override
	public ASTType visit(ASTNotEquals astNotEquals)
	{
		return handleComparison(astNotEquals, astNotEquals.getLeft(), astNotEquals.getRight(), "!=");
	}

	@Override
	public ASTType visit(ASTLessThan astLessThan)
	{
		return handleComparison(astLessThan, astLessThan.getLeft(), astLessThan.getRight(), "<");
	}

	@Override
	public ASTType visit(ASTLessThanOrEqual astLessThanOrEqual)
	{
		return handleComparison(astLessThanOrEqual, astLessThanOrEqual.getLeft(), astLessThanOrEqual.getRight(), "<=");
	}

	@Override
	public ASTType visit(ASTGreaterThan astGreaterThan)
	{
		return handleComparison(astGreaterThan, astGreaterThan.getLeft(), astGreaterThan.getRight(), ">");
	}

	@Override
	public ASTType visit(ASTGreaterThanOrEqual astGreaterThanOrEqual)
	{
		return handleComparison(astGreaterThanOrEqual, astGreaterThanOrEqual.getLeft(),
				astGreaterThanOrEqual.getRight(), ">=");
	}

	@Override
	public ASTType visit(ASTAnd astAnd)
	{
		return handleLogicalOperation(astAnd, astAnd.getLeft(), astAnd.getRight(), "&&");
	}

	@Override
	public ASTType visit(ASTOr astOr)
	{
		return handleLogicalOperation(astOr, astOr.getLeft(), astOr.getRight(), "||");
	}

	@Override
	public ASTType visit(ASTCallFunctionExpr astCallFunctionExpr)
	{
		return handleFunctionCall(astCallFunctionExpr, astCallFunctionExpr.getFunctionName(),
				astCallFunctionExpr.getArguments());
	}

	@Override
	public ASTType visit(ASTCallFunctionStat astCallFunctionStat)
	{
		handleFunctionCall(astCallFunctionStat, astCallFunctionStat.getFunctionName(),
				astCallFunctionStat.getArguments());
		return null;
	}

	@Override
	public ASTType visit(ASTIntLiteral astIntLiteral)
	{
		ASTType astType = new ASTIntType(astIntLiteral.getToken());
		astIntLiteral.setType(astType);
		return astType;
	}

	@Override
	public ASTType visit(ASTStringLiteral astStringLiteral)
	{
		ASTType astType = new ASTStringType(astStringLiteral.getToken());
		astStringLiteral.setType(astType);
		return astType;
	}

	@Override
	public ASTType visit(ASTTrue astTrue)
	{
		ASTType astType = new ASTBooleanType(astTrue.getToken());
		astTrue.setType(astType);
		return astType;
	}

	@Override
	public ASTType visit(ASTFalse astFalse)
	{
		ASTType astType = new ASTBooleanType(astFalse.getToken());
		astFalse.setType(astType);
		return astType;
	}

	@Override
	public ASTType visit(ASTIdentifierExpr astIdentifierExpr)
	{
		Binding binding = astIdentifierExpr.getBinding();
		if (binding != null) {
			if (binding instanceof SymbolVariable) {
				ASTType astType = ((SymbolVariable) binding).getType();
				astIdentifierExpr.setType(astType);
				return astType;
			}

			if (binding instanceof SymbolClass) {
				ASTType astType = ((SymbolClass) binding).getType();
				astIdentifierExpr.setType(astType);
				return astType;
			}

			DiagnosticReporter.error(astIdentifierExpr,
					"Unexpected binding type: " + binding.getClass().getSimpleName());
		}

		return null;
	}

	@Override
	public ASTType visit(ASTIdentifier astIdentifier)
	{
		Binding binding = astIdentifier.getBinding();
		if (binding != null) {
			if (binding instanceof SymbolVariable) {
				return ((SymbolVariable) binding).getType();
			}
			if (binding instanceof SymbolClass) {
				return ((SymbolClass) binding).getType();
			}

			DiagnosticReporter.error(astIdentifier, "Unexpected binding type: " + binding.getClass().getSimpleName());
		}
		return null;
	}

	@Override
	public ASTType visit(ASTWhile astWhile)
	{
		ASTType conditionType = astWhile.getCondition().accept(this);
		validateBooleanCondition(astWhile.getCondition(), conditionType);
		astWhile.getBody().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTIfChain astIfChain)
	{
		for (ASTConditionalBranch astConditionalBranch : astIfChain.getBranches()) {
			astConditionalBranch.accept(this);
		}

		if (astIfChain.getElseBody() != null) {
			astIfChain.getElseBody().accept(this);
		}

		return null;
	}

	@Override
	public ASTType visit(ASTConditionalBranch astConditionalBranch)
	{
		ASTType conditionType = astConditionalBranch.getCondition().accept(this);
		validateBooleanCondition(astConditionalBranch.getCondition(), conditionType);
		astConditionalBranch.getBody().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTFunctionType astFunctionType)
	{
		return astFunctionType;
	}

	@Override
	public ASTType visit(ASTIntType astIntType)
	{
		return astIntType;
	}

	@Override
	public ASTType visit(ASTStringType astStringType)
	{
		return astStringType;
	}

	@Override
	public ASTType visit(ASTVoidType astVoidType)
	{
		return astVoidType;
	}

	@Override
	public ASTType visit(ASTBooleanType astBooleanType)
	{
		return astBooleanType;
	}

	@Override
	public ASTType visit(ASTIntArrayType astIntArrayType)
	{
		return astIntArrayType;
	}

	@Override
	public ASTType visit(ASTStringArrayType astStringArrayType)
	{
		return astStringArrayType;
	}

	@Override
	public ASTType visit(ASTIdentifierType astIdentifierType)
	{
		return astIdentifierType;
	}

	@Override
	public ASTType visit(ASTParameterizedType astParameterizedType)
	{
		return astParameterizedType;
	}

	@Override
	public ASTType visit(ASTNewArray astNewArray)
	{
		ASTType typeArrayLength = astNewArray.getArrayLength().accept(this);
		if (typeArrayLength == null || !(typeArrayLength instanceof ASTIntType)) {
			DiagnosticReporter.error(astNewArray.getArrayLength().getToken(), "Array length must be of type int");
		}

		ASTType astType = new ASTIntArrayType(astNewArray.getToken());
		astNewArray.setType(astType);
		return astType;
	}

	@Override
	public ASTType visit(ASTNewInstance astNewInstance)
	{
		Binding binding = astNewInstance.getClassName().getBinding();
		if (binding instanceof SymbolClass) {
			ASTType type = ((SymbolClass) binding).getType();
			astNewInstance.setType(type);
			return type;
		}
		return null;
	}

	@Override
	public ASTType visit(ASTReturnStatement astReturnStatement)
	{
		if (astReturnStatement.getExpression() != null) {
			astReturnStatement.getExpression().accept(this);
		}
		return null;
	}

	@Override
	public ASTType visit(ASTArrayIndexExpr astArrayIndexExpr)
	{
		ASTType typeArray = astArrayIndexExpr.getArray().accept(this);
		if (typeArray == null || !(typeArray instanceof ASTIntArrayType)) {
			DiagnosticReporter.error(astArrayIndexExpr.getArray().getToken(),
					"Array expression must evaluate to be of Type int[]");
		} else {
			astArrayIndexExpr.getArray().setType(typeArray);
		}

		ASTType typeArrayIndex = astArrayIndexExpr.getIndex().accept(this);
		if (typeArrayIndex == null || !(typeArrayIndex instanceof ASTIntType)) {
			DiagnosticReporter.error(astArrayIndexExpr.getIndex().getToken(),
					"Index expression must evaluate to be of Type int");
		} else {
			astArrayIndexExpr.getIndex().setType(typeArrayIndex);
		}

		ASTType astType = new ASTIntType(astArrayIndexExpr.getToken());
		astArrayIndexExpr.setType(astType);
		return astType;
	}

	@Override
	public ASTType visit(ASTArrayAssign astArrayAssign)
	{
		ASTType typeArrayId = astArrayAssign.getIdentifier().accept(this);
		if (typeArrayId == null || !(typeArrayId instanceof ASTIntArrayType)) {
			DiagnosticReporter.error(astArrayAssign.getIdentifier().getToken(), "Identifier must be of Type int[]");
		}

		ASTType typeArrayExpr1 = astArrayAssign.getArray().accept(this);
		if (typeArrayExpr1 == null || !(typeArrayExpr1 instanceof ASTIntType)) {
			DiagnosticReporter.error(astArrayAssign.getArray().getToken(), "Expression must be of Type int");
		} else {
			astArrayAssign.getArray().setType(typeArrayExpr1);
		}

		ASTType typeArrayExpr2 = astArrayAssign.getValue().accept(this);
		if (typeArrayExpr2 == null || !(typeArrayExpr2 instanceof ASTIntType)) {
			DiagnosticReporter.error(astArrayAssign.getValue().getToken(), "Expression must be of Type int");
		} else {
			astArrayAssign.getValue().setType(typeArrayExpr2);
		}

		return null;
	}

	@Override
	public ASTType visit(ASTProperty astProperty)
	{
		return astProperty.getType();
	}

	@Override
	public ASTType visit(ASTArgument astArgument)
	{
		return astArgument.getType();
	}

	@Override
	public ASTType visit(ASTVariable astVariable)
	{
		return astVariable.getType();
	}

	@Override
	public ASTType visit(ASTArrayLiteral astArrayLiteral)
	{
		ASTType astType = new ASTStringArrayType(astArrayLiteral.getToken());
		astArrayLiteral.setType(astType);
		return astType;
	}

	@Override
	public ASTType visit(ASTForEach astForEach)
	{
		ASTType iterableType = astForEach.getIterable().accept(this);

		if (iterableType != null && !(iterableType instanceof ASTIntArrayType)
				&& !(iterableType instanceof ASTStringArrayType)) {
			DiagnosticReporter.error(astForEach, "Foreach loop requires array type");
		}

		return null;
	}

	@Override
	public ASTType visit(ASTLambda astLambda)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTImport astImport)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTInterface astInterface)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTFieldAccessExpr astFieldAccessExpr)
	{
		ASTType instanceType = astFieldAccessExpr.getInstance().accept(this);
		String fieldName = astFieldAccessExpr.getField().getName();

		if (instanceType instanceof ASTIdentifierType) {
			String className = ((ASTIdentifierType) instanceType).getName();
			SymbolClass symbolClass = symbolProgram.getClass(className);

			if (symbolClass != null) {
				SymbolProperty property = symbolClass.getProperty(fieldName);

				if (property != null) {
					astFieldAccessExpr.getField().setType(property.getType());
					return property.getType();
				} else {
					DiagnosticReporter.error(astFieldAccessExpr.getField(),
							"Field '" + fieldName + "' not found in class '" + className + "'");
				}
			} else {
				DiagnosticReporter.error(astFieldAccessExpr.getInstance(), "Unknown class '" + className + "'");
			}
		} else if (instanceType != null) {
			DiagnosticReporter.error(astFieldAccessExpr.getInstance(),
					"Field access requires class instance, found: " + instanceType);
		}

		return null;
	}

	private boolean isCompatible(ASTType type1, ASTType type2)
	{
		if (type1 == null || type2 == null) {
			return false;
		}

		if (type1.getClass() == type2.getClass()) {
			return true;
		}

		// TODO: Add more types
		if (type1 instanceof ASTIntType && type2 instanceof ASTIntType) {
			return true;
		}

		if (type1 instanceof ASTIdentifierType && type2 instanceof ASTIdentifierType) {
			ASTIdentifierType id1 = (ASTIdentifierType) type1;
			ASTIdentifierType id2 = (ASTIdentifierType) type2;

			if (id1.getName().equals(id2.getName())) {
				return true;
			}

			// TODO: Add inheritance and interface implementation checks
		}

		return false;
	}

	private void validateBinaryOperation(ASTBinaryExpression operator, ASTType left, ASTType right,
			Class<? extends ASTType> expectedType, String operation)
	{
		if (left == null || right == null) {
			DiagnosticReporter.error(operator.getToken(),
					"Operands must have valid types for " + operation + " operator");
			return;
		}

		if (!isCompatible(left, right)
				|| (expectedType != null && !(expectedType.isInstance(left) && expectedType.isInstance(right)))) {
			DiagnosticReporter.error(operator,
					"Operator " + operation + " cannot be applied to " + left + " and " + right);
		}
	}

	private ASTType handleBinaryArithmetic(ASTBinaryExpression operator, AST left, AST right, String op)
	{
		ASTType leftType = left.accept(this);
		ASTType rightType = right.accept(this);

		validateBinaryOperation(operator, leftType, rightType, ASTIntType.class, op);

		ASTType resultType = new ASTIntType(operator.getToken());
		operator.setType(resultType);
		return resultType;
	}

	private ASTType handleComparison(ASTBinaryExpression operator, AST left, AST right, String op)
	{
		ASTType leftType = left.accept(this);
		ASTType rightType = right.accept(this);

		validateBinaryOperation(operator, leftType, rightType, null, op);

		ASTType resultType = new ASTBooleanType(operator.getToken());
		operator.setType(resultType);
		return resultType;
	}

	private ASTType handleLogicalOperation(ASTBinaryExpression operator, AST left, AST right, String op)
	{
		ASTType leftType = left.accept(this);
		ASTType rightType = right.accept(this);

		validateBinaryOperation(operator, leftType, rightType, ASTBooleanType.class, op);

		ASTType resultType = new ASTBooleanType(operator.getToken());
		operator.setType(resultType);
		return resultType;
	}

	private void validateFunctionArguments(IASTCallFunction astCall, SymbolFunction function,
			List<ASTExpression> arguments)
	{
		if (arguments.size() != function.getParameterCount()) {
			DiagnosticReporter.error(astCall.getToken(), "Function " + function.getName() + " expects "
					+ function.getParameterCount() + " arguments but got " + arguments.size());
			return;
		}

		for (int i = 0; i < arguments.size(); i++) {
			ASTType argType = arguments.get(i).accept(this);
			ASTType paramType = function.getParameter(i).getType();

			if (!isCompatible(paramType, argType)) {
				DiagnosticReporter.error(arguments.get(i),
						"Argument " + (i + 1) + ": expected " + paramType + " but got " + argType);
			}
		}
	}

	private ASTType handleFunctionCall(IASTCallFunction astCall, ASTIdentifierExpr functionName,
			List<ASTExpression> arguments)
	{
		SymbolFunction symbolFunction = (SymbolFunction) functionName.getBinding();

		if (LibraryManager.isBuiltIn(functionName.getName())) {
			// LibraryFunction libraryFunction = LibraryManager.getBuiltIn(functionName);
			return null;
		}

		if (symbolFunction == null) {
			DiagnosticReporter.error(functionName, "Function " + functionName + " not found");
			return null;
		}

		validateFunctionArguments(astCall, symbolFunction, arguments);

		ASTType returnType = symbolFunction.getReturnType();

		if (astCall instanceof ASTExpression) {
			((ASTExpression) astCall).setType(returnType);
		}

		return returnType;
	}

	private void validateBooleanCondition(AST condition, ASTType conditionType)
	{
		if (!(conditionType instanceof ASTBooleanType)) {
			DiagnosticReporter.error(condition, "Condition must be boolean type");
		}
	}
}
