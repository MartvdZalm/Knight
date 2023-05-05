package src.visitor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import src.ast.*;
import src.lexer.*;
import src.semantics.*;
import src.symbol.*;

public class TypeAnalyser implements Visitor<Type>
{
	private SymbolTable st;
	private Klass currClass;
	private Function currFunc;
	private Set<String> hsKlass = new HashSet<>();
	private Set<String> hsFunc = new HashSet<>();

	public TypeAnalyser(SymbolTable table)
	{
		this.st = table;
	}

	@Override
	public Type visit(Print n)
	{
		Type texp = n.getExpr().accept(this);
		if (texp == null) {
			return null;
		}

		if (!((texp instanceof IntType) || (texp instanceof BooleanType) || (texp instanceof StringType))) {
			Token sym = n.getExpr().getToken();
			addError(sym.getRow(), sym.getCol(), "The argument of System.out.print must be of Type int, boolean or String");
		} else {
			n.getExpr().setType(texp);
		}
		return null;
	}

	@Override
	public Type visit(Println n)
	{
		Type texp = n.getExpr().accept(this);
		if (texp == null) {
			return null;
		}

		if (!((texp instanceof IntType) || (texp instanceof BooleanType) || (texp instanceof StringType))) {
			Token sym = n.getExpr().getToken();
			addError(sym.getRow(), sym.getCol(), "The argument of System.out.print must be of Type int, boolean or String");
		}

		return null;
	}

	@Override
	public Type visit(Assign n)
	{
		Type rhs = n.getExpr().accept(this);
		Type lhs = n.getId().accept(this);

		if (!st.compareTypes(lhs, rhs)) {
			Token sym = n.getToken();
			if (lhs == null || rhs == null) {
				addError(sym.getRow(), sym.getCol(), "Incompatible types used with assignment Operator = ");
			} else {
				addError(sym.getRow(), sym.getCol(), "Operator = cannot be applied to " + lhs + ", " + rhs);
			}

		} else {
			n.getExpr().setType(rhs);
		}

		return null;
	}

	@Override
	public Type visit(Skip n)
	{
		return null;
	}

	@Override
	public Type visit(Block n)
	{
		for (int i = 0; i < n.getStatListSize(); i++) {
			Statement st = n.getStatAt(i);
			st.accept(this);
		}
		return null;
	}

	@Override
	public Type visit(IfThenElse n)
	{
		Type texp = n.getExpr().accept(this);
		if (!(texp instanceof BooleanType)) {
			Token sym = n.getExpr().getToken();
			addError(sym.getRow(), sym.getCol(), "Expression must be of type boolean");
		} else {
			n.getExpr().setType(texp);
		}

		n.getThen().accept(this);
		n.getElze().accept(this);

		return null;
	}

	@Override
	public Type visit(While n)
	{
		Type texp = n.getExpr().accept(this);
		if (!(texp instanceof BooleanType)) {
			Token sym = n.getExpr().getToken();
			addError(sym.getRow(), sym.getCol(), "Expression must be of type boolean");
		} else {
			n.getExpr().setType(texp);
		}

		n.getBody().accept(this);

		return null;
	}

	@Override
	public Type visit(IntLiteral n)
	{
		Type t = new IntType(n.getToken());
		n.setType(t);
		return t;
	}

	@Override
	public Type visit(Plus n)
	{
		Type tlhs = n.getLhs().accept(this);
		Type trhs = n.getRhs().accept(this);

		if (tlhs == null || !(tlhs instanceof IntType || tlhs instanceof StringType) || trhs == null
				|| !(trhs instanceof IntType || trhs instanceof StringType)) {
			Token sym = n.getToken();
			addError(sym.getRow(), sym.getCol(), "Incompatible Types used with + operator");
			return new IntType(n.getToken());
		}

		if (tlhs instanceof IntType) {
			n.setType(trhs);
			return trhs;
		}

		if (tlhs instanceof StringType) {
			n.setType(tlhs);
			return tlhs;
		}

		Type t = new IntType(n.getToken());
		n.setType(t);
		return t;
	}

