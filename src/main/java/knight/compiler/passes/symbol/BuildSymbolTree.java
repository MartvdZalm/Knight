package knight.compiler.passes.symbol;

import knight.compiler.ast.ASTAnd;
import knight.compiler.ast.ASTArgument;
import knight.compiler.ast.ASTArrayAssign;
import knight.compiler.ast.ASTArrayIndexExpr;
import knight.compiler.ast.ASTAssign;
import knight.compiler.ast.ASTBody;
import knight.compiler.ast.ASTBooleanType;
import knight.compiler.ast.ASTCallFunctionExpr;
import knight.compiler.ast.ASTCallFunctionStat;
import knight.compiler.ast.ASTClass;
import knight.compiler.ast.ASTConditionalBranch;
import knight.compiler.ast.ASTDivision;
import knight.compiler.ast.ASTEquals;
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
import knight.compiler.ast.ASTStatement;
import knight.compiler.ast.ASTExpression;
import knight.compiler.ast.ASTModulus;
import knight.compiler.ast.ASTNewArray;
import knight.compiler.ast.ASTNewInstance;
import knight.compiler.ast.ASTNotEquals;
import knight.compiler.ast.ASTOr;
import knight.compiler.ast.ASTPlus;
import knight.compiler.ast.ASTPointerAssign;
import knight.compiler.ast.ASTProgram;
import knight.compiler.ast.ASTProperty;
import knight.compiler.ast.ASTReturnStatement;
import knight.compiler.ast.ASTStringLiteral;
import knight.compiler.ast.ASTStringType;
import knight.compiler.ast.ASTThis;
import knight.compiler.ast.ASTTimes;
import knight.compiler.ast.ASTTrue;
import knight.compiler.ast.ASTType;
import knight.compiler.ast.ASTVariable;
import knight.compiler.ast.ASTVariableInit;
import knight.compiler.ast.ASTVisitor;
import knight.compiler.ast.ASTVoidType;
import knight.compiler.ast.ASTWhile;
import knight.compiler.lexer.Token;
import knight.compiler.passes.symbol.diagnostics.SemanticErrors;
import knight.compiler.passes.symbol.model.Scope;
import knight.compiler.passes.symbol.model.SymbolClass;
import knight.compiler.passes.symbol.model.SymbolFunction;
import knight.compiler.passes.symbol.model.SymbolProgram;

/*
 * File: BuildSymbolTree.java
 * @author: Mart van der Zalm
 * Date: 2025-04-10
 */
public class BuildSymbolTree implements ASTVisitor<ASTType>
{
	private SymbolProgram symbolProgram;
	private SymbolClass symbolClass;
	private SymbolFunction symbolFunction;
	private Scope currentScope;

	public BuildSymbolTree()
	{
		symbolProgram = new SymbolProgram();
	}

