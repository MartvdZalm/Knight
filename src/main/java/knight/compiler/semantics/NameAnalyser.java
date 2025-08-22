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

import java.util.HashSet;
import java.util.Set;

public class NameAnalyser implements ASTVisitor<ASTType>
{
	private SymbolProgram symbolProgram;
	private SymbolClass symbolClass;
	private SymbolFunction symbolFunction;
	private Scope currentScope;
	private final Set<String> processedClasses = new HashSet<>();
	private final Set<String> processedFunctions = new HashSet<>();

	public NameAnalyser(SymbolProgram symbolProgram)
	{
		this.symbolProgram = symbolProgram;
	}

	public void setSymbolProgram(SymbolProgram symbolProgram)
	{
		this.symbolProgram = symbolProgram;
	}

	public void setSymbolClass(SymbolClass symbolClass)
	{
		this.symbolClass = symbolClass;
	}

	public void setSymbolFunction(SymbolFunction symbolFunction)
	{
		this.symbolFunction = symbolFunction;
	}

	private SymbolVariable getVariable(String id, SymbolClass sClass, SymbolFunction sFunction)
	{
		if (currentScope != null) {
			return currentScope.getVariable(id);
		}

		return symbolProgram.getVariable(id, sClass, sFunction);
	}

	@Override
	public ASTType visit(ASTAssign astAssign)
	{
		astAssign.getIdentifier().accept(this);
		astAssign.getExpression().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTBody astBody)
	{
		Scope bodyScope = astBody.getScope();
		Scope savedScope = currentScope;
		currentScope = bodyScope;

		for (AST node : astBody.getNodes()) {
			node.accept(this);
		}

		currentScope = savedScope;
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
	public ASTType visit(ASTNewArray astNewArray)
	{
		astNewArray.getArrayLength().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTNewInstance astNewInstance)
	{
		String identifier = astNewInstance.getClassName().getName();
		SymbolClass symbolClass = symbolProgram.getClass(identifier);
		if (symbolClass == null) {
			DiagnosticReporter.error(astNewInstance.getClassName().getToken(),
					"Cannot instantiate undefined class '" + identifier + "'");
		}

		astNewInstance.getClassName().setBinding(symbolClass);
		return null;
	}

	@Override
	public ASTType visit(ASTCallFunctionExpr astCallFunctionExpr)
	{
		for (ASTExpression astExpression : astCallFunctionExpr.getArguments()) {
			astExpression.accept(this);
		}

		String functionName = astCallFunctionExpr.getFunctionName().getName();
		String instanceName = astCallFunctionExpr.getInstance().getName();

		SymbolFunction symbolFunction = null;
		if (instanceName != null) {
			symbolFunction = symbolProgram.getFunction(functionName, instanceName);
		} else {
			symbolFunction = symbolProgram.getFunction(functionName);
		}

		if (symbolFunction == null) {
			DiagnosticReporter.error(astCallFunctionExpr.getToken(), "Undefined function '" + functionName + "'");
		}

		astCallFunctionExpr.getFunctionName().setBinding(symbolFunction);
		return null;
	}

	@Override
	public ASTType visit(ASTCallFunctionStat astCallFunctionStat)
	{
		for (ASTExpression astExpression : astCallFunctionStat.getArguments()) {
			astExpression.accept(this);
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
	public ASTType visit(ASTIdentifier astIdentifier)
	{
		String identifier = astIdentifier.getName();
		SymbolVariable symbolVariable = this.getVariable(identifier, symbolClass, symbolFunction);

		if (symbolVariable == null) {
			System.out.println("NameAnalyser");

			DiagnosticReporter.error(astIdentifier.getToken(), "variable " + identifier + " is not declared");
		}

		astIdentifier.setBinding(symbolVariable);
		return null;
	}

	@Override
	public ASTType visit(ASTIdentifierType astIdentifierType)
	{
		String identifier = astIdentifierType.getName();
		SymbolClass symbolClass = symbolProgram.getClass(identifier);
		if (symbolClass == null) {
			DiagnosticReporter.error(astIdentifierType.getToken(), "Undefined class '" + identifier + "'");
		}

		astIdentifierType.setBinding(symbolClass);
		return null;
	}

	@Override
	public ASTType visit(ASTIdentifierExpr astIdentifierExpr)
	{
		String identifier = astIdentifierExpr.getName();
		SymbolVariable symbolVariable = this.getVariable(identifier, symbolClass, symbolFunction);
		if (symbolVariable == null) {
			DiagnosticReporter.error(astIdentifierExpr.getToken(), "Undefined variable '" + identifier + "'");
		}

		astIdentifierExpr.setBinding(symbolVariable);
		return null;
	}

	@Override
	public ASTType visit(ASTVariable astVariable)
	{
		astVariable.getType().accept(this);
		astVariable.getIdentifier().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTVariableInit astVariableInit)
	{
		astVariableInit.getType().accept(this);
		astVariableInit.getIdentifier().accept(this);
		astVariableInit.getExpression().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTFunction astFunction)
	{
		Scope bodyScope = astFunction.getScope();
		Scope savedScope = currentScope;
		currentScope = bodyScope;

		String functionName = astFunction.getIdentifier().getName();

		if (processedFunctions.contains(functionName)) {
			return null;
		}
		processedFunctions.add(functionName);

		SymbolFunction previousFunction = symbolFunction;
		symbolFunction = (symbolClass != null) ? symbolClass.getFunction(functionName)
				: symbolProgram.getFunction(functionName);
		astFunction.getIdentifier().setBinding(symbolFunction);

		astFunction.getReturnType().accept(this);

		for (ASTArgument astArgument : astFunction.getArguments()) {
			astArgument.accept(this);
		}

		astFunction.getBody().accept(this);

		currentScope = savedScope;
		symbolFunction = previousFunction;
		return null;
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
	public ASTType visit(ASTReturnStatement astReturnStatement)
	{
		astReturnStatement.getExpression().accept(this);
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
	public ASTType visit(ASTStringLiteral astStringLiteral)
	{
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

		SymbolClass previousClass = symbolClass;
		symbolClass = symbolProgram.getClass(className);
		astClass.getIdentifier().setBinding(symbolClass);

		for (ASTProperty astProperty : astClass.getProperties()) {
			astProperty.accept(this);
		}

		for (ASTFunction astFunction : astClass.getFunctions()) {
			astFunction.accept(this);
		}

		processedFunctions.clear();
		symbolClass = previousClass;
		return null;
	}

	@Override
	public ASTType visit(ASTProperty astProperty)
	{
		astProperty.getIdentifier().accept(this);
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
	public ASTType visit(ASTArgument astArgument)
	{
		astArgument.getIdentifier().accept(this);
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
	public ASTType visit(ASTStringArrayType astStringArrayType)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTArrayLiteral astArrayLiteral)
	{
		for (ASTExpression astExpression : astArrayLiteral.getExpressions()) {
			astExpression.accept(this);
		}
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
	public ASTType visit(ASTLambda astLambda)
	{
		astLambda.getArguments().forEach(arg -> arg.accept(this));
		astLambda.getReturnType().accept(this);
		astLambda.getBody().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTImport astImport)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTParameterizedType astParameterizedType)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTInterface astInterface)
	{
		String interfaceName = astInterface.getIdentifier().getName();
		SymbolInterface si = symbolProgram.getInterface(interfaceName);
		astInterface.getIdentifier().setBinding(si);
		return null;
	}
}
