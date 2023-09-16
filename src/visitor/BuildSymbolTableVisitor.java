package src.visitor;

import src.ast.*;
import src.lexer.*;
import src.semantics.SemanticErrors;
import src.symbol.*;
import src.symbol.Function;

public class BuildSymbolTableVisitor implements Visitor<Type>
{
	private Klass currClass;
	private Function currFunction;
	private SymbolTable symbolTable;
	private String mKlassId;

	public BuildSymbolTableVisitor()
	{
		symbolTable = new SymbolTable();
	}

	public SymbolTable getSymTab()
	{
		return symbolTable;
	}

	@Override
	public Type visit(Program program)
	{
		for (int i = 0; i < program.getClassListSize(); i++) {
			program.getClassList().get(i).accept(this);
		}
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
	public Type visit(ForLoop forLoop)
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
			addError(tok.getRow(), tok.getCol(), "main class " + id + " cannot be used as a type in class " + currClass.getId());
		}

		return identifierType;
	}

	public void checkIfVariableExist(VarDecl varDecl)
	{
		Type t = varDecl.getType().accept(this);
		String id = varDecl.getId().getVarID();

		if (currFunction != null) {
			if (!currFunction.addVar(id, t)) {
				Token tok = varDecl.getId().getToken();
				addError(tok.getRow(), tok.getCol(), "Variable " + id + " already defined in method " + currFunction.getId() + " in class " + currClass.getId());
			}
		} else {
			if (!currClass.addVar(id, t)) {
				Token sym = varDecl.getId().getToken();
				addError(sym.getRow(), sym.getCol(), "Variable " + id + " already defined in class " + currClass.getId());
			}
		}
	}

	@Override
	public Type visit(VarDeclNoInit varDecl)
	{
		checkIfVariableExist(varDecl);
		return null;
	}

	@Override
	public Type visit(VarDeclInit varDeclInit)
	{
		checkIfVariableExist(varDeclInit);
		return null;
	}

	@Override
	public Type visit(ArgDecl argDecl)
	{
		Type t = argDecl.getType().accept(this);
		String id = argDecl.getId().getVarID();

		if (!currFunction.addParam(id, t)) {
			Token sym = argDecl.getId().getToken();
			addError(sym.getRow(), sym.getCol(), "Argument " + id + " already defined in method " + currFunction.getId() + " in class " + currClass.getId());
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

	public void checkFunction(FunctionDecl funcDecl)
	{
		Type type = funcDecl.getReturnType().accept(this);
		String functionName = funcDecl.getFunctionName().getVarID();

		if (!currClass.addFunction(functionName, type)) {
			Token tok = funcDecl.getToken();
			addError(tok.getRow(), tok.getCol(), "Function " + functionName + "  already defined in class " + currClass.getId());
		} else {
			currFunction = currClass.getFunction(functionName);
		}

		for (int i = 0; i < funcDecl.getArgListSize(); i++) {
			ArgDecl ad = funcDecl.getArgDeclAt(i);
			ad.accept(this);
		}

		for (int i = 0; i < funcDecl.getDeclListSize(); i++) {
			Declaration vd = funcDecl.getDeclAt(i);
			vd.accept(this);
		}
	}

	@Override
	public Type visit(FunctionAnonymous functionAnonymous)
	{
		checkFunction(functionAnonymous);
		currFunction = null;
		return null;
	}

	@Override
	public Type visit(FunctionVoid functionVoid)
	{
		checkFunction(functionVoid);
		currFunction = null;
		return null;
	}

	@Override
	public Type visit(FunctionReturn functionReturn)
	{
		checkFunction(functionReturn);
		functionReturn.getReturnExpr().accept(this);
		currFunction = null;
		return null;
	}

	@Override
	public Type visit(ClassDeclSimple classDeclSimple)
	{
		String identifier = classDeclSimple.getId().getVarID();
		if (!symbolTable.addKlass(identifier, null)) {
			Token sym = classDeclSimple.getToken();
			addError(sym.getRow(), sym.getCol(), "Class " + identifier + " is already defined!");
			currClass = new Klass(identifier, null);
		} else {
			currClass = symbolTable.getKlass(identifier);
		}

		for (int i = 0; i < classDeclSimple.getDeclListSize(); i++) {
			Declaration vd = classDeclSimple.getDeclAt(i);
			vd.accept(this);
		}

		return null;
	}

	@Override
	public Type visit(ClassDeclExtends classDeclExtends)
	{
		String identifier = classDeclExtends.getId().getVarID();
		String parent = classDeclExtends.getParent().getVarID();
		if (!symbolTable.addKlass(identifier, parent)) {
			Token sym = classDeclExtends.getToken();
			addError(sym.getRow(), sym.getCol(), "Class " + identifier + " is already defined!");
			currClass = new Klass(identifier, parent);
		} else {
			currClass = symbolTable.getKlass(identifier);
		}

		if (parent != null && parent.equals(mKlassId)) {
			Token sym = classDeclExtends.getParent().getToken();
			addError(sym.getRow(), sym.getCol(), "class " + identifier + " cannot inherit main class");
		}

		for (int i = 0; i < classDeclExtends.getDeclListSize(); i++) {
			Declaration vd = classDeclExtends.getDeclAt(i);
			vd.accept(this);
		}

		currClass = null;
		return null;
	}

	public static void addError(int line, int col, String errorText)
	{
		SemanticErrors.addError(line, col, errorText);
	}

	@Override
	public Type visit(EnumDecl enumDecl) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'visit'");
	}
}