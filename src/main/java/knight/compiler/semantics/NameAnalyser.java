package knight.compiler.semantics;

import knight.compiler.ast.*;
import knight.compiler.ast.controlflow.ASTConditionalBranch;
import knight.compiler.ast.controlflow.ASTForEach;
import knight.compiler.ast.controlflow.ASTIfChain;
import knight.compiler.ast.controlflow.ASTWhile;
import knight.compiler.ast.expressions.*;
import knight.compiler.ast.program.*;
import knight.compiler.ast.statements.*;
import knight.compiler.ast.types.*;
import knight.compiler.semantics.diagnostics.DiagnosticReporter;
import knight.compiler.semantics.model.*;
import knight.compiler.semantics.utils.ScopeManager;

import java.util.HashSet;
import java.util.Set;

public class NameAnalyser implements ASTVisitor<ASTType>
{
	private final SymbolProgram symbolProgram;
	private final ScopeManager scopeManager;
	private final Set<String> processedClasses = new HashSet<>();
	private final Set<String> processedFunctions = new HashSet<>();

	public NameAnalyser(SymbolProgram symbolProgram)
	{
		this.symbolProgram = symbolProgram;
		this.scopeManager = new ScopeManager();
	}

	private SymbolVariable resolveVariable(String name)
	{
		if (scopeManager.getCurrentScope() != null) {
			SymbolVariable variable = scopeManager.getCurrentScope().getVariable(name);
			if (variable != null) {
				return variable;
			}
		}

		if (scopeManager.isInClass()) {
			SymbolProperty property = scopeManager.getCurrentClass().getProperty(name);
			if (property != null) {
				// Convert property to variable for binding (properties are accessible like
				// variables)
				return new SymbolVariable(property.getName(), property.getType());
			}
		}

		return symbolProgram.getGlobalVariable(name);
	}

	private SymbolFunction resolveFunction(String functionName, String instanceName)
	{
		if (instanceName != null) {
			SymbolClass instanceClass = symbolProgram.getClass(instanceName);
			if (instanceClass != null) {
				return instanceClass.getFunction(functionName);
			}
		} else if (scopeManager.isInClass()) {
			SymbolFunction function = scopeManager.getCurrentClass().getFunction(functionName);
			if (function != null) {
				return function;
			}
		}

		return symbolProgram.getGlobalFunction(functionName);
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

		SymbolClass symbolClass = symbolProgram.getClass(className);
		if (symbolClass == null) {
			DiagnosticReporter.error(astClass, "Class " + className + " not found in symbol table");
			return null;
		}

		scopeManager.enterClass(symbolClass);
		astClass.getIdentifier().setBinding(symbolClass);

		try {
			for (ASTProperty astProperty : astClass.getProperties()) {
				astProperty.accept(this);
			}

			for (ASTFunction astFunction : astClass.getFunctions()) {
				astFunction.accept(this);
			}
		} finally {
			scopeManager.exitClass();
			processedFunctions.clear();
		}

		return null;
	}

	@Override
	public ASTType visit(ASTProperty astProperty)
	{
		astProperty.getType().accept(this);

		String propertyName = astProperty.getIdentifier().getName();
		SymbolClass currentClass = scopeManager.getCurrentClass();
		SymbolProperty property = currentClass.getProperty(propertyName);

		if (property != null) {
			astProperty.getIdentifier().setBinding(property);
		} else {
			DiagnosticReporter.error(astProperty,
					"Property " + propertyName + " not found in class " + currentClass.getName());
		}

		return null;
	}

	@Override
	public ASTType visit(ASTFunction astFunction)
	{
		String functionName = astFunction.getIdentifier().getName();

		if (processedFunctions.contains(functionName)) {
			return null;
		}
		processedFunctions.add(functionName);

		SymbolFunction symbolFunction;
		if (scopeManager.isInClass()) {
			symbolFunction = scopeManager.getCurrentClass().getFunction(functionName);
		} else {
			symbolFunction = symbolProgram.getGlobalFunction(functionName);
		}

		if (symbolFunction == null) {
			DiagnosticReporter.error(astFunction, "Function " + functionName + " not found in symbol table");
			return null;
		}

		scopeManager.enterFunction(symbolFunction);
		astFunction.getIdentifier().setBinding(symbolFunction);

		try {
			astFunction.getReturnType().accept(this);

			for (int i = 0; i < astFunction.getArgumentCount(); i++) {
				astFunction.getArgument(i).accept(this);
			}

			astFunction.getBody().accept(this);
		} finally {
			scopeManager.exitFunction();
		}

		return null;
	}