	public SymbolProgram getSymbolProgram()
	{
		return symbolProgram;
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
	public ASTType visit(ASTClass astClass)
	{
		String className = astClass.getClassName().getId();

		if (!symbolProgram.addClass(className, null)) {
			SemanticErrors.addError(astClass.getToken(), "Class " + className + " is already defined!");
			symbolClass = new SymbolClass(className, null);
		} else {
			symbolClass = symbolProgram.getClass(className);
		}

		for (ASTProperty astProperty : astClass.getPropertyList()) {
			astProperty.accept(this);
		}

		for (ASTFunction astFunction : astClass.getFunctionList()) {
			astFunction.accept(this);
		}

		symbolClass = null;
		return null;
	}

	@Override
	public ASTType visit(ASTProperty astProperty)
	{
		if (symbolClass == null) {
			SemanticErrors.addError(astProperty.getToken(), "Property declared outside of class");
			return null;
		}

		ASTType type = astProperty.getType().accept(this);
		String identifier = astProperty.getId().getId();

		if (!symbolClass.addVariable(identifier, type)) {
			Token token = astProperty.getId().getToken();
			SemanticErrors.addError(token,
					"Property " + identifier + " already defined in class " + symbolClass.getId());
		}

		return null;
	}

	public void checkFunction(ASTFunction astFunction)
	{
		ASTType type = astFunction.getReturnType().accept(this);
		String identifier = astFunction.getFunctionName().getId();

		if (symbolClass == null) {
			if (!symbolProgram.addFunction(identifier, type)) {
				SemanticErrors.addError(astFunction.getToken(), "Function " + identifier + " already defined");
			} else {
				symbolFunction = symbolProgram.getFunction(identifier);
			}
		} else {
			if (!symbolClass.addFunction(identifier, type)) {
				SemanticErrors.addError(astFunction.getToken(),
						"Function " + identifier + " already defined in class " + symbolClass.getId());
			} else {
				symbolFunction = symbolClass.getFunction(identifier);
			}
		}

		for (int i = 0; i < astFunction.getArgumentListSize(); i++) {
			astFunction.getArgumentAt(i).accept(this);
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
	public ASTType visit(ASTAssign astAssign)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTBody astBody)
	{
		currentScope = new Scope(currentScope);

		for (ASTVariable astVariable : astBody.getVariableList()) {
			astVariable.accept(this);
		}

		for (ASTStatement astStatement : astBody.getStatementList()) {
			astStatement.accept(this);
		}

		astBody.setScope(currentScope);
		currentScope = currentScope.getParent();
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
	public ASTType visit(ASTIdentifierExpr astIdentifierExpr)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTNewArray astNewArray)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTNewInstance astNewInstance)
	{
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
	public ASTType visit(ASTReturnStatement astReturnStatement)
	{
		if (astReturnStatement.getReturnExpr() != null) {
			astReturnStatement.getReturnExpr().accept(this);
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
	public ASTType visit(ASTIdentifierType astIdentifierType)
	{
		String identifier = astIdentifierType.getId();
		return astIdentifierType;
	}

	public void checkIfVariableExist(ASTVariable astVariable)
	{
		ASTType type = astVariable.getType().accept(this);
		String identifier = astVariable.getId().getId();

		if (symbolFunction != null) {
			if (!currentScope.addVariable(identifier, type)) {
				Token token = astVariable.getId().getToken();
				SemanticErrors.addError(token,
						"Variable " + identifier + " already defined in function " + symbolFunction.getId());
			}
		} else if (symbolClass != null) {
			if (!symbolClass.addVariable(identifier, type)) {
				Token token = astVariable.getId().getToken();
				SemanticErrors.addError(token,
						"Variable " + identifier + " already defined in class " + symbolClass.getId());
			}
		} else {
			if (!symbolProgram.addVariable(identifier, type)) {
				Token token = astVariable.getId().getToken();
				SemanticErrors.addError(token, "Variable " + identifier + " already defined");
			}
		}
	}

	@Override
	public ASTType visit(ASTVariable astVariable)
	{
		checkIfVariableExist(astVariable);
		astVariable.getId().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTVariableInit astVariableInit)
	{
		checkIfVariableExist(astVariableInit);
		astVariableInit.getId().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTIdentifier astIdentifier)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTArrayIndexExpr astIndexArray)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTArrayAssign astArrayAssign)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTStringLiteral astStringLiteral)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTPointerAssign astPointerAssign)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTThis astThis)
	{
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
		ASTType type = astArgument.getType().accept(this);
		String identifier = astArgument.getIdentifier().getId();

		if (symbolFunction != null) {
			if (!symbolFunction.addParam(identifier, type)) {
				Token token = astArgument.getIdentifier().getToken();
				String classId = symbolClass != null ? symbolClass.getId() : "global";
				SemanticErrors.addError(token, "Argument " + identifier + " already defined in function "
						+ symbolFunction.getId() + " in class " + classId);
			}
		} else {
			SemanticErrors.addError(astArgument.getToken(), "Argument declared outside of a function");
		}

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
}
