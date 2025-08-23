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
import knight.compiler.lexer.Token;
import knight.compiler.semantics.diagnostics.DiagnosticReporter;
import knight.compiler.semantics.model.*;
import knight.compiler.semantics.utils.ScopeManager;

public class BuildSymbolTree implements ASTVisitor<ASTType>
{
	private final SymbolProgram symbolProgram;
	private final ScopeManager scopeManager;

	public BuildSymbolTree()
	{
		this.symbolProgram = new SymbolProgram();
		this.scopeManager = new ScopeManager();
	}

	public BuildSymbolTree(SymbolProgram symbolProgram)
	{
		this.symbolProgram = symbolProgram;
		this.scopeManager = new ScopeManager();
	}

	public SymbolProgram getSymbolProgram()
	{
		return symbolProgram;
	}

	@Override
	public ASTType visit(ASTProgram astProgram)
	{
		for (ASTImport astImport : astProgram.getImports()) {
			astImport.accept(this);
		}

		for (AST node : astProgram.getNodes()) {
			node.accept(this);
		}

		return null;
	}

	@Override
	public ASTType visit(ASTClass astClass)
	{
		String className = astClass.getIdentifier().getName();
		String parentClassName = astClass.getExtendsClass() != null ? astClass.getExtendsClass().getName() : null;

		if (!symbolProgram.addClass(className, parentClassName)) {
			DiagnosticReporter.error(astClass.getToken(), "Class " + className + " is already defined!");
			return null;
		}

		SymbolClass symbolClass = symbolProgram.getClass(className);
		scopeManager.enterClass(symbolClass);

		try {
			for (ASTIdentifier implemented : astClass.getImplementsInterfaces()) {
				symbolClass.addImplementedInterface(implemented.getName());
			}

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
	public ASTType visit(ASTProperty astProperty)
	{
		if (!scopeManager.isInClass()) {
			DiagnosticReporter.error(astProperty.getToken(), "Property declared outside of class");
			return null;
		}

		ASTType astType = astProperty.getType().accept(this);
		String propertyName = astProperty.getIdentifier().getName();
		SymbolClass currentClass = scopeManager.getCurrentClass();

		if (!currentClass.addProperty(propertyName, astType)) {
			DiagnosticReporter.error(astProperty.getIdentifier(),
					"Property " + propertyName + " already defined in class " + currentClass.getName());
		}

		return null;
	}

	@Override
	public ASTType visit(ASTFunction astFunction)
	{
		ASTType returnType = astFunction.getReturnType().accept(this);
		String functionName = astFunction.getIdentifier().getName();

		SymbolFunction symbolFunction;
		if (scopeManager.isInClass()) {
			SymbolClass currentClass = scopeManager.getCurrentClass();
			if (!currentClass.addFunction(functionName, returnType)) {
				DiagnosticReporter.error(astFunction.getToken(),
						"Function " + functionName + " already defined in class " + currentClass.getName());
				return null;
			}
			symbolFunction = currentClass.getFunction(functionName);
		} else {
			if (!symbolProgram.addGlobalFunction(functionName, returnType)) {
				DiagnosticReporter.error(astFunction.getToken(), "Function " + functionName + " already defined");
				return null;
			}
			symbolFunction = symbolProgram.getGlobalFunction(functionName);
		}

		scopeManager.enterFunction(symbolFunction);
		astFunction.setScope(scopeManager.getCurrentScope());

		try {
			for (ASTArgument astArgument : astFunction.getArguments()) {
				astArgument.accept(this);
			}

			astFunction.getBody().accept(this);

		} finally {
			scopeManager.exitFunction();
		}

		return null;
	}

	@Override
	public ASTType visit(ASTArgument astArgument)
	{
		ASTType type = astArgument.getType().accept(this);
		String paramName = astArgument.getIdentifier().getName();

		if (!scopeManager.isInFunction()) {
			DiagnosticReporter.error(astArgument, "Parameter declared outside of function");
			return null;
		}

		Scope currentScope = scopeManager.getCurrentScope();
		SymbolFunction currentFunction = scopeManager.getCurrentFunction();

		if (!currentScope.addVariable(paramName, type)) {
			DiagnosticReporter.error(astArgument, "Parameter " + paramName + " already declared");
			return null;
		}

		if (!currentFunction.addParameter(paramName, type)) {
			DiagnosticReporter.error(astArgument, "Parameter " + paramName + " already declared in function");
		}

		return null;
	}

	@Override
	public ASTType visit(ASTBody astBody)
	{
		scopeManager.enterBlock();
		astBody.setScope(scopeManager.getCurrentScope());

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
	public ASTType visit(ASTVariable astVariable)
	{
		ASTType type = astVariable.getType().accept(this);
		String varName = astVariable.getIdentifier().getName();

		if (scopeManager.isInFunction()) {
			Scope currentScope = scopeManager.getCurrentScope();
			if (!currentScope.addVariable(varName, type)) {
				DiagnosticReporter.error(astVariable, "Variable " + varName + " already declared in this scope");
			}
		} else if (scopeManager.isInClass()) {
			DiagnosticReporter.error(astVariable, "Variables must be declared as properties in class scope");
		} else {
			if (!symbolProgram.addGlobalVariable(varName, type)) {
				DiagnosticReporter.error(astVariable, "Global variable " + varName + " already declared");
			}
		}

		return null;
	}

	@Override
	public ASTType visit(ASTVariableInit astVariableInit)
	{
		visit((ASTVariable) astVariableInit);

		astVariableInit.getExpression().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTFieldAssign astFieldAssign)
	{
		astFieldAssign.getInstance().accept(this);
		astFieldAssign.getField().accept(this);
		astFieldAssign.getValue().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTInterface astInterface)
	{
		String interfaceName = astInterface.getIdentifier().getName();

		if (!symbolProgram.addInterface(interfaceName)) {
			DiagnosticReporter.error(astInterface, "Interface " + interfaceName + " already defined");
			return null;
		}

		SymbolInterface symbolInterface = symbolProgram.getInterface(interfaceName);

		for (ASTIdentifier extended : astInterface.getExtendedInterfaces()) {
			symbolInterface.addExtendedInterface(extended.getName());
		}

		for (ASTFunction astFunction : astInterface.getFunctions()) {
			ASTType returnType = astFunction.getReturnType().accept(this);
			String functionName = astFunction.getIdentifier().getName();

			if (!symbolInterface.addFunction(functionName, returnType)) {
				DiagnosticReporter.error(astFunction,
						"Function " + functionName + " already defined in interface " + interfaceName);
			}
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
	public ASTType visit(ASTWhile astWhile)
	{
		astWhile.getCondition().accept(this);
		astWhile.getBody().accept(this);
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
		String className = astNewInstance.getClassName().getName();

		for (ASTArgument astArgument : astNewInstance.getArguments()) {
			astArgument.accept(this);
		}

		return null;
	}

	@Override
	public ASTType visit(ASTCallFunctionExpr astCallFunctionExpr)
	{
		for (ASTExpression astExpression : astCallFunctionExpr.getArguments()) {
			astExpression.accept(this);
		}
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
	public ASTType visit(ASTReturnStatement astReturnStatement)
	{
		if (astReturnStatement.getExpression() != null) {
			astReturnStatement.getExpression().accept(this);
		}
		return null;
	}

	@Override
	public ASTType visit(ASTForEach astForEach)
	{
		ASTType iterableType = astForEach.getIterable().accept(this);
		if (iterableType != null && !(iterableType instanceof ASTIntArrayType)
				&& !(iterableType instanceof ASTStringArrayType)) {
			DiagnosticReporter.error(astForEach, "Foreach loop requires array type");
		}

		astForEach.getVariable().accept(this);
		astForEach.getBody().accept(this);

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
	public ASTType visit(ASTArrayLiteral astArrayLiteral)
	{
		for (ASTExpression element : astArrayLiteral.getExpressions()) {
			element.accept(this);
		}
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
	public ASTType visit(ASTLambda astLambda)
	{
		SymbolFunction lambdaFunction = new SymbolFunction("lambda", astLambda.getReturnType());
		scopeManager.enterFunction(lambdaFunction);

		try {
			for (ASTArgument arg : astLambda.getArguments()) {
				arg.accept(this);
			}

			astLambda.getReturnType().accept(this);
			astLambda.getBody().accept(this);
		} finally {
			scopeManager.exitFunction();
		}

		return null;
	}

	@Override
	public ASTType visit(ASTImport astImport)
	{
		return null;
	}
}