	@Override
	public Type visit(Minus n)
	{
		Type lhs = n.getLhs().accept(this);
		Type rhs = n.getRhs().accept(this);

		if (lhs == null || rhs == null) { 
			Token sym = n.getToken();
			addError(sym.getRow(), sym.getCol(), "Improper Type used with - operator");
			return new IntType(n.getToken());
		}

		if (!(lhs instanceof IntType) || !(rhs instanceof IntType)) {
			Token sym = n.getLhs().getToken();
			addError(sym.getRow(), sym.getCol(), "Operator - cannot be applied to " + lhs + ", " + rhs);
		}

		Type t = new IntType(n.getToken());
		n.setType(t);
		return t;
	}

	@Override
	public Type visit(Times n)
	{
		Type lhs = n.getLhs().accept(this);
		Type rhs = n.getRhs().accept(this);

		if (lhs == null || rhs == null) { 
			Token sym = n.getToken();
			addError(sym.getRow(), sym.getCol(), "Improper Type used with * operator");
			return new IntType(n.getToken());
		}

		if (!(lhs instanceof IntType) || !(rhs instanceof IntType)) {
			Token sym = n.getLhs().getToken();
			addError(sym.getRow(), sym.getCol(), "Operator * cannot be applied to " + lhs + ", " + rhs);
		}

		Type t = new IntType(n.getToken());
		n.setType(t);
		return t;
	}

	@Override
	public Type visit(Division n)
	{
		Type lhs = n.getLhs().accept(this);
		Type rhs = n.getRhs().accept(this);

		if (lhs == null || rhs == null) { 
			Token sym = n.getToken();
			addError(sym.getRow(), sym.getCol(), "Improper Type used with / operator");
			return new IntType(n.getToken());
		}

		if (!(lhs instanceof IntType) || !(rhs instanceof IntType)) {
			Token sym = n.getLhs().getToken();
			addError(sym.getRow(), sym.getCol(), "Operator / cannot be applied to " + lhs + ", " + rhs);
		}
		Type t = new IntType(n.getToken());
		n.setType(t);
		return t;
	}

	@Override
	public Type visit(Equals n)
	{
		Type t1 = n.getLhs().accept(this);
		Type t2 = n.getRhs().accept(this);

		if (t1 == null || t2 == null) {
			Token sym = n.getToken();
			addError(sym.getRow(), sym.getCol(), "Incorrect types used with == oprator");
		} else if ((t1 instanceof IntType && t2 instanceof IntType)
				|| (t1 instanceof BooleanType && t2 instanceof BooleanType)
				|| (t1 instanceof StringType && t2 instanceof StringType)
				|| (t1 instanceof IntArrayType && t2 instanceof IntArrayType)
				|| (t1 instanceof IdentifierType && t2 instanceof IdentifierType)) {
			n.setType(t1);
		} else {
			Token sym = n.getToken();
			addError(sym.getRow(), sym.getCol(), "Oprator == cannot be applied to " + t1 + ", " + t2);
		}
		return new BooleanType(n.getToken());
	}

	@Override
	public Type visit(LessThan n)
	{
		Type t1 = n.getLhs().accept(this);
		Type t2 = n.getRhs().accept(this);

		if (t1 == null || t2 == null) {
			Token sym = n.getToken();
			addError(sym.getRow(), sym.getCol(), "Incorrect types used with < oprator");
		} else if (!(t1 instanceof IntType) || !(t2 instanceof IntType)) {
			Token sym = n.getToken();
			addError(sym.getRow(), sym.getCol(), "Operator < cannot be applied to " + t1 + ", " + t2);
		}
		Type t = new BooleanType(n.getToken());
		n.setType(t);
		return t;
	}

	@Override
	public Type visit(And n)
	{
		Type t1 = n.getLhs().accept(this);
		Type t2 = n.getRhs().accept(this);

		if (t1 == null || t2 == null) {
			Token sym = n.getToken();
			addError(sym.getRow(), sym.getCol(), "Incorrect types used with && oprator");
		} else if (!(t1 instanceof BooleanType) || !(t2 instanceof BooleanType)) {
			Token sym = n.getLhs().getToken();
			addError(sym.getRow(), sym.getCol(), "Operator && cannot be applied to " + t1 + ", " + t2);
		}
		Type t = new BooleanType(n.getToken());
		n.setType(t);
		return t;
	}