	@Override
	public ASTType visit(ASTBody astBody)
	{
		Scope bodyScope = astBody.getScope();
		Scope savedScope = scopeManager.getCurrentScope();
		scopeManager.setCurrentScope(bodyScope);

		try {
			for (AST node : astBody.getNodes()) {
				node.accept(this);
			}
		} finally {
			scopeManager.setCurrentScope(savedScope);
		}

		return null;
	}

	@Override
	public ASTType visit(ASTArgument astArgument)
	{
		astArgument.getType().accept(this);

		String paramName = astArgument.getIdentifier().getName();
		SymbolFunction currentFunction = scopeManager.getCurrentFunction();
		SymbolVariable parameter = currentFunction != null ? currentFunction.getParameter(paramName) : null;

		if (parameter != null) {
			astArgument.getIdentifier().setBinding(parameter);
		} else {
			DiagnosticReporter.error(astArgument, "Parameter " + paramName + " not found in function");
		}

		return null;
	}

	@Override
	public ASTType visit(ASTVariable astVariable)
	{
		astVariable.getType().accept(this);

		String varName = astVariable.getIdentifier().getName();
		SymbolVariable variable = resolveVariable(varName);

		if (variable != null) {
			astVariable.getIdentifier().setBinding(variable);
		} else {
			DiagnosticReporter.error(astVariable, "Variable " + varName + " not declared");
		}

		return null;
	}

	@Override
	public ASTType visit(ASTVariableInit astVariableInit)
	{
		astVariableInit.getType().accept(this);

		String varName = astVariableInit.getIdentifier().getName();
		SymbolVariable variable = resolveVariable(varName);

		if (variable != null) {
			astVariableInit.getIdentifier().setBinding(variable);
		} else {
			DiagnosticReporter.error(astVariableInit, "Variable " + varName + " not declared");
		}

		if (astVariableInit.getExpression() != null) {
			astVariableInit.getExpression().accept(this);
		}

		return null;
	}

	@Override
	public ASTType visit(ASTIdentifier astIdentifier)
	{
		String identifier = astIdentifier.getName();
		SymbolVariable variable = resolveVariable(identifier);

		if (variable != null) {
			astIdentifier.setBinding(variable);
		} else {
			DiagnosticReporter.error(astIdentifier, "Variable " + identifier + " not declared");
		}

		return null;
	}

	@Override
	public ASTType visit(ASTIdentifierExpr astIdentifierExpr)
	{
		String identifier = astIdentifierExpr.getName();
		SymbolVariable variable = resolveVariable(identifier);

		if (variable != null) {
			astIdentifierExpr.setBinding(variable);
		} else {
			DiagnosticReporter.error(astIdentifierExpr, "Variable " + identifier + " not declared");
		}

		return null;
	}

	@Override
	public ASTType visit(ASTIdentifierType astIdentifierType)
	{
		String identifier = astIdentifierType.getName();
		SymbolClass symbolClass = symbolProgram.getClass(identifier);

		if (symbolClass != null) {
			astIdentifierType.setBinding(symbolClass);
		} else {
			DiagnosticReporter.error(astIdentifierType, "Class " + identifier + " not found");
		}

		return null;
	}

	@Override
	public ASTType visit(ASTAssign astAssign)
	{
		astAssign.getIdentifier().accept(this);
		astAssign.getExpression().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTFieldAssign astFieldAssign)
	{
		astFieldAssign.getInstance().accept(this);
		astFieldAssign.getValue().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTCallFunctionExpr astCallFunctionExpr)
	{
		String functionName = astCallFunctionExpr.getFunctionName().getName();
		String instanceName = null;

		if (astCallFunctionExpr.getInstance() != null) {
			instanceName = astCallFunctionExpr.getInstance().getName();
		}

		SymbolFunction symbolFunction = resolveFunction(functionName, instanceName);

		if (symbolFunction != null) {
			astCallFunctionExpr.getFunctionName().setBinding(symbolFunction);
		} else {
			DiagnosticReporter.error(astCallFunctionExpr, "Function '" + functionName + "' not found");
		}

		for (ASTExpression astExpression : astCallFunctionExpr.getArguments()) {
			astExpression.accept(this);
		}

		return null;
	}

	@Override
	public ASTType visit(ASTCallFunctionStat astCallFunctionStat)
	{
		String functionName = astCallFunctionStat.getFunctionName().getName();
		String instanceName = null;

		if (astCallFunctionStat.getInstance() != null) {
			instanceName = astCallFunctionStat.getInstance().getName();
		}

		SymbolFunction symbolFunction = resolveFunction(functionName, instanceName);

		if (symbolFunction != null) {
			astCallFunctionStat.getFunctionName().setBinding(symbolFunction);
		} else {
			DiagnosticReporter.error(astCallFunctionStat, "Function '" + functionName + "' not found");
		}

		for (ASTExpression astExpression : astCallFunctionStat.getArguments()) {
			astExpression.accept(this);
		}

		return null;
	}

