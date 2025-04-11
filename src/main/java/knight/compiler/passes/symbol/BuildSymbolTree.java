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
	public ASTType visit(ASTClass classDecl)
	{
		String className = classDecl.getClassName().getId();

		if (!symbolProgram.addClass(className, null)) {
			Token token = classDecl.getToken();
			addError(token.getRow(), token.getCol(), "Class " + className + " is already defined!");
			symbolClass = new SymbolClass(className, null);
		} else {
			symbolClass = symbolProgram.getClass(className);
		}

		for (int i = 0; i < classDecl.getPropertyListSize(); i++) {
			classDecl.getPropertyDeclAt(i).accept(this);
		}

		for (int i = 0; i < classDecl.getFunctionListSize(); i++) {
			classDecl.getFunctionDeclAt(i).accept(this);
		}

		symbolClass = null;
		return null;
	}

	public void checkFunction(ASTFunction funcDecl)
	{
		ASTType type = funcDecl.getReturnType().accept(this);
		String id = funcDecl.getFunctionName().getId();

		if (symbolClass == null) {
			if (!symbolProgram.addFunction(id, type)) {
				Token tok = funcDecl.getToken();
				addError(tok.getRow(), tok.getCol(), "Function " + id + " already defined");
			} else {
				symbolFunction = symbolProgram.getFunction(id);
			}
		} else {
			if (!symbolClass.addFunction(id, type)) {
				Token tok = funcDecl.getToken();
				addError(tok.getRow(), tok.getCol(),
						"Function " + id + " already defined in class " + symbolClass.getId());
			} else {
				symbolFunction = symbolClass.getFunction(id);
			}
		}

		for (int i = 0; i < funcDecl.getArgumentListSize(); i++) {
			funcDecl.getArgumentAt(i).accept(this);
		}

//		for (int i = 0; i < funcDecl.getVariableListSize(); i++) {
//			funcDecl.getVariableDeclAt(i).accept(this);
//		}
//
//		for (int i = 0; i < funcDecl.getStatementListSize(); i++) {
//			funcDecl.getStatementDeclAt(i).accept(this);
//		}
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
	public ASTType visit(ASTBody body)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTWhile while1)
	{
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
	public ASTType visit(ASTCallFunctionExpr callFunctionExpr)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTCallFunctionStat callFunctionStat)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTReturnStatement returnStatement)
	{
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
	public ASTType visit(ASTIdentifierType identifierType)
	{
		String id = identifierType.getId();

		if (id != null && id.equals(mKlassId)) {
			Token tok = identifierType.getToken();
			addError(tok.getRow(), tok.getCol(),
					"Class " + id + " cannot be used as a type in class " + symbolClass.getId());
		}

		return identifierType;
	}

	public void checkIfVariableExist(ASTVariable varDecl)
	{
		ASTType t = varDecl.getType().accept(this);
		String id = varDecl.getId().getId();

		if (symbolFunction != null) {
			if (!symbolFunction.addVariable(id, t)) {
				Token tok = varDecl.getId().getToken();
				addError(tok.getRow(), tok.getCol(),
						"Variable " + id + " already defined in function " + symbolFunction.getId());
			}
		} else if (symbolClass != null) {
			if (!symbolClass.addVariable(id, t)) {
				Token sym = varDecl.getId().getToken();
				addError(sym.getRow(), sym.getCol(),
						"Variable " + id + " already defined in class " + symbolClass.getId());
			}
		} else {
			if (!symbolProgram.addVariable(id, t)) {
				Token sym = varDecl.getId().getToken();
				addError(sym.getRow(), sym.getCol(), "Variable " + id + " already defined");
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
//
//	@Override
//	public ASTType visit(ASTArgument argDecl)
//	{
//		ASTType t = argDecl.getType().accept(this);
//		String id = argDecl.getId().getId();
//
//		if (!symbolFunction.addParam(id, t)) {
//			Token sym = argDecl.getId().getToken();
//			addError(sym.getRow(), sym.getCol(), "Argument " + id + " already defined in method " + symbolFunction.getId() + " in class " + symbolClass.getId());
//		}
//		return null;
//	}

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

	@Override
	public ASTType visit(ASTNotEquals astNotEquals)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ASTType visit(ASTPlus astPlus)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
