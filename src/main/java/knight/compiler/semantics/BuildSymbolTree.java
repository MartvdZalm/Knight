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
import knight.compiler.lexer.Token;
import knight.compiler.semantics.diagnostics.DiagnosticReporter;
import knight.compiler.semantics.model.*;

public class BuildSymbolTree implements ASTVisitor<ASTType>
{
	private SymbolProgram symbolProgram;
	private SymbolClass symbolClass;
	private SymbolFunction symbolFunction;
	private Scope currentScope;

	public BuildSymbolTree()
	{
		this.symbolProgram = new SymbolProgram();
	}

	public SymbolProgram getSymbolProgram()
	{
		return symbolProgram;
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

	@Override
	public ASTType visit(ASTProgram astProgram)
	{
		for (ASTImport astImport : astProgram.getImportList()) {
			astImport.accept(this);
		}

		for (AST node : astProgram.getNodeList()) {
			node.accept(this);
		}

		return null;
	}

	@Override
	public ASTType visit(ASTClass astClass)
	{
		String className = astClass.getClassName().getId();

		if (!symbolProgram.addClass(className, null)) {
			DiagnosticReporter.error(astClass.getToken(), "Class " + className + " is already defined!");
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
			DiagnosticReporter.error(astProperty.getToken(), "Property declared outside of class");
			return null;
		}

		ASTType astType = astProperty.getType().accept(this);
		String astIdentifier = astProperty.getId().getId();

		if (!symbolClass.addVariable(astIdentifier, astType)) {
			Token token = astProperty.getId().getToken();
			DiagnosticReporter.error(token,
					"Property " + astIdentifier + " already defined in class " + symbolClass.getId());
		}

		return null;
	}

	@Override
	public ASTType visit(ASTFunction astFunction)
	{
		currentScope = new Scope(currentScope);

		ASTType astType = astFunction.getReturnType().accept(this);
		String identifier = astFunction.getFunctionName().getId();

		if (symbolClass == null) {
			if (!symbolProgram.addFunction(identifier, astType)) {
				DiagnosticReporter.error(astFunction.getToken(), "Function " + identifier + " already defined");
			} else {
				symbolFunction = symbolProgram.getFunction(identifier);
			}
		} else {
			if (!symbolClass.addFunction(identifier, astType)) {
				DiagnosticReporter.error(astFunction.getToken(),
						"Function " + identifier + " already defined in class " + symbolClass.getId());
			} else {
				symbolFunction = symbolClass.getFunction(identifier);
			}
		}

		for (int i = 0; i < astFunction.getArgumentListSize(); i++) {
			astFunction.getArgumentAt(i).accept(this);
		}

		astFunction.getBody().accept(this);

		astFunction.setScope(currentScope);
		currentScope = currentScope.getParent();

		symbolFunction = null;
		return null;
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
		currentScope = new Scope(currentScope);

		for (AST node : astBody.getNodesList()) {
			node.accept(this);
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
		String astIdentifier = astIdentifierExpr.getId();

		SymbolVariable symbolVariable = null;

		if (symbolFunction != null) {
			symbolVariable = currentScope.getVariable(astIdentifier);
		} else {
			symbolVariable = symbolProgram.getVariable(astIdentifier, symbolClass, symbolFunction);
		}

		if (symbolVariable == null) {
			DiagnosticReporter.error(astIdentifierExpr.getToken(), "Variable " + astIdentifier + " not declared");
		}

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
		String className = astNewInstance.getClassName().getId();
		if (!symbolProgram.containsClass(className)) {
			DiagnosticReporter.error(astNewInstance.getClassName().getToken(), "Class " + className + " not found");
		}

		for (ASTArgument astArgument : astNewInstance.getArguments()) {
			astArgument.accept(this);
		}

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
	public ASTType visit(ASTIdentifierType astIdentifierType)
	{
		return astIdentifierType;
	}

	private void checkIfVariableExist(ASTVariable astVariable)
	{
		ASTType type = astVariable.getType().accept(this);
		String identifier = astVariable.getId().getId();

		if (symbolFunction != null) {
			if (!currentScope.addVariable(identifier, type)) {
				Token token = astVariable.getId().getToken();
				DiagnosticReporter.error(token,
						"Variable " + identifier + " already defined in function " + symbolFunction.getId());
			}
		} else if (symbolClass != null) {
			if (!symbolClass.addVariable(identifier, type)) {
				Token token = astVariable.getId().getToken();
				DiagnosticReporter.error(token,
						"Variable " + identifier + " already defined in class " + symbolClass.getId());
			}
		} else {
			if (!symbolProgram.addVariable(identifier, type)) {
				Token token = astVariable.getId().getToken();
				DiagnosticReporter.error(token, "Variable " + identifier + " already defined");
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
			if (!currentScope.addVariable(identifier, type)) {
				Token token = astArgument.getIdentifier().getToken();
				DiagnosticReporter.error(token,
						"Argument " + identifier + " already defined in function " + symbolFunction.getId());
			}

			if (!symbolFunction.addParam(identifier, type)) {
				Token token = astArgument.getIdentifier().getToken();
				String classId = symbolClass != null ? symbolClass.getId() : "global";
				DiagnosticReporter.error(token, "Argument " + identifier + " already defined in function "
						+ symbolFunction.getId() + " in class " + classId);
			}
		} else {
			DiagnosticReporter.error(astArgument.getToken(), "Argument declared outside of a function");
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

	@Override
	public ASTType visit(ASTStringArrayType astStringArrayType)
	{
		return astStringArrayType;
	}

	@Override
	public ASTType visit(ASTArrayLiteral astArrayLiteral)
	{
		for (ASTExpression element : astArrayLiteral.getExpressionList()) {
			element.accept(this);
		}
		return null;
	}

	@Override
	public ASTType visit(ASTForeach astForeach)
	{
		ASTType iterableType = astForeach.getIterable().accept(this);
		if (iterableType != null && !(iterableType instanceof ASTIntArrayType)
				&& !(iterableType instanceof ASTStringArrayType)) {
			DiagnosticReporter.error(astForeach.getIterable().getToken(), "Foreach loop requires array type");
		}

		astForeach.getVariable().accept(this);
		astForeach.getBody().accept(this);

		return null;
	}

	@Override
	public ASTType visit(ASTLambda astLambda)
	{
		for (ASTArgument arg : astLambda.getArgumentList()) {
			arg.accept(this);
		}

		astLambda.getReturnType().accept(this);
		astLambda.getBody().accept(this);

		return null;
	}

	@Override
	public ASTType visit(ASTImport astImport)
	{
//		Optional<Library> library = LibraryManager.findLibrary(astImport.getLibrary().toString());
//		if (!library.isPresent()) {
//			DiagnosticReporter.error(astImport.getToken(), "Library " + astImport.getLibrary() + " not found.");
//			return null;
//		}
		return null;
	}

	@Override
	public ASTType visit(ASTParameterizedType astParameterizedType)
	{
		return astParameterizedType;
	}

	@Override
	public ASTType visit(ASTInterface astInterface)
	{
		String interfaceName = astInterface.getName().getId();

		if (!symbolProgram.addInterface(interfaceName)) {
			DiagnosticReporter.error(astInterface.getToken(), "Interface " + interfaceName + " already defined");
		}

		SymbolInterface symbolInterface = symbolProgram.getInterface(interfaceName);

		for (ASTIdentifier extended : astInterface.getExtendedInterfaces()) {
			symbolInterface.addExtendedInterface(extended.getId());
		}

		for (ASTFunction method : astInterface.getMethodSignatures()) {
			ASTType returnType = method.getReturnType().accept(this);
			symbolInterface.addFunction(method.getFunctionName().toString(), returnType);
		}

		return null;
	}
}
