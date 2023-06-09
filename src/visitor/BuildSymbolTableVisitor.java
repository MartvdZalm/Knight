package src.visitor;

import src.ast.*;
import src.lexer.*;
import src.semantics.SemanticErrors;
import src.symbol.*;

public class BuildSymbolTableVisitor implements Visitor<Type>
{
	private Klass currClass;
	private Function currFunc;
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
	public Type visit(Program n)
	{
		for (int i = 0; i < n.getClassListSize(); i++) {
			n.getClassList().get(i).accept(this);
		}
		return null;
	}

	@Override
	public Type visit(Skip skip)
	{
		return null;
	}

	@Override
	public Type visit(Assign n)
	{
		return null;
	}
	
	@Override
	public Type visit(Block n)
	{
		return null;
	}

	@Override
	public Type visit(IfThenElse n)
	{
		return null;
	}

	@Override
	public Type visit(While n)
	{
		return null;
	}

	@Override
	public Type visit(ForLoop forLoop)
	{
		return null;
	}

	@Override
	public Type visit(IntLiteral n)
	{
		return null;
	}

	@Override
	public Type visit(Plus n)
	{
		return null;
	}

	@Override
	public Type visit(Minus n)
	{
		return null;
	}

	@Override
	public Type visit(Times n)
	{
		return null;
	}

	@Override
	public Type visit(Division n)
	{
		n.getLhs().accept(this);
		n.getRhs().accept(this);
		return null;
	}

	@Override
	public Type visit(Equals n)
	{
		n.getLhs().accept(this);
		n.getRhs().accept(this);
		return null;
	}

	@Override
	public Type visit(LessThan n)
	{
		n.getLhs().accept(this);
		n.getRhs().accept(this);
		return null;
	}

	@Override
	public Type visit(And n)
	{
		n.getLhs().accept(this);
		n.getRhs().accept(this);
		return null;
	}

	@Override
	public Type visit(Or n)
	{
		n.getLhs().accept(this);
		n.getRhs().accept(this);
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
	public Type visit(IdentifierExpr identifier)
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
	public Type visit(CallFunctionExpr cf)
	{
		return null;
	}

	@Override
	public Type visit(CallFunctionStat cf)
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
	public Type visit(IdentifierType refType)
	{
		String id = refType.getVarID();
		if (id != null && id.equals(mKlassId)) {
			Token tok = refType.getToken();
			addError(tok.getRow(), tok.getCol(), "main class " + id + " cannot be used as a type in class " + currClass.getId());
		}

		return refType;
	}

	@Override
	public Type visit(VarDecl vd)
	{
		Type t = vd.getType().accept(this);
		String id = vd.getId().getVarID();

		if (currFunc != null) {
			if (!currFunc.addVar(id, t)) {
				Token tok = vd.getId().getToken();
				addError(tok.getRow(), tok.getCol(), "Variable " + id + " already defined in method " + currFunc.getId() + " in class " + currClass.getId());
			}
		} else {
			if (!currClass.addVar(id, t)) {
				Token sym = vd.getId().getToken();
				addError(sym.getRow(), sym.getCol(), "Variable " + id + " already defined in class " + currClass.getId());
			}
		}

		return null;
	}

	@Override
	public Type visit(VarDeclInit vd)
	{
		Type t = vd.getType().accept(this);
		String id = vd.getId().getVarID();

		if (vd.getExpr() instanceof FunctionExprReturn || vd.getExpr() instanceof FunctionExprVoid) {
			if (!currClass.addFunction(id, t)) {
				Token tok = vd.getToken();
				addError(tok.getRow(), tok.getCol(), "Function " + id + " already defined in class " + currClass.getId());
				return null;
			} else {
				currFunc = currClass.getFunction(id);
			}

			vd.getExpr().accept(this);
		}

		if (currFunc != null) {
			if (!currFunc.addVar(id, t)) {
				Token tok = vd.getId().getToken();
				addError(tok.getRow(), tok.getCol(), "Variable " + id + " already defined in method " + currFunc.getId() + " in class " + currClass.getId());
			}
		} else {
			if (!currClass.addVar(id, t)) {
				Token sym = vd.getId().getToken();
				addError(sym.getRow(), sym.getCol(), "Variable " + id + " already defined in class " + currClass.getId());
			}
		}
		
		return null;
	}

	@Override
	public Type visit(ArgDecl ad)
	{
		Type t = ad.getType().accept(this);
		String id = ad.getId().getVarID();
		if (!currFunc.addParam(id, t)) {
			Token sym = ad.getId().getToken();
			addError(sym.getRow(), sym.getCol(), "Argument " + id + " already defined in method " + currFunc.getId() + " in class " + currClass.getId());
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

	@Override
	public Type visit(FunctionExprReturn funcExprReturn)
	{
		for (int i = 0; i < funcExprReturn.getArgListSize(); i++) {
			ArgDecl ad = funcExprReturn.getArgDeclAt(i);
			ad.accept(this);
		}

		for (int i = 0; i < funcExprReturn.getVarListSize(); i++) {
			Declaration vd = funcExprReturn.getVarDeclAt(i);
			vd.accept(this);
		}

		for (int i = 0; i < funcExprReturn.getStatListSize(); i++) {
			Statement st = funcExprReturn.getStatAt(i);
			st.accept(this);
		}

		funcExprReturn.getReturnExpr().accept(this);
		currFunc = null;

		return null;
	}

	@Override
	public Type visit(FunctionExprVoid funcExprVoid)
	{
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

		for (int i = 0; i < classDeclSimple.getVarListSize(); i++) {
			Declaration vd = classDeclSimple.getVarDeclAt(i);
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

		for (int i = 0; i < classDeclExtends.getVarListSize(); i++) {
			Declaration vd = classDeclExtends.getVarDeclAt(i);
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
	public Type visit(Include include)
	{
		return null;
	}
}