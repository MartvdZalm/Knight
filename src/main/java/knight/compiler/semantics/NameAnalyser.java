package knight.compiler.semantics;

import knight.compiler.ast.*;
import knight.compiler.ast.controlflow.ASTConditionalBranch;
import knight.compiler.ast.controlflow.ASTForeach;
import knight.compiler.ast.controlflow.ASTIfChain;
import knight.compiler.ast.controlflow.ASTWhile;
import knight.compiler.ast.expressions.*;
import knight.compiler.ast.program.*;
import knight.compiler.ast.statements.*;
import knight.compiler.ast.types.*;
import knight.compiler.semantics.diagnostics.SemanticErrors;
import knight.compiler.semantics.model.*;

import java.util.HashSet;
import java.util.Set;

public class NameAnalyser implements ASTVisitor<ASTType>
{
	private final SymbolProgram symbolProgram;
	private SymbolClass symbolClass;
	private SymbolFunction symbolFunction;
	private Scope currentScope;

	private Set<String> processedClasses = new HashSet<>();
	private Set<String> processedFunctions = new HashSet<>();

	public NameAnalyser(SymbolProgram symbolProgram)
	{
		this.symbolProgram = symbolProgram;
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
		astAssign.getExpr().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTBody astBody)
	{
		Scope bodyScope = astBody.getScope();
		Scope savedScope = currentScope;
		currentScope = bodyScope;

		for (AST node : astBody.getNodesList()) {
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
		String identifier = astNewInstance.getClassName().getId();
		SymbolClass symbolClass = symbolProgram.getClass(identifier);
		if (symbolClass == null) {
			SemanticErrors.addError(astNewInstance.getClassName().getToken(),
					"Cannot instantiate undefined class '" + identifier + "'");
		}

		astNewInstance.getClassName().setB(symbolClass);
		return null;
	}

	@Override
	public ASTType visit(ASTCallFunctionExpr astCallFunctionExpr)
	{
		for (ASTExpression astExpression : astCallFunctionExpr.getArgumentList()) {
			astExpression.accept(this);
		}
		return null;
	}

	@Override
	public ASTType visit(ASTCallFunctionStat astCallFunctionStat)
	{
		for (ASTExpression astExpression : astCallFunctionStat.getArgumentList()) {
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
		String identifier = astIdentifier.getId();
		SymbolVariable symbolVariable = this.getVariable(identifier, symbolClass, symbolFunction);

		if (symbolVariable == null) {
			SemanticErrors.addError(astIdentifier.getToken(), "variable " + identifier + " is not declared");
		}

		astIdentifier.setB(symbolVariable);
		return null;
	}

	@Override
	public ASTType visit(ASTIdentifierType astIdentifierType)
	{
		String identifier = astIdentifierType.getId();
		SymbolClass symbolClass = symbolProgram.getClass(identifier);
		if (symbolClass == null) {
			SemanticErrors.addError(astIdentifierType.getToken(), "Undefined class '" + identifier + "'");
		}

		astIdentifierType.setB(symbolClass);
		return null;
	}

	@Override
	public ASTType visit(ASTIdentifierExpr astIdentifierExpr)
	{
		String identifier = astIdentifierExpr.getId();
		SymbolVariable symbolVariable = this.getVariable(identifier, symbolClass, symbolFunction);
		if (symbolVariable == null) {
			SemanticErrors.addError(astIdentifierExpr.getToken(), "Undefined variable '" + identifier + "'");
		}

		astIdentifierExpr.setB(symbolVariable);
		return null;
	}

	@Override
	public ASTType visit(ASTVariable astVariable)
	{
		astVariable.getType().accept(this);
		astVariable.getId().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTVariableInit astVariableInit)
	{
		astVariableInit.getType().accept(this);
		astVariableInit.getId().accept(this);
		astVariableInit.getExpr().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTFunction astFunction)
	{
		String functionName = astFunction.getFunctionName().getId();

		if (processedFunctions.contains(functionName)) {
			return null;
		}
		processedFunctions.add(functionName);

		SymbolFunction previousFunction = symbolFunction;
		symbolFunction = (symbolClass != null) ? symbolClass.getFunction(functionName)
				: symbolProgram.getFunction(functionName);
		astFunction.getFunctionName().setB(symbolFunction);

		astFunction.getReturnType().accept(this);

		for (ASTArgument astArgument : astFunction.getArgumentList()) {
			astArgument.accept(this);
		}

		astFunction.getBody().accept(this);

		symbolFunction = previousFunction;
		return null;
	}

	@Override
	public ASTType visit(ASTProgram astProgram)
	{
		for (AST node : astProgram.getNodeList()) {
			node.accept(this);
		}

		return null;
	}

	@Override
	public ASTType visit(ASTReturnStatement astReturnStatement)
	{
		astReturnStatement.getReturnExpr().accept(this);
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
		astArrayAssign.getId().accept(this);
		astArrayAssign.getExpression1().accept(this);
		astArrayAssign.getExpression2().accept(this);
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
		String className = astClass.getClassName().getId();

		if (processedClasses.contains(className)) {
			return null;
		}
		processedClasses.add(className);

		SymbolClass previousClass = symbolClass;
		symbolClass = symbolProgram.getClass(className);
		astClass.getClassName().setB(symbolClass);

		for (ASTProperty astProperty : astClass.getPropertyList()) {
			astProperty.accept(this);
		}

		for (ASTFunction astFunction : astClass.getFunctionList()) {
			astFunction.accept(this);
		}

		processedFunctions.clear();
		symbolClass = previousClass;
		return null;
	}

	@Override
	public ASTType visit(ASTProperty astProperty)
	{
		astProperty.getId().accept(this);
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
		astNotEquals.getLeftSide().accept(this);
		astNotEquals.getRightSide().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTPlus astPlus)
	{
		astPlus.getLeftSide().accept(this);
		astPlus.getRightSide().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTOr astOr)
	{
		astOr.getLeftSide().accept(this);
		astOr.getRightSide().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTAnd astAnd)
	{
		astAnd.getLeftSide().accept(this);
		astAnd.getRightSide().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTEquals astEquals)
	{
		astEquals.getLeftSide().accept(this);
		astEquals.getRightSide().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTLessThan astLessThan)
	{
		astLessThan.getLeftSide().accept(this);
		astLessThan.getRightSide().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTLessThanOrEqual astLessThanOrEqual)
	{
		astLessThanOrEqual.getLeftSide().accept(this);
		astLessThanOrEqual.getRightSide().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTGreaterThan astGreaterThan)
	{
		astGreaterThan.getLeftSide().accept(this);
		astGreaterThan.getRightSide().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTGreaterThanOrEqual astGreaterThanOrEqual)
	{
		astGreaterThanOrEqual.getLeftSide().accept(this);
		astGreaterThanOrEqual.getRightSide().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTMinus astMinus)
	{
		astMinus.getLeftSide().accept(this);
		astMinus.getRightSide().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTTimes astTimes)
	{
		astTimes.getLeftSide().accept(this);
		astTimes.getRightSide().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTDivision astDivision)
	{
		astDivision.getLeftSide().accept(this);
		astDivision.getRightSide().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTModulus astModulus)
	{
		astModulus.getLeftSide().accept(this);
		astModulus.getRightSide().accept(this);
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
		for (ASTExpression astExpression : astArrayLiteral.getExpressionList()) {
			astExpression.accept(this);
		}
		return null;
	}

	@Override
	public ASTType visit(ASTForeach astForeach)
	{
		astForeach.getIterable().accept(this);
		astForeach.getVariable().accept(this);
		astForeach.getBody().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTLambda astLambda)
	{
		astLambda.getArgumentList().forEach(arg -> arg.accept(this));
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
		String interfaceName = astInterface.getName().getId();
		SymbolInterface si = symbolProgram.getInterface(interfaceName);
		astInterface.getName().setB(si);
		return null;
	}
}