	@Override
	public ASTType visit(ASTNewInstance astNewInstance)
	{
		String className = astNewInstance.getClassName().getName();
		SymbolClass symbolClass = symbolProgram.getClass(className);

		if (symbolClass != null) {
			astNewInstance.getClassName().setBinding(symbolClass);
		} else {
			DiagnosticReporter.error(astNewInstance.getClassName(),
					"Cannot instantiate undefined class '" + className + "'");
		}

		for (ASTArgument astArgument : astNewInstance.getArguments()) {
			astArgument.accept(this);
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
	public ASTType visit(ASTWhile astWhile)
	{
		astWhile.getCondition().accept(this);
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
		astConditionalBranch.getCondition().accept(this);
		astConditionalBranch.getBody().accept(this);

		return null;
	}

	@Override
	public ASTType visit(ASTForEach astForEach)
	{
		astForEach.getIterable().accept(this);
		astForEach.getVariable().accept(this);
		astForEach.getBody().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTArrayIndexExpr astArrayIndexExpr)
	{
		astArrayIndexExpr.getArray().accept(this);
		astArrayIndexExpr.getIndex().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTArrayAssign astArrayAssign)
	{
		astArrayAssign.getIdentifier().accept(this);
		astArrayAssign.getArray().accept(this);
		astArrayAssign.getValue().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTArrayLiteral astArrayLiteral)
	{
		for (ASTExpression element : astArrayLiteral.getExpressions()) {
			element.accept(this);
		}
		return null;
	}

	@Override
	public ASTType visit(ASTLambda astLambda)
	{
		SymbolFunction lambdaFunction = new SymbolFunction("lambda", astLambda.getReturnType());
		scopeManager.enterFunction(lambdaFunction);

		try {
			for (ASTArgument astArgument : astLambda.getArguments()) {
				astArgument.accept(this);
			}

			astLambda.getReturnType().accept(this);
			astLambda.getBody().accept(this);
		} finally {
			scopeManager.exitFunction();
		}

		return null;
	}

	@Override
	public ASTType visit(ASTInterface astInterface)
	{
		String interfaceName = astInterface.getIdentifier().getName();
		SymbolInterface symbolInterface = symbolProgram.getInterface(interfaceName);

		if (symbolInterface != null) {
			astInterface.getIdentifier().setBinding(symbolInterface);
		} else {
			DiagnosticReporter.error(astInterface, "Interface " + interfaceName + " not found");
		}

		return null;
	}

	@Override
	public ASTType visit(ASTFunctionType astFunctionType)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTIntType astIntType)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTStringType astStringType)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTVoidType astVoidType)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTBooleanType astBooleanType)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTIntArrayType astIntArrayType)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTStringArrayType astStringArrayType)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTParameterizedType astParameterizedType)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTIntLiteral astIntLiteral)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTTrue astTrue)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTFalse astFalse)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTStringLiteral astStringLiteral)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTNewArray astNewArray)
	{
		astNewArray.getArrayLength().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTNotEquals astNotEquals)
	{
		astNotEquals.getLeft().accept(this);
		astNotEquals.getRight().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTPlus astPlus)
	{
		astPlus.getLeft().accept(this);
		astPlus.getRight().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTOr astOr)
	{
		astOr.getLeft().accept(this);
		astOr.getRight().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTAnd astAnd)
	{
		astAnd.getLeft().accept(this);
		astAnd.getRight().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTEquals astEquals)
	{
		astEquals.getLeft().accept(this);
		astEquals.getRight().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTLessThan astLessThan)
	{
		astLessThan.getLeft().accept(this);
		astLessThan.getRight().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTLessThanOrEqual astLessThanOrEqual)
	{
		astLessThanOrEqual.getLeft().accept(this);
		astLessThanOrEqual.getRight().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTGreaterThan astGreaterThan)
	{
		astGreaterThan.getLeft().accept(this);
		astGreaterThan.getRight().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTGreaterThanOrEqual astGreaterThanOrEqual)
	{
		astGreaterThanOrEqual.getLeft().accept(this);
		astGreaterThanOrEqual.getRight().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTMinus astMinus)
	{
		astMinus.getLeft().accept(this);
		astMinus.getRight().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTTimes astTimes)
	{
		astTimes.getLeft().accept(this);
		astTimes.getRight().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTDivision astDivision)
	{
		astDivision.getLeft().accept(this);
		astDivision.getRight().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTModulus astModulus)
	{
		astModulus.getLeft().accept(this);
		astModulus.getRight().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTImport astImport)
	{
		return null;
	}
}
