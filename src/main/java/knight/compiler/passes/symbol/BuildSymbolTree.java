package knight.compiler.passes.symbol;

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
import knight.compiler.ast.ASTSkip;
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

/*
 * File: BuildSymbolProgramVisitor.java
 * @author: Mart van der Zalm
 * Date: 2025-04-10
 */
public class BuildSymbolTree implements ASTVisitor<ASTType>
{
	private SymbolProgram symbolProgram;
	private SymbolClass symbolClass;
	private SymbolFunction symbolFunction;
	private String mKlassId;

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
		for (int i = 0; i < astProgram.getClassListSize(); i++) {
			astProgram.getClassDeclAt(i).accept(this);
		}

		for (int i = 0; i < astProgram.getFunctionListSize(); i++) {
			astProgram.getFunctionDeclAt(i).accept(this);
		}

		for (int i = 0; i < astProgram.getVariableListSize(); i++) {
			astProgram.getVariableDeclAt(i).accept(this);
		}

		return null;
	}

	@Override
	public ASTType visit(ASTClass astClass)
	{
		String className = astClass.getClassName().getId();

		if (!symbolProgram.addClass(className, null)) {
			addError(astClass.getToken(), "Class " + className + " is already defined!");
			symbolClass = new SymbolClass(className, null);
		} else {
			symbolClass = symbolProgram.getClass(className);
		}

		for (int i = 0; i < astClass.getPropertyListSize(); i++) {
			astClass.getPropertyDeclAt(i).accept(this);
		}

		for (int i = 0; i < astClass.getFunctionListSize(); i++) {
			astClass.getFunctionDeclAt(i).accept(this);
		}

		symbolClass = null;
		return null;
	}

	@Override
	public ASTType visit(ASTProperty astProperty)
	{
		if (symbolClass == null) {
			addError(astProperty.getToken(), "Property declared outside of class");
			return null;
		}

		ASTType type = astProperty.getType().accept(this);
		String identifier = astProperty.getId().getId();

		if (!symbolClass.addVariable(identifier, type)) {
			Token token = astProperty.getId().getToken();
			addError(token, "Property " + identifier + " already defined in class " + symbolClass.getId());
		}

		return null;
	}

	public void checkFunction(ASTFunction astFunction)
	{
		ASTType type = astFunction.getReturnType().accept(this);
		String identifier = astFunction.getFunctionName().getId();

		if (symbolClass == null) {
			if (!symbolProgram.addFunction(identifier, type)) {
				addError(astFunction.getToken(), "Function " + identifier + " already defined");
			} else {
				symbolFunction = symbolProgram.getFunction(identifier);
			}
		} else {
			if (!symbolClass.addFunction(identifier, type)) {
				addError(astFunction.getToken(),
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
	public ASTType visit(ASTSkip skip)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTAssign assign)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTBody astBody)
	{
		symbolFunction.startNewScope();

		for (int i = 0; i < astBody.getVariableListSize(); i++) {
			astBody.getVariableAt(i).accept(this);
			System.out.println(astBody.getVariableAt(i).getId());
		}

		for (int i = 0; i < astBody.getStatementListSize(); i++) {
			astBody.getStatementAt(i).accept(this);
		}

		symbolFunction.endCurrentScope();
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
	public ASTType visit(ASTIdentifierExpr identifierExpr)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTNewArray newArray)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTNewInstance newInstance)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTCallFunctionExpr astCallFunctionExpr)
	{
		for (int i = 0; i < astCallFunctionExpr.getArgumentListSize(); i++) {
			astCallFunctionExpr.getArgumentAt(i).accept(this);
		}
		return null;
	}

	@Override
	public ASTType visit(ASTCallFunctionStat astCallFunctionStat)
	{
		for (int i = 0; i < astCallFunctionStat.getArgumentListSize(); i++) {
			astCallFunctionStat.getArgumentAt(i).accept(this);
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
	public ASTType visit(ASTFunctionType functionType)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTIntType intType)
	{
		return intType;
	}

	@Override
	public ASTType visit(ASTStringType stringType)
	{
		return stringType;
	}

	@Override
	public ASTType visit(ASTVoidType voidType)
	{
		return voidType;
	}

	@Override
	public ASTType visit(ASTBooleanType booleanType)
	{
		return booleanType;
	}

	@Override
	public ASTType visit(ASTIntArrayType intArrayType)
	{
		return intArrayType;
	}

	@Override
	public ASTType visit(ASTIdentifierType astIdentifierType)
	{
		String identifier = astIdentifierType.getId();

		if (identifier != null && identifier.equals(mKlassId)) {
			addError(astIdentifierType.getToken(),
					"Class " + identifier + " cannot be used as a type in class " + symbolClass.getId());
		}

		return astIdentifierType;
	}

	public void checkIfVariableExist(ASTVariable astVariable)
	{
		ASTType type = astVariable.getType().accept(this);
		String identifier = astVariable.getId().getId();

		if (symbolFunction != null) {
			if (!symbolFunction.addVariable(identifier, type)) {
				Token token = astVariable.getId().getToken();
				addError(token, "Variable " + identifier + " already defined in function " + symbolFunction.getId());
			}
		} else if (symbolClass != null) {
			if (!symbolClass.addVariable(identifier, type)) {
				Token token = astVariable.getId().getToken();
				addError(token, "Variable " + identifier + " already defined in class " + symbolClass.getId());
			}
		} else {
			if (!symbolProgram.addVariable(identifier, type)) {
				Token token = astVariable.getId().getToken();
				addError(token, "Variable " + identifier + " already defined");
			}
		}
	}

	@Override
	public ASTType visit(ASTVariable varDecl)
	{
		checkIfVariableExist(varDecl);
		return null;
	}

	@Override
	public ASTType visit(ASTVariableInit varDeclInit)
	{
		checkIfVariableExist(varDeclInit);
		return null;
	}

	@Override
	public ASTType visit(ASTIdentifier identifier)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTArrayIndexExpr indexArray)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTArrayAssign arrayAssign)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTStringLiteral stringLiteral)
	{
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
	public ASTType visit(ASTIfChain astIfChain)
	{
		for (int i = 0; i < astIfChain.getBranchListSize(); i++) {
			astIfChain.getBranchAt(i).accept(this);
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
	public ASTType visit(ASTBinaryOperation astBinaryOperation)
	{
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
				addError(token, "Argument " + identifier + " already defined in function " + symbolFunction.getId()
						+ " in class " + classId);
			}
		} else {
			addError(astArgument.getToken(), "Argument declared outside of a function");
		}

		return null;
	}

	@Override
	public ASTType visit(ASTNotEquals astNotEquals)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTPlus astPlus)
	{
		return null;
	}

	public static void addError(Token token, String errorText)
	{
		SemanticErrors.addError(token.getRow(), token.getCol(), errorText);
	}
}