	@Override
	public Type visit(Or n)
	{
		Type t1 = n.getLhs().accept(this);
		Type t2 = n.getRhs().accept(this);

		if (t1 == null || t2 == null) {
			Token sym = n.getToken();
			addError(sym.getRow(), sym.getCol(), "Incorrect types used with || oprator");
		} else if (!(t1 instanceof BooleanType) || !(t2 instanceof BooleanType)) {
			Token sym = n.getLhs().getToken();
			addError(sym.getRow(), sym.getCol(), "Operator || cannot be applied to " + t1 + ", " + t2);
		}
		Type t = new BooleanType(n.getToken());
		n.setType(t);
		return t;
	}

	@Override
	public Type visit(True true1)
	{
		Type t = new BooleanType(true1.getToken());
		true1.setType(t);
		return t;
	}

	@Override
	public Type visit(False false1)
	{
		Type t = new BooleanType(false1.getToken());
		false1.setType(t);
		return t;
	}

	@Override
	public Type visit(IdentifierExpr i)
	{
		Binding b = i.getB();
		if (b != null) {
			Type t = ((Variable) b).getType();
			i.setType(t);
			return t;
		}
		return null;
	}

	@Override
	public Type visit(This this1)
	{
		this1.setType(currClass.type());
		return currClass.type();
	}

	@Override
	public Type visit(NewArray na)
	{
		Type tl = na.getArrayLength().accept(this);
		if (tl == null || !(tl instanceof IntType)) {
			Token sym = na.getArrayLength().getToken();
			addError(sym.getRow(), sym.getCol(), "Array length must be of type int");
		}

		Type t = new IntArrayType(na.getToken());
		na.setType(t);
		return t;
	}

	@Override
	public Type visit(NewInstance ni)
	{
		Binding b = ni.getClassName().getB();
		if (b != null) {
			Klass klass = (Klass) b;
			ni.setType(klass.type());
			return klass.type();
		}
		return new IdentifierType(ni.getToken(), ni.getClassName().getVarID());
	}

	@Override
	public Type visit(CallFunc cm)
	{
		// Check Reference Object
		Type ref = cm.getInstanceName().accept(this);
		if (ref == null || !(ref instanceof IdentifierType)) {
			Token sym = cm.getInstanceName().getToken();
			addError(sym.getRow(), sym.getCol(), "Dereferenced object must be of an object type");
		}

		// Check if method exists
		IdentifierType tid = (IdentifierType) ref;
		IdentifierExpr mid = cm.getMethodId();
		Function m = st.getMethod(mid.getVarID(), tid.getVarID());
		if (m == null) {
			Token sym = mid.getToken();
			addError(sym.getRow(), sym.getCol(), "Method " + mid + " not declared");
			return null;
		} else {
			mid.setB(m);
			checkCallArguments(cm, m);
			cm.setType(m.getType());
			return m.getType();
		}
	}

	private void checkCallArguments(CallFunc cm, Function m)
	{
		List<Type> argTypes = new ArrayList<>();
		for (int i = 0; i < cm.getArgExprListSize(); i++) {
			Type t2 = cm.getArgExprAt(i).accept(this);
			argTypes.add(t2);
		}

		// Check number of arguments & parameters
		if (cm.getArgExprListSize() != m.getParamsSize()) {
			Token sym = cm.getToken();
			addError(sym.getRow(), sym.getCol(), "The method " + m.toString()
					+ " is not applicable for the arguments (" + getArguments(argTypes) + ")");
			return;
		}

		// Check argument types
		for (int i = 0; i < argTypes.size(); i++) {
			Variable var = m.getParamAt(i);
			Type t1 = var.getType();
			Type t2 = argTypes.get(i);

			if (!st.compareTypes(t1, t2)) {
				Token sym = cm.getArgExprAt(i).getToken();
				addError(sym.getRow(), sym.getCol(), "The method " + m.toString()
						+ " is not applicable for the arguments (" + getArguments(argTypes) + ")");
				return;
			}

		}
	}

