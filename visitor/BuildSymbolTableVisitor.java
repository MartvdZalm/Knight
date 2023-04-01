package visitor;

import ast.*;
import symbol.*;
import lexer.*;
import semantics.SemanticErrors;

public class BuildSymbolTableVisitor implements Visitor<Type>
{

	SymbolTable symbolTable;
	String mKlassId;

	public BuildSymbolTableVisitor() {
		this.symbolTable = new SymbolTable();
	}

	public SymbolTable getSymTab() {

		return this.symbolTable;
	}

	private Klass currClass;
	private Function currMethod;

	@Override
	public Type visit(Program n) {

		n.mClass.accept(this);

		for (int i = 0; i < n.classList.size(); i++) {
			n.classList.get(i).accept(this);
		}
		return null;
	}

	@Override
	public Type visit(Print n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(Assign n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(Skip n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(Block n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(IfThenElse n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(While n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(IntLiteral n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(Plus n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(Minus n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(Times n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(Division n) {
		n.getLhs().accept(this);
		n.getRhs().accept(this);
		return null;
	}

	@Override
	public Type visit(Equals n) {
		n.getLhs().accept(this);
		n.getRhs().accept(this);
		return null;
	}

	@Override
	public Type visit(LessThan n) {
		n.getLhs().accept(this);
		n.getRhs().accept(this);
		return null;
	}

	@Override
	public Type visit(And n) {
		n.getLhs().accept(this);
		n.getRhs().accept(this);
		return null;
	}

	@Override
	public Type visit(Or n) {
		n.getLhs().accept(this);
		n.getRhs().accept(this);
		return null;
	}

	@Override
	public Type visit(True true1) {
		return null;
	}

	@Override
	public Type visit(False false1) {
		return null;
	}

	@Override
	public Type visit(IdentifierExpr identifier) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(This this1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(NewArray newArray) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(NewInstance newInstance) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(CallFunc cm) {

		return null;
	}

	@Override
	public Type visit(Length length) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(IntType intType) {
		return intType;
	}

	@Override
	public Type visit(StringType stringType) {
		return stringType;
	}

	@Override
	public Type visit(BooleanType booleanType) {
		return booleanType;
	}

	@Override
	public Type visit(IntArrayType intArrayType) {
		return intArrayType;
	}

	@Override
	public Type visit(IdentifierType refType) {

		String id = refType.getVarID();
		if (id != null && id.equals(mKlassId)) {
			// TODO: This is a Hack!
			Token sym = refType.getToken();
			addError(sym.getRow(), sym.getCol(),
					"main class " + id + " cannot be used as a type in class " + currClass.getId());
		}

		return refType;
	}

	@Override
	public Type visit(VarDecl vd) {
		Type t = vd.getType().accept(this);
		String id = vd.getId().getVarID();

		if (currMethod != null) {// Method Variable
			if (!currMethod.addVar(id, t)) {
				Token sym = vd.getId().getToken();
				addError(sym.getRow(), sym.getCol(), "Variable " + id + " already defined in method "
						+ currMethod.getId() + " in class " + currClass.getId());
			}

		} else {// Klass variable

			if (!currClass.addVar(id, t)) {// duplicate
				Token sym = vd.getId().getToken();
				addError(sym.getRow(), sym.getCol(),
						"Variable " + id + " already defined in class " + currClass.getId());
			}
		}

		return null;
	}

	@Override
	public Type visit(ArgDecl ad) {
		Type t = ad.getType().accept(this);
		String id = ad.getId().getVarID();
		if (!currMethod.addParam(id, t)) {
			Token sym = ad.getId().getToken();
			addError(sym.getRow(), sym.getCol(), "Argument " + id + " already defined in method "
					+ currMethod.getId() + " in class " + currClass.getId());
		}

		return null;
	}

	@Override
	public Type visit(MainClass mc) {
		String id = mc.getClassName().getVarID();
		mKlassId = id;
		if (!symbolTable.addKlass(id, null)) {
			Token sym = mc.getToken();
			addError(sym.getRow(), sym.getCol(), "Class " + id + " is already defined!");
			currClass = new Klass(id, null);
		} else {
			currClass = symbolTable.getKlass(id);
		}


		for (int i = 0; i < mc.getStatListSize(); i++) {
			mc.getStatAt(i).accept(this);
		}

		for (int i = 0; i < mc.getVarListSize(); i++) {
			mc.getVarDeclAt(i).accept(this);
		}

		// currMethod = null;
		currClass = null;
		return null;
	}

	@Override
	public Type visit(Identifier identifier) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(IndexArray indexArray) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(ArrayAssign arrayAssign) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(StringLiteral stringLiteral) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(FuncDecl md) {
		Type t = md.getReturnType().accept(this);
		String id = md.getMethodName().getVarID();

		if (!currClass.addMethod(id, t)) {
			Token sym = md.getToken();
			addError(sym.getRow(), sym.getCol(), "Method " + id + " already defined in class " + currClass.getId());
			currMethod = new Function(id, t);
		} else {
			currMethod = currClass.getMethod(id);
		}

		for (int i = 0; i < md.getArgListSize(); i++) {
			ArgDecl ad = md.getArgDeclAt(i);
			ad.accept(this);
		}

		for (int i = 0; i < md.getVarListSize(); i++) {
			VarDecl vd = md.getVarDeclAt(i);
			vd.accept(this);
		}

		for (int i = 0; i < md.getStatListSize(); i++) {
			Statement st = md.getStatAt(i);
			st.accept(this);
		}

		md.getReturnExpr().accept(this);
		currMethod = null;
		return null;
	}

	@Override
	public Type visit(ClassDeclSimple classDeclSimple) {

		String id = classDeclSimple.getId().getVarID();
		if (!symbolTable.addKlass(id, null)) {
			Token sym = classDeclSimple.getToken();
			addError(sym.getRow(), sym.getCol(), "Class " + id + " is already defined!");
			currClass = new Klass(id, null);
		} else {
			currClass = symbolTable.getKlass(id);
		}

		for (int i = 0; i < classDeclSimple.getVarListSize(); i++) {
			VarDecl vd = classDeclSimple.getVarDeclAt(i);
			vd.accept(this);
		}

		for (int i = 0; i < classDeclSimple.getMethodListSize(); i++) {
			FuncDecl md = classDeclSimple.getMethodDeclAt(i);
			md.accept(this);
		}
		// System.out.println("visited class" + id);
		return null;
	}

	@Override
	public Type visit(ClassDeclExtends classDeclExtends) {

		String id = classDeclExtends.getId().getVarID();
		String parent = classDeclExtends.getParent().getVarID();
		if (!symbolTable.addKlass(id, parent)) {
			Token sym = classDeclExtends.getToken();
			addError(sym.getRow(), sym.getCol(), "Class " + id + " is already defined!");
			currClass = new Klass(id, parent);
		} else {
			currClass = symbolTable.getKlass(id);
		}

		if (parent != null && parent.equals(mKlassId)) {
			// TODO: This is a Hack!
			Token sym = classDeclExtends.getParent().getToken();
			addError(sym.getRow(), sym.getCol(), "class " + id + " cannot inherit main class");
		}

		for (int i = 0; i < classDeclExtends.getVarListSize(); i++) {
			VarDecl vd = classDeclExtends.getVarDeclAt(i);
			vd.accept(this);
		}

		for (int i = 0; i < classDeclExtends.getMethodListSize(); i++) {
			FuncDecl md = classDeclExtends.getMethodDeclAt(i);
			md.accept(this);
		}
		currClass = null;
		// System.out.println("visited class" + id);
		return null;
	}

	public static void addError(int line, int col, String errorText) {
		SemanticErrors.addError(line, col, errorText);
	}
}