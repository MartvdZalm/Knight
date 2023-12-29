package src.visitor;

import src.ast.*;
import src.ast.Class;
import src.lexer.*;
import src.semantics.SemanticErrors;

import src.symbol.SymbolClass;
import src.symbol.SymbolFunction;
import src.symbol.SymbolProgram;

public class BuildSymbolProgramVisitor implements Visitor<Type>
{
	private SymbolProgram symbolProgram;
	private SymbolClass symbolClass;
	private SymbolFunction symbolFunction;
	private String mKlassId;

	public BuildSymbolProgramVisitor()
	{
		symbolProgram = new SymbolProgram();
	}

	public SymbolProgram getSymbolProgram()
	{
		return symbolProgram;
	}

	@Override
	public Type visit(Program program)
	{
		for (int i = 0; i < program.getIncludeListSize(); i++) {
			program.getIncludeDeclAt(i).accept(this);
		}

		for (int i = 0; i < program.getEnumListSize(); i++) {
			program.getEnumDeclAt(i).accept(this);
		}

		for (int i = 0; i < program.getInterListSize(); i++) {
			program.getInterDeclAt(i).accept(this);
		}

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
	public Type visit(Class classDecl)
	{
		String identifier = classDecl.getId().getVarID();

		if (!symbolProgram.addClass(identifier, null)) {
			Token sym = classDecl.getToken();
			addError(sym.getRow(), sym.getCol(), "Class " + identifier + " is already defined!");
			symbolClass = new SymbolClass(identifier, null);
		} else {
			symbolClass = symbolProgram.getClass(identifier);
		}

		for (int i = 0; i < classDecl.getVariableListSize(); i++) {
			classDecl.getVariableDeclAt(i).accept(this);
		}

		for (int i = 0; i < classDecl.getFunctionListSize(); i++) {
			classDecl.getFunctionDeclAt(i).accept(this);
		}

		symbolClass = null;
		return null;
	}

	public void checkFunction(Function funcDecl)
	{
		Type type = funcDecl.getReturnType().accept(this);
		String id = funcDecl.getId().getVarID();

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
				addError(tok.getRow(), tok.getCol(), "Function " + id + " already defined in class " + symbolClass.getId());
			} else {
				symbolFunction = symbolClass.getFunction(id);
			}
		}	

		for (int i = 0; i < funcDecl.getArgumentListSize(); i++) {
			funcDecl.getArgumentDeclAt(i).accept(this);
		}

		for (int i = 0; i < funcDecl.getVariableListSize(); i++) {
			funcDecl.getVariableDeclAt(i).accept(this);
		}