	private String getArguments(List<Type> argList)
	{
		if (argList == null || argList.size() == 0) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < argList.size(); i++) {
			sb.append(argList.get(i));
			if (i < argList.size() - 1) {
				sb.append(", ");
			}
		}
		return sb.toString();
	}

	@Override
	public Type visit(Length length)
	{
		Type t = length.getArray().accept(this);

		if (t == null || !(t instanceof IntArrayType)) {
			Token sym = length.getArray().getToken();
			addError(sym.getRow(), sym.getCol(), "Identifier must be of Type int[]");
		} else {
			length.getArray().setType(t);
		}

		Type type = new IntType(length.getToken());
		length.setType(type);
		return type;
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
	public Type visit(IdentifierType referenceType)
	{
		return referenceType;
	}

	@Override
	public Type visit(VarDecl vd)
	{
		return vd.getType();
	}

	@Override
	public Type visit(VarDeclInit vd)
	{
		Type rhs = vd.getExpr().accept(this);
		Type lhs = vd.getId().accept(this);

		if (!st.compareTypes(lhs, rhs)) {
			Token sym = vd.getToken();
			if (lhs == null || rhs == null) {
				addError(sym.getRow(), sym.getCol(), "Incompatible types used with assignment Operator = ");
			} else {
				addError(sym.getRow(), sym.getCol(), "Operator = cannot be applied to " + lhs + ", " + rhs);
			}

		} else {
			vd.getExpr().setType(rhs);
		}
		return null;
	}

	@Override
	public Type visit(ArgDecl ad)
	{
		return ad.getType();
	}

	@Override
	public Type visit(FuncDeclMain funcDeclMain)
	{
		String id = funcDeclMain.getMethodName().getVarID();
		hsFunc.add(id);

		currFunc = (Function) funcDeclMain.getMethodName().getB();

		for (int i = 0; i < funcDeclMain.getVarListSize(); i++) {
			Declaration decl = funcDeclMain.getVarDeclAt(i);
			decl.accept(this);
		}

		for (int i = 0; i < funcDeclMain.getStatListSize(); i++) {
			Statement st = funcDeclMain.getStatAt(i);
			st.accept(this);
		}

		Type t1 = funcDeclMain.getReturnType();
		Type t2 = funcDeclMain.getReturnExpr().accept(this);

		Token tok = funcDeclMain.getReturnType().getToken();
		if (tok.getToken() != Tokens.INTEGER) {
			addError(tok.getRow(), tok.getCol(), "Method " + id + " must return a result of type int");
		} else {
			if (!st.compareTypes(t1, t2)) {
				Token sym = funcDeclMain.getReturnExpr().getToken();
				addError(sym.getRow(), sym.getCol(), "Method " + id + " must return a result of Type " + t1);
			}
		}

		funcDeclMain.getReturnExpr().setType(t2);
		currFunc = null;
		return funcDeclMain.getReturnType();
	}

	private void checkOverriding(FuncDecl md, Function m)
	{
		String p = currClass.parent();
		Function pm = st.getMethod(m.getId(), p);
		if (pm == null || pm.getParamsSize() != m.getParamsSize()) {
			// Overloaded method, Error caught in Name Analysis
			return;
		}

		for (int i = 0; i < md.getArgListSize(); i++) {
			ArgDecl ad = md.getArgDeclAt(i);
			Type t1 = ad.accept(this);
			Variable v = pm.getParamAt(i);
			Type t2 = v.getType();
			if (!st.absCompTypes(t1, t2)) {
				Token sym = ad.getToken();
				addError(sym.getRow(), sym.getCol(), "cannot override method " + pm.getId()
						+ "; attempting to use incompatible type for parameter " + ad.getId());
				return;
			}
		}

		if (!st.absCompTypes(m.getType(), pm.getType())) {
			Token sym = md.getReturnType().getToken();
			addError(sym.getRow(), sym.getCol(),
					"cannot override method " + pm.getId() + "; attempting to use incompatible return type");
		}
	}

	@Override
	public Type visit(FuncDeclReturn funcDeclStandard)
	{
		String id = funcDeclStandard.getMethodName().getVarID();
		if (hsFunc.contains(id)) { // Duplicate Method
			return funcDeclStandard.getReturnType();
		}
		hsFunc.add(id);

		currFunc = (Function) funcDeclStandard.getMethodName().getB();

		checkOverriding(funcDeclStandard, currFunc);

		for (int i = 0; i < funcDeclStandard.getVarListSize(); i++) {
			Declaration decl = funcDeclStandard.getVarDeclAt(i);
			decl.accept(this);
		}

		for (int i = 0; i < funcDeclStandard.getStatListSize(); i++) {
			Statement st = funcDeclStandard.getStatAt(i);
			st.accept(this);
		}

		Type t1 = funcDeclStandard.getReturnType();
		Type t2 = funcDeclStandard.getReturnExpr().accept(this);

		if (!st.compareTypes(t1, t2)) {
			Token sym = funcDeclStandard.getReturnExpr().getToken();
			addError(sym.getRow(), sym.getCol(), "Method " + id + " must return a result of Type " + t1);
		}

		funcDeclStandard.getReturnExpr().setType(t2);
		currFunc = null;
		return funcDeclStandard.getReturnType();
	}

	@Override
	public Type visit(FuncDeclVoid funcDeclStandard)
	{
		String id = funcDeclStandard.getMethodName().getVarID();
		if (hsFunc.contains(id)) { // Duplicate Method
			return funcDeclStandard.getReturnType();
		}
		hsFunc.add(id);

		currFunc = (Function) funcDeclStandard.getMethodName().getB();

		checkOverriding(funcDeclStandard, currFunc);

		for (int i = 0; i < funcDeclStandard.getVarListSize(); i++) {
			Declaration decl = funcDeclStandard.getVarDeclAt(i);
			decl.accept(this);
		}

		for (int i = 0; i < funcDeclStandard.getStatListSize(); i++) {
			Statement st = funcDeclStandard.getStatAt(i);
			st.accept(this);
		}

		currFunc = null;
		return funcDeclStandard.getReturnType();
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
	public Type visit(Identifier id)
	{
		Binding b = id.getB();
		if (b != null) {
			return ((Variable) b).getType();
		}
		return null;
	}

	@Override
	public Type visit(IndexArray ia)
	{
		// Check array type
		Type tid = ia.getArray().accept(this);
		if (tid == null || !(tid instanceof IntArrayType)) {
			Token sym = ia.getArray().getToken();
			addError(sym.getRow(), sym.getCol(), "Array expression must evaluate to be of Type int[]");
		} else {
			ia.getArray().setType(tid);
		}

		// Check index type
		Type tin = ia.getIndex().accept(this);
		if (tin == null || !(tin instanceof IntType)) {
			Token sym = ia.getIndex().getToken();
			addError(sym.getRow(), sym.getCol(), "Index expression must evaluate to be of Type int");
		} else {
			ia.getIndex().setType(tin);
		}

		Type t = new IntType(ia.getToken());
		ia.setType(t);
		return t;
	}

	@Override
	public Type visit(ArrayAssign aa)
	{
		// Check identifier type
		Type tid = aa.getIdentifier().accept(this);
		if (tid == null || !(tid instanceof IntArrayType)) {
			Token sym = aa.getIdentifier().getToken();
			addError(sym.getRow(), sym.getCol(), "Identifier must be of Type int[]");
		}

		// Check expression type
		Type texp1 = aa.getE1().accept(this);
		if (texp1 == null || !(texp1 instanceof IntType)) {
			Token sym = aa.getE1().getToken();
			addError(sym.getRow(), sym.getCol(), "Expression must be of Type int");
		} else {
			aa.getE1().setType(texp1);
		}

		// Check assigned expression type
		Type texp2 = aa.getE2().accept(this);
		if (texp2 == null || !(texp2 instanceof IntType)) {
			Token sym = aa.getE2().getToken();
			addError(sym.getRow(), sym.getCol(), "Expression must be of Type int");
		} else {
			aa.getE2().setType(texp2);
		}

		return null;
	}

	@Override
	public Type visit(StringLiteral stringLiteral)
	{
		Type t = new StringType(stringLiteral.getToken());
		stringLiteral.setType(t);
		return t;
	}

	@Override
	public Type visit(ClassDeclSimple cd)
	{
		String id = cd.getId().getVarID();
		if (hsKlass.contains(id)) {
			return null;
		}
		hsKlass.add(id);

		Binding b = cd.getId().getB();
		currClass = (Klass) b;

		hsFunc.clear();

		for (int i = 0; i < cd.getVarListSize(); i++) {
			Declaration decl = cd.getVarDeclAt(i);
			decl.accept(this);
		}

		for (int i = 0; i < cd.getMethodListSize(); i++) {
			FuncDecl md = cd.getMethodDeclAt(i);
			md.accept(this);
		}

		return null;
	}

	@Override
	public Type visit(ClassDeclExtends cd)
	{
		String id = cd.getId().getVarID();
		if (hsKlass.contains(id)) { // Duplicate class
			return null;
		}
		hsKlass.add(id);
		Binding b = cd.getId().getB();
		currClass = (Klass) b;

		hsFunc.clear();
		for (int i = 0; i < cd.getMethodListSize(); i++) {
			FuncDecl md = cd.getMethodDeclAt(i);
			md.accept(this);
		}

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