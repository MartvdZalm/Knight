package knight.compiler.visitor;

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
import knight.compiler.ast.ASTPointerAssign;
import knight.compiler.ast.ASTProgram;
import knight.compiler.ast.ASTProperty;
import knight.compiler.ast.ASTReturnStatement;
import knight.compiler.ast.ASTSkip;
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
import knight.compiler.semantics.SemanticErrors;
import knight.compiler.symbol.SymbolClass;
import knight.compiler.symbol.SymbolFunction;
import knight.compiler.symbol.SymbolProgram;
import knight.compiler.symbol.SymbolVariable;

/*
 * File: NameAnalyserTreeVisitor.java
 * @author: Mart van der Zalm
 * Date: 2025-04-10
 */
public class NameAnalyserTree implements ASTVisitor<ASTType>
{
	private SymbolProgram symbolProgram;
	private SymbolClass symbolClass;
	private SymbolFunction symbolFunction;

	private Set<String> hsymbolClass = new HashSet<>();
	private Set<String> hsymbolFunction = new HashSet<>();

	public NameAnalyserTree(SymbolProgram symbolProgram)
	{
		this.symbolProgram = symbolProgram;
	}

	@Override
	public ASTType visit(ASTAssign assign)
	{
		assign.getId().accept(this);
		assign.getExpr().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTBody body)
	{
		for (int i = 0; i < body.getVariableListSize(); i++) {
			ASTVariable variable = body.getVariableAt(i);
			variable.accept(this);
		}

		for (int i = 0; i < body.getStatementListSize(); i++) {
			ASTStatement statement = body.getStatementAt(i);
			statement.accept(this);
		}

		return null;
	}

//	@Override
//	public ASTType visit(ASTIfThenElse ifThenElse)
//	{
//		ifThenElse.getExpr().accept(this);
//		ifThenElse.getThen().accept(this);
//		ifThenElse.getElze().accept(this);
//		return null;
//	}

	@Override
	public ASTType visit(ASTSkip skip)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTWhile while1)
	{
		while1.getExpr().accept(this);
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
			addError(sym.getRow(), sym.getCol(), "class " + id + " is not declared");
		}

		ni.getClassName().setB(klass);
		return null;
	}

	@Override
	public ASTType visit(ASTCallFunctionExpr cm)
	{
		/*
		 * The call function expression can be written like this
		 * 'object.functionName()'. Here is object the instancename, but if there is no
		 * instancename 'functionName()', the instancename will be null. So This check
		 * needs to be done.
		 */
//		if (cm.getInstanceName() != null) {
//			cm.getInstanceName().accept(this);
//		}

		for (int i = 0; i < cm.getArgumentListSize(); i++) {
			ASTExpression e = cm.getArgumentAt(i);
			e.accept(this);
		}

		return null;
	}

	@Override
	public ASTType visit(ASTCallFunctionStat cm)
	{
		for (int i = 0; i < cm.getArgExprListSize(); i++) {
			ASTExpression e = cm.getArgExprAt(i);
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
	public ASTType visit(ASTIdentifier identifier)
	{
		String id = identifier.getId();
		SymbolVariable var = symbolProgram.getVariable(id, symbolClass, symbolFunction);

		if (var == null) {
			Token sym = identifier.getToken();
			addError(sym.getRow(), sym.getCol(), "variable " + id + " is not declared");
		}

		identifier.setB(var);
		return null;
	}

	@Override
	public ASTType visit(ASTIdentifierType identifierType)
	{
		String id = identifierType.getId();
		SymbolClass klass = symbolProgram.getClass(id);
		if (klass == null) {
			Token sym = identifierType.getToken();
			addError(sym.getRow(), sym.getCol(), "class " + id + " is not declared");
		}

		identifierType.setB(klass);
		return null;
	}

	@Override
	public ASTType visit(ASTIdentifierExpr identifierExpr)
	{
		String id = identifierExpr.getId();
		SymbolVariable var = symbolProgram.getVariable(id, symbolClass, symbolFunction);
		if (var == null) {
			Token sym = identifierExpr.getToken();
			addError(sym.getRow(), sym.getCol(), "variable " + id + " is not declared");
		}

		identifierExpr.setB(var);
		return null;
	}

	public void checkVariable(ASTVariable varDecl)
	{
		String id = varDecl.getId().getId();

		varDecl.getType().accept(this);
		varDecl.getId().accept(this);
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

//	@Override
//	public ASTType visit(ASTArgument ad)
//	{
//		ad.getId().accept(this);
//		return null;
//	}

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

	public static void addError(int line, int col, String errorText)
	{
		SemanticErrors.addError(line, col, errorText);
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
	public ASTType visit(ASTProperty property)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTIfChain ifChain)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ASTType visit(ASTBinaryOperation astBinaryOperation)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ASTType visit(ASTConditionalBranch astConditionalBranch)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ASTType visit(ASTArgument astArgument)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