		for (int i = 0; i < funcDecl.getStatementListSize(); i++) {
			funcDecl.getStatementDeclAt(i).accept(this);
		}
	}

	@Override
	public Type visit(Function functionDecl)
	{
		checkFunction(functionDecl);
		symbolFunction = null;
		return null;
	}

	@Override
	public Type visit(FunctionReturn functionReturn)
	{
		checkFunction(functionReturn);
		functionReturn.getReturnExpr().accept(this);
		symbolFunction = null;
		return null;
	}

	@Override
	public Type visit(Include include)
	{
		return null;
	}

	@Override
	public Type visit(Skip skip)
	{
		return null;
	}

	@Override
	public Type visit(Assign assign)
	{
		return null;
	}
	
	@Override
	public Type visit(Block block)
	{
		return null;
	}

	@Override
	public Type visit(IfThenElse ifThenElse)
	{
		return null;
	}

	@Override
	public Type visit(While while1)
	{
		return null;
	}
	
	@Override
	public Type visit(IntLiteral intLiteral)
	{
		return null;
	}

	@Override
	public Type visit(Plus plus)
	{
		return null;
	}

	@Override
	public Type visit(Minus minus)
	{
		return null;
	}

	@Override
	public Type visit(Times times)
	{
		return null;
	}

	@Override
	public Type visit(Increment increment)
	{
		return null;
	}

	@Override
	public Type visit(Modulus modulus)
	{
		return null;
	}

	@Override
	public Type visit(Division division)
	{
		division.getLhs().accept(this);
		division.getRhs().accept(this);
		return null;
	}

	@Override
	public Type visit(Equals equals)
	{
		equals.getLhs().accept(this);
		equals.getRhs().accept(this);
		return null;
	}

	@Override
	public Type visit(LessThan lessThan)
	{
		lessThan.getLhs().accept(this);
		lessThan.getRhs().accept(this);
		return null;
	}

	@Override
	public Type visit(LessThanOrEqual lessThanOrEqual)
	{
		lessThanOrEqual.getLhs().accept(this);
		lessThanOrEqual.getRhs().accept(this);
		return null;
	}

	@Override
	public Type visit(GreaterThan greaterThan)
	{
		greaterThan.getLhs().accept(this);
		greaterThan.getRhs().accept(this);
		return null;
	}

	@Override
	public Type visit(GreaterThanOrEqual greaterThanOrEqual)
	{
		greaterThanOrEqual.getLhs().accept(this);
		greaterThanOrEqual.getRhs().accept(this);
		return null;
	}

	@Override
	public Type visit(And and)
	{
		and.getLhs().accept(this);
		and.getRhs().accept(this);
		return null;
	}

	@Override
	public Type visit(Or or)
	{
		or.getLhs().accept(this);
		or.getRhs().accept(this);
		return null;
	}

	@Override
	public Type visit(True true1)
	{
		return null;
	}

	@Override
	public Type visit(False false1)
	{
		return null;
	}

	@Override
	public Type visit(IdentifierExpr identifierExpr)
	{
		return null;
	}

	@Override
	public Type visit(NewArray newArray)
	{
		return null;
	}

	@Override
	public Type visit(NewInstance newInstance)
	{
		return null;
	}

	@Override
	public Type visit(CallFunctionExpr callFunctionExpr)
	{
		return null;
	}

	@Override
	public Type visit(CallFunctionStat callFunctionStat)
	{
		return null;
	}

	@Override
	public Type visit(ReturnStatement returnStatement)
	{
		return null;
	}

	@Override
	public Type visit(FunctionType functionType)
	{
		return null;
	}

	@Override
	public Type visit(IntType intType)
	{
		return intType;
	}

	@Override
	public Type visit(StringType stringType)
	{
		return stringType;
	}

	@Override
	public Type visit(VoidType voidType)
	{
		return voidType;		
	}

	@Override
	public Type visit(BooleanType booleanType)
	{
		return booleanType;
	}

	@Override
	public Type visit(IntArrayType intArrayType)
	{
		return intArrayType;
	}

	@Override
	public Type visit(IdentifierType identifierType)
	{
		String id = identifierType.getVarID();

		if (id != null && id.equals(mKlassId)) {
			Token tok = identifierType.getToken();
			addError(tok.getRow(), tok.getCol(), "Class " + id + " cannot be used as a type in class " + symbolClass.getId());
		}

		return identifierType;
	}

	public void checkIfVariableExist(Variable varDecl)
	{
		Type t = varDecl.getType().accept(this);
		String id = varDecl.getId().getVarID();

		if (symbolFunction != null) {
			if (!symbolFunction.addVariable(id, t)) {
				Token tok = varDecl.getId().getToken();
				addError(tok.getRow(), tok.getCol(), "Variable " + id + " already defined in method " + symbolFunction.getId() + " in class " + symbolClass.getId());
			}
		} else if (symbolClass != null) {
			if (!symbolClass.addVariable(id, t)) {
				Token sym = varDecl.getId().getToken();
				addError(sym.getRow(), sym.getCol(), "Variable " + id + " already defined in class " + symbolClass.getId());
			}
		} else {
			if (!symbolProgram.addVariable(id, t)) {
				Token sym = varDecl.getId().getToken();
				addError(sym.getRow(), sym.getCol(), "Variable " + id + " already defined");
			}
		}
	}

	@Override
	public Type visit(Variable varDecl)
	{
		checkIfVariableExist(varDecl);
		return null;
	}

	@Override
	public Type visit(VariableInit varDeclInit)
	{
		checkIfVariableExist(varDeclInit);
		return null;
	}

	@Override
	public Type visit(Argument argDecl)
	{
		Type t = argDecl.getType().accept(this);
		String id = argDecl.getId().getVarID();

		if (!symbolFunction.addParam(id, t)) {
			Token sym = argDecl.getId().getToken();
			addError(sym.getRow(), sym.getCol(), "Argument " + id + " already defined in method " + symbolFunction.getId() + " in class " + symbolClass.getId());
		}
		return null;
	}

	@Override
	public Type visit(Identifier identifier)
	{
		return null;
	}

	@Override
	public Type visit(ArrayIndexExpr indexArray)
	{
		return null;
	}

	@Override
	public Type visit(ArrayAssign arrayAssign)
	{
		return null;
	}

	@Override
	public Type visit(StringLiteral stringLiteral)
	{
		return null;
	}

	public static void addError(int line, int col, String errorText)
	{
		SemanticErrors.addError(line, col, errorText);
	}

	@Override
	public Type visit(Enumeration enumDecl)
	{
		return null;
	}

	@Override
	public Type visit(Interface interDecl)
	{
		return null;
	}

	@Override
	public Type visit(ForLoop forLoop)
	{
		return null;
	}
}