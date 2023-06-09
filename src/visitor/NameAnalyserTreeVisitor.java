package src.visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import src.ast.*;
import src.lexer.*;
import src.semantics.*;
import src.symbol.*;

public class NameAnalyserTreeVisitor implements Visitor<Type>
{
	SymbolTable symbolTable;
	private Klass currClass;
	private Function currFunc;
	private Set<String> hsKlass = new HashSet<>();
	private Set<String> hsFunc = new HashSet<>();

	public NameAnalyserTreeVisitor(SymbolTable table)
	{
		this.symbolTable = table;
	}

	@Override
	public Type visit(Assign n)
	{
		n.getId().accept(this);
		n.getExpr().accept(this);
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
		n.getExpr().accept(this);
		n.getThen().accept(this);
		n.getElze().accept(this);
		return null;
	}

	@Override
	public Type visit(Skip skip)
	{
		return null;
	}

	@Override
	public Type visit(While n)
	{
		n.getExpr().accept(this);
		n.getBody().accept(this);
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
		n.getLhs().accept(this);
		n.getRhs().accept(this);
		return null;
	}

	@Override
	public Type visit(Minus n)
	{
		n.getLhs().accept(this);
		n.getRhs().accept(this);
		return null;
	}

	@Override
	public Type visit(Times n)
	{
		n.getLhs().accept(this);
		n.getRhs().accept(this);
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
	public Type visit(NewArray na)
	{
		na.getArrayLength().accept(this);
		return null;
	}

	@Override
	public Type visit(NewInstance ni)
	{
		String id = ni.getClassName().getVarID();
		Klass klass = symbolTable.getKlass(id);
		if (klass == null) {
			Token sym = ni.getClassName().getToken();
		 	addError(sym.getRow(), sym.getCol(), "class " + id + " is not declared");
		}

		ni.getClassName().setB(klass);
		return null;
	}

	@Override
	public Type visit(CallFunctionExpr cm)
	{
		/*
		 * The call function expression can be written like this 'object.functionName()'. Here is object the instancename,
		 * but if there is no instancename 'functionName()', the instancename will be null. So This check needs to be done.
		 */
		if (cm.getInstanceName() != null) {
			cm.getInstanceName().accept(this);
		}

		for (int i = 0; i < cm.getArgExprListSize(); i++) {
			Expression e = cm.getArgExprAt(i);
			e.accept(this);
		}

		return null;
	}

	@Override
	public Type visit(CallFunctionStat cm)
	{
		return null;
	}

	@Override
	public Type visit(IntType intType)
	{
		return null;
	}

	@Override
	public Type visit(StringType stringType)
	{
		return null;
	}

	@Override
	public Type visit(VoidType voidType)
	{
		return null;
	}

	@Override
	public Type visit(BooleanType booleanType)
	{
		return null;
	}

	@Override
	public Type visit(IntArrayType intArrayType)
	{
		return null;
	}

	@Override
	public Type visit(Identifier i)
	{
		String id = i.getVarID();
		Variable var = symbolTable.getVar(currFunc, currClass, id);
		Function func = symbolTable.getFunction(id, currClass.getId());

		if (var == null && func == null) {
			Token sym = i.getToken();
			addError(sym.getRow(), sym.getCol(), "variable " + id + " is not declared");
		}
		
		if (var != null) {
			i.setB(var);
		} else {
			i.setB(func);
		}

		return null;
	}

	@Override
	public Type visit(IdentifierType ref)
	{
		String id = ref.getVarID();
		Klass klass = symbolTable.getKlass(id);
		if (klass == null) {
			Token sym = ref.getToken();
			addError(sym.getRow(), sym.getCol(), "class " + id + " is not declared");
		}

		ref.setB(klass);
		return null;
	}

	@Override
	public Type visit(IdentifierExpr i)
	{
		String id = i.getVarID();
		Variable var = symbolTable.getVar(currFunc, currClass, id);
		if (var == null) {
			Token sym = i.getToken();
			addError(sym.getRow(), sym.getCol(), "variable " + id + " is not declared");
		}
		
		i.setB(var);
		return null;
	}

	@Override
	public Type visit(VarDecl vd)
	{
		vd.getType().accept(this);
		vd.getId().accept(this);

		String id = vd.getId().getVarID();

		if (currFunc == null) {
			Klass parent = symbolTable.getKlass(currClass.parent());
			if (symbolTable.containsVar(null, parent, id)) {
				Token sym = vd.getId().getToken();
				addError(sym.getRow(), sym.getCol(), "Variable " + id + " already defined in parent class");
			}
		}

		return null;
	}

	
	@Override
	public Type visit(VarDeclInit vd)
	{
		String id = vd.getId().getVarID(); // Get the name of the variable declaration.

		vd.getType().accept(this); 
		vd.getId().accept(this);

		if (vd.getExpr() instanceof FunctionExprReturn || vd.getExpr() instanceof FunctionExprVoid) { 
			if (hsFunc.contains(id)) { // Check if function already exist
				return null;
			} else {
				hsFunc.add(id); // Else add the function to the List.
			}

			currFunc = currClass.getFunction(id); // Set the current functon to this function.
			vd.getId().setB(currFunc); // Set the identifier of this declaration connected to the current function, because this declaration is a function.
		}

		if (currFunc == null) {
			Klass parent = symbolTable.getKlass(currClass.parent());
			if (symbolTable.containsVar(null, parent, id)) {
				Token sym = vd.getId().getToken();
				addError(sym.getRow(), sym.getCol(), "Variable " + id + " already defined in parent class");
			}
		}

		vd.getExpr().accept(this);

		return null;
	}

	@Override
	public Type visit(ArgDecl ad)
	{
		ad.getId().accept(this);
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
		for (int i = 0; i < funcExprVoid.getArgListSize(); i++) {
			ArgDecl ad = funcExprVoid.getArgDeclAt(i);
			ad.accept(this);
		}

		for (int i = 0; i < funcExprVoid.getVarListSize(); i++) {
			Declaration vd = funcExprVoid.getVarDeclAt(i);
			vd.accept(this);
		}

		for (int i = 0; i < funcExprVoid.getStatListSize(); i++) {
			Statement st = funcExprVoid.getStatAt(i);
			st.accept(this);
		}

		currFunc = null;
		return null;
	}

	@Override
	public Type visit(Program program)
	{
		checkInheritanceCycle(program);

		for (int i = 0; i < program.getClassListSize(); i++) {
			program.getClassList().get(i).accept(this);
		}
		return null;
	}

	private void checkInheritanceCycle(Program p)
	{
		Map<String, List<String>> adjList = new HashMap<>();

		for (int i = 0; i < p.getClassListSize(); i++) {
			ClassDecl cd = p.getClassDeclAt(i);
			if (cd instanceof ClassDeclExtends) {
				String cid = ((ClassDeclExtends) cd).getId().getVarID();
				String pid = ((ClassDeclExtends) cd).getParent().getVarID();
				List<String> cList = adjList.get(pid);
				if (cList == null) {
					cList = new ArrayList<String>();
					adjList.put(pid, cList);
				}
				cList.add(cid);
			}
		}

		String[] nodes = adjList.keySet().toArray(new String[0]);
		List<String> visited = new ArrayList<>();
		for (int i = 0; i < nodes.length; i++) {
			if (!visited.contains(nodes[i])) {
				List<String> rStack = new ArrayList<>();
				dfs(nodes[i], adjList, visited, rStack, p);
			}
		}
	}

	private void dfs(String src, Map<String, List<String>> adjList, List<String> visited, List<String> rStack, Program p)
	{
		visited.add(src);
		rStack.add(src);
		List<String> cList = adjList.get(src);
		if (cList == null) {
			return;
		}

		for (String cid : cList) {
			if (!visited.contains(cid)) {
				dfs(cid, adjList, visited, rStack, p);
			} else if (rStack.contains(cid)) {
				Token sym = getKlassSymbol(cid, p);
				if (sym != null) {
					addError(sym.getRow(), sym.getCol(), "Inheretence cycle found in class hierarchy class " + cid + " extends class " + src);
				} else {
					addError(0, 0, "Inheretence cycle found in class hierarchy class " + cid + " extends parent " + src);
				}
			}
		}
	}

	public Token getKlassSymbol(String id, Program p)
	{
		for (int i = 0; i < p.getClassListSize(); i++) {
			ClassDecl cd = p.getClassDeclAt(i);
			if (cd instanceof ClassDeclSimple) {
				if (id.equals(((ClassDeclSimple) cd).getId().getVarID())) {
					return cd.getToken();
				}
			}

			if (cd instanceof ClassDeclExtends) {
				if (id.equals(((ClassDeclExtends) cd).getId().getVarID())) {
					return cd.getToken();
				}
			}
		}
		return null;
	}

	@Override
	public Type visit(ArrayIndexExpr ia)
	{
		ia.getArray().accept(this);
		ia.getIndex().accept(this);
		return null;
	}

	@Override
	public Type visit(ArrayAssign aa)
	{
		aa.getIdentifier().accept(this);
		aa.getE1().accept(this);
		aa.getE2().accept(this);
		return null;
	}

	@Override
	public Type visit(StringLiteral stringLiteral)
	{
		return null;
	}

	@Override
	public Type visit(ClassDeclSimple cd)
	{
		String id = cd.getId().getVarID();
		if (hsKlass.contains(id)) { 
			return null;
		} else {
			hsKlass.add(id);
		}

		currClass = symbolTable.getKlass(id);
		cd.getId().setB(currClass);

		for (int i = 0; i < cd.getVarListSize(); i++) {
			Declaration vd = cd.getVarDeclAt(i);
			vd.accept(this);
		}

		hsFunc.clear();

		return null;
	}

	@Override
	public Type visit(ClassDeclExtends cd)
	{
		String id = cd.getId().getVarID();
		if (hsKlass.contains(id)) {
			return null;
		} else {
			hsKlass.add(id);
		}

		currClass = symbolTable.getKlass(id);
		cd.getId().setB(currClass);

		String parent = currClass.parent();
		Klass parentKlass = symbolTable.getKlass(parent);
		if (parentKlass == null) {
			Token sym = cd.getParent().getToken();
			addError(sym.getRow(), sym.getCol(), "parent class " + parent + " not declared");
		} else {
			cd.getParent().setB(parentKlass);
		}

		for (int i = 0; i < cd.getVarListSize(); i++) {
			Declaration vd = cd.getVarDeclAt(i);
			vd.accept(this);
		}

		hsFunc.clear();

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