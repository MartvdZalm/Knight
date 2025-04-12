package knight.compiler.passes.symbol;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

import knight.compiler.ast.ASTArgument;
import knight.compiler.ast.ASTArrayAssign;
import knight.compiler.ast.ASTArrayIndexExpr;
import knight.compiler.ast.ASTAssign;
import knight.compiler.ast.ASTBinaryOperation;
import knight.compiler.ast.ASTBody;
import knight.compiler.ast.ASTBooleanType;
import knight.compiler.ast.ASTCallFunctionExpr;
import knight.compiler.ast.ASTCallFunctionStat;
import knight.compiler.ast.ASTClass;
import knight.compiler.ast.ASTConditionalBranch;
import knight.compiler.ast.ASTExpression;
import knight.compiler.ast.ASTFalse;
import knight.compiler.ast.ASTFunction;
import knight.compiler.ast.ASTFunctionReturn;
import knight.compiler.ast.ASTFunctionType;
import knight.compiler.ast.ASTIdentifier;
import knight.compiler.ast.ASTIdentifierExpr;
import knight.compiler.ast.ASTIdentifierType;
import knight.compiler.ast.ASTIfChain;
import knight.compiler.ast.ASTIntArrayType;
import knight.compiler.ast.ASTIntLiteral;
import knight.compiler.ast.ASTIntType;
import knight.compiler.ast.ASTNewArray;
import knight.compiler.ast.ASTNewInstance;
import knight.compiler.ast.ASTNotEquals;
import knight.compiler.ast.ASTPlus;
import knight.compiler.ast.ASTPointerAssign;
import knight.compiler.ast.ASTProgram;
import knight.compiler.ast.ASTProperty;
import knight.compiler.ast.ASTReturnStatement;
import knight.compiler.ast.ASTStatement;
import knight.compiler.ast.ASTStringLiteral;
import knight.compiler.ast.ASTStringType;
import knight.compiler.ast.ASTThis;
import knight.compiler.ast.ASTTrue;
import knight.compiler.ast.ASTType;
import knight.compiler.ast.ASTVariable;
import knight.compiler.ast.ASTVariableInit;
import knight.compiler.ast.ASTVisitor;
import knight.compiler.ast.ASTVoidType;
import knight.compiler.ast.ASTWhile;
import knight.compiler.lexer.Token;
import knight.compiler.passes.symbol.diagnostics.SemanticErrors;
import knight.compiler.passes.symbol.model.SymbolClass;
import knight.compiler.passes.symbol.model.SymbolFunction;
import knight.compiler.passes.symbol.model.SymbolProgram;
import knight.compiler.passes.symbol.model.SymbolVariable;
import knight.compiler.passes.symbol.model.Scope;

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
	public ASTType visit(ASTAssign assign)
	{
		assign.getIdentifier().accept(this);
		assign.getExpr().accept(this);
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
	public ASTType visit(ASTWhile while1)
	{
		while1.getCondition().accept(this);
		while1.getBody().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTIntLiteral intLiteral)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTTrue true1)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTFalse false1)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTNewArray newArray)
	{
		newArray.getArrayLength().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTNewInstance ni)
	{
		String id = ni.getClassName().getId();
		SymbolClass klass = symbolProgram.getClass(id);
		if (klass == null) {
			Token sym = ni.getClassName().getToken();
			SemanticErrors.addError(sym.getRow(), sym.getCol(), "class " + id + " is not declared");
		}

		ni.getClassName().setB(klass);
		return null;
	}

	@Override
	public ASTType visit(ASTCallFunctionExpr cm)
	{
		for (int i = 0; i < cm.getArgumentListSize(); i++) {
			ASTExpression e = cm.getArgumentAt(i);
			e.accept(this);
		}

		return null;
	}

	@Override
	public ASTType visit(ASTCallFunctionStat cm)
	{
		for (int i = 0; i < cm.getArgumentListSize(); i++) {
			ASTExpression e = cm.getArgumentAt(i);
			e.accept(this);
		}
		return null;
	}

	@Override
	public ASTType visit(ASTFunctionType functionType)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTIntType intType)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTStringType stringType)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTVoidType voidType)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTBooleanType booleanType)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTIntArrayType intArrayType)
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
	public ASTType visit(ASTVariable varDeclNoInit)
	{
		checkVariable(varDeclNoInit);
		return null;
	}

	@Override
	public ASTType visit(ASTVariableInit varDeclInit)
	{
		checkVariable(varDeclInit);
		varDeclInit.getExpr().accept(this);
		return null;
	}

	public void checkFunction(ASTFunction functionDecl)
	{
		String functionName = functionDecl.getFunctionName().getId();

		if (hsymbolFunction.contains(functionName)) {
			return;
		} else {
			hsymbolFunction.add(functionName);
		}

		functionDecl.getReturnType().accept(this);

		if (symbolClass == null) {
			symbolFunction = symbolProgram.getFunction(functionName);
		} else {
			symbolFunction = symbolClass.getFunction(functionName);
		}

		functionDecl.getFunctionName().setB(symbolFunction);

		for (int i = 0; i < functionDecl.getArgumentListSize(); i++) {
			functionDecl.getArgumentAt(i).accept(this);
		}

		functionDecl.getBody().accept(this);
	}

	@Override
	public ASTType visit(ASTFunction functionDecl)
	{
		checkFunction(functionDecl);
		symbolFunction = null;
		return null;
	}

	@Override
	public ASTType visit(ASTFunctionReturn functionReturn)
	{
		checkFunction(functionReturn);
		functionReturn.getReturnExpr().accept(this);
		symbolFunction = null;
		return null;
	}

	@Override
	public ASTType visit(ASTProgram program)
	{
		for (int i = 0; i < program.getClassListSize(); i++) {
			program.getClassDeclAt(i).accept(this);
		}

		for (int i = 0; i < program.getFunctionListSize(); i++) {
			program.getFunctionDeclAt(i).accept(this);
		}

		for (int i = 0; i < program.getVariableListSize(); i++) {
			program.getVariableDeclAt(i).accept(this);
		}

		return null;
	}

	@Override
	public ASTType visit(ASTReturnStatement returnStatement)
	{
		returnStatement.getReturnExpr().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTArrayIndexExpr ia)
	{
		ia.getArray().accept(this);
		ia.getIndex().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTArrayAssign aa)
	{
		aa.getId().accept(this);
		aa.getExpression1().accept(this);
		aa.getExpression2().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTStringLiteral stringLiteral)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTClass cd)
	{
		String id = cd.getClassName().getId();
		if (hsymbolClass.contains(id)) {
			return null;
		} else {
			hsymbolClass.add(id);
		}

		symbolClass = symbolProgram.getClass(id);
		cd.getClassName().setB(symbolClass);

		for (int i = 0; i < cd.getPropertyListSize(); i++) {
			cd.getPropertyDeclAt(i).accept(this);;
		}

		for (int i = 0; i < cd.getFunctionListSize(); i++) {
			cd.getFunctionDeclAt(i).accept(this);;
		}

		hsymbolFunction.clear();

		symbolClass = null;
		return null;
	}

	@Override
	public ASTType visit(ASTPointerAssign pointerAssign)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTThis astThis)
	{
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
	public ASTType visit(ASTBinaryOperation astBinaryOperation)
	{
		astBinaryOperation.getLeftSide().accept(this);
		astBinaryOperation.getRightSide().accept(this);
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
}
