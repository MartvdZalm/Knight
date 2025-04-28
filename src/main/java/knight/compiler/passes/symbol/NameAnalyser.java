package knight.compiler.passes.symbol;

import java.util.HashSet;
import java.util.Set;

import knight.compiler.ast.ASTAnd;
import knight.compiler.ast.ASTArgument;
import knight.compiler.ast.ASTArrayAssign;
import knight.compiler.ast.ASTArrayIndexExpr;
import knight.compiler.ast.ASTArrayLiteral;
import knight.compiler.ast.ASTAssign;
import knight.compiler.ast.ASTBody;
import knight.compiler.ast.ASTBooleanType;
import knight.compiler.ast.ASTCallFunctionExpr;
import knight.compiler.ast.ASTCallFunctionStat;
import knight.compiler.ast.ASTClass;
import knight.compiler.ast.ASTConditionalBranch;
import knight.compiler.ast.ASTDivision;
import knight.compiler.ast.ASTEquals;
import knight.compiler.ast.ASTExpression;
import knight.compiler.ast.ASTFalse;
import knight.compiler.ast.ASTFunction;
import knight.compiler.ast.ASTFunctionReturn;
import knight.compiler.ast.ASTFunctionType;
import knight.compiler.ast.ASTGreaterThan;
import knight.compiler.ast.ASTGreaterThanOrEqual;
import knight.compiler.ast.ASTIdentifier;
import knight.compiler.ast.ASTIdentifierExpr;
import knight.compiler.ast.ASTIdentifierType;
import knight.compiler.ast.ASTIfChain;
import knight.compiler.ast.ASTIntArrayType;
import knight.compiler.ast.ASTIntLiteral;
import knight.compiler.ast.ASTIntType;
import knight.compiler.ast.ASTLessThan;
import knight.compiler.ast.ASTLessThanOrEqual;
import knight.compiler.ast.ASTMinus;
import knight.compiler.ast.ASTModulus;
import knight.compiler.ast.ASTNewArray;
import knight.compiler.ast.ASTNewInstance;
import knight.compiler.ast.ASTNotEquals;
import knight.compiler.ast.ASTOr;
import knight.compiler.ast.ASTPlus;
import knight.compiler.ast.ASTProgram;
import knight.compiler.ast.ASTProperty;
import knight.compiler.ast.ASTReturnStatement;
import knight.compiler.ast.ASTStatement;
import knight.compiler.ast.ASTStringArrayType;
import knight.compiler.ast.ASTStringLiteral;
import knight.compiler.ast.ASTStringType;
import knight.compiler.ast.ASTTimes;
import knight.compiler.ast.ASTTrue;
import knight.compiler.ast.ASTType;
import knight.compiler.ast.ASTVariable;
import knight.compiler.ast.ASTVariableInit;
import knight.compiler.ast.ASTVisitor;
import knight.compiler.ast.ASTVoidType;
import knight.compiler.ast.ASTWhile;
import knight.compiler.passes.symbol.diagnostics.SemanticErrors;
import knight.compiler.passes.symbol.model.Scope;
import knight.compiler.passes.symbol.model.SymbolClass;
import knight.compiler.passes.symbol.model.SymbolFunction;
import knight.compiler.passes.symbol.model.SymbolProgram;
import knight.compiler.passes.symbol.model.SymbolVariable;

/*
 * File: NameAnalyser.java
 * @author: Mart van der Zalm
 * Date: 2025-04-10
 */
public class NameAnalyser implements ASTVisitor<ASTType>
{
	private SymbolProgram symbolProgram;
	private SymbolClass symbolClass;
	private SymbolFunction symbolFunction;

	private Set<String> hsymbolClass = new HashSet<>();
	private Set<String> hsymbolFunction = new HashSet<>();

	private Scope currentScope;

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

		for (ASTVariable astVariable : astBody.getVariableList()) {
			astVariable.accept(this);
		}

		for (ASTStatement astStatement : astBody.getStatementList()) {
			astStatement.accept(this);
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
					"class " + identifier + " is not declared");
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
			SemanticErrors.addError(astIdentifierType.getToken(), "class " + identifier + " is not declared");
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
			SemanticErrors.addError(astIdentifierExpr.getToken(), "variable " + identifier + " is not declared");
		}

		astIdentifierExpr.setB(symbolVariable);
		return null;
	}

	public void checkVariable(ASTVariable astVariable)
	{
		astVariable.getType().accept(this);
		astVariable.getId().accept(this);
	}

	@Override
	public ASTType visit(ASTVariable astVariable)
	{
		checkVariable(astVariable);
		return null;
	}

	@Override
	public ASTType visit(ASTVariableInit astVariableInit)
	{
		checkVariable(astVariableInit);
		astVariableInit.getExpr().accept(this);
		return null;
	}

	public void checkFunction(ASTFunction astFunction)
	{
		String functionName = astFunction.getFunctionName().getId();

		if (hsymbolFunction.contains(functionName)) {
			return;
		} else {
			hsymbolFunction.add(functionName);
		}

		astFunction.getReturnType().accept(this);

		if (symbolClass == null) {
			symbolFunction = symbolProgram.getFunction(functionName);
		} else {
			symbolFunction = symbolClass.getFunction(functionName);
		}

		astFunction.getFunctionName().setB(symbolFunction);

		for (ASTArgument astArgument : astFunction.getArgumentList()) {
			astArgument.accept(this);
		}

		astFunction.getBody().accept(this);
	}

	@Override
	public ASTType visit(ASTFunction astFunction)
	{
		checkFunction(astFunction);
		symbolFunction = null;
		return null;
	}

	@Override
	public ASTType visit(ASTFunctionReturn astFunctionReturn)
	{
		checkFunction(astFunctionReturn);
		astFunctionReturn.getReturnExpr().accept(this);
		symbolFunction = null;
		return null;
	}

	@Override
	public ASTType visit(ASTProgram astProgram)
	{
		for (ASTClass astClass : astProgram.getClassList()) {
			astClass.accept(this);
		}

		for (ASTFunction astFunction : astProgram.getFunctionList()) {
			astFunction.accept(this);
		}

		for (ASTVariable astVariable : astProgram.getVariableList()) {
			astVariable.accept(this);
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
		String identifier = astClass.getClassName().getId();
		if (hsymbolClass.contains(identifier)) {
			return null;
		} else {
			hsymbolClass.add(identifier);
		}

		symbolClass = symbolProgram.getClass(identifier);
		astClass.getClassName().setB(symbolClass);

		for (ASTProperty astProperty : astClass.getPropertyList()) {
			astProperty.accept(this);
		}

		for (ASTFunction astFunction : astClass.getFunctionList()) {
			astFunction.accept(this);
		}

		hsymbolFunction.clear();
		symbolClass = null;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ASTType visit(ASTArrayLiteral astArrayLiteral)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
