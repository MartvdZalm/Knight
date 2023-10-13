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
	private SProgram sProgram;
	private SClass sClass;
	private SFunction sFunction;

	private Set<String> hsClass = new HashSet<>();
	private Set<String> hsFunction = new HashSet<>();

	public NameAnalyserTreeVisitor(SProgram sProgram)
	{
		this.sProgram = sProgram;
	}

	@Override
	public Type visit(Include include)
	{
		return null;
	}

	@Override
	public Type visit(Assign assign)
	{
		assign.getId().accept(this);
		assign.getExpr().accept(this);
		return null;
	}
	
	@Override
	public Type visit(Block block)
	{
		for (int i = 0; i < block.getStatListSize(); i++) {
			Statement st = block.getStatAt(i);
			st.accept(this);
		}
		return null;
	}

	@Override
	public Type visit(IfThenElse ifThenElse)
	{
		ifThenElse.getExpr().accept(this);
		ifThenElse.getThen().accept(this);
		ifThenElse.getElze().accept(this);
		return null;
	}

	@Override
	public Type visit(Skip skip)
	{
		return null;
	}

	@Override
	public Type visit(While while1)
	{
		while1.getExpr().accept(this);
		while1.getBody().accept(this);
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
		plus.getLhs().accept(this);
		plus.getRhs().accept(this);
		return null;
	}

	@Override
	public Type visit(Minus minus)
	{
		minus.getLhs().accept(this);
		minus.getRhs().accept(this);
		return null;
	}

	@Override
	public Type visit(Times times)
	{
		times.getLhs().accept(this);
		times.getRhs().accept(this);
		return null;
	}

	@Override
	public Type visit(Increment increment)
	{
		increment.getExpr().accept(this);
		return null;
	}

	@Override
	public Type visit(Modulus modulus)
	{
		modulus.getLhs().accept(this);
		modulus.getRhs().accept(this);
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
	public Type visit(NewArray newArray)
	{
		newArray.getArrayLength().accept(this);
		return null;
	}

	@Override
	public Type visit(NewInstance ni)
	{
		String id = ni.getClassName().getVarID();
		SClass klass = sProgram.getClass(id);
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
		for (int i = 0; i < cm.getArgExprListSize(); i++) {
			Expression e = cm.getArgExprAt(i);
			e.accept(this);
		}
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
	public Type visit(Identifier identifier)
	{
		String id = identifier.getVarID();
		SVariable var = sProgram.getVariable(id, sClass, sFunction);

		if (var == null) {
			Token sym = identifier.getToken();
			addError(sym.getRow(), sym.getCol(), "variable " + id + " is not declared");
		}

		identifier.setB(var);
		return null;
	}

	@Override
	public Type visit(IdentifierType identifierType)
	{
		String id = identifierType.getVarID();
		SClass klass = sProgram.getClass(id);
		if (klass == null) {
			Token sym = identifierType.getToken();
			addError(sym.getRow(), sym.getCol(), "class " + id + " is not declared");
		}

		identifierType.setB(klass);
		return null;
	}

	@Override
	public Type visit(IdentifierExpr identifierExpr)
	{
		String id = identifierExpr.getVarID();
		SVariable var = sProgram.getVariable(id, sClass, sFunction);
		if (var == null) {
			Token sym = identifierExpr.getToken();
			addError(sym.getRow(), sym.getCol(), "variable " + id + " is not declared");
		}
		
		identifierExpr.setB(var);
		return null;
	}

	public void checkVariable(VarDecl varDecl)
	{
		String id = varDecl.getId().getVarID();
		
		varDecl.getType().accept(this);
		varDecl.getId().accept(this);
	}

	@Override
	public Type visit(VarDeclNoInit varDeclNoInit)
	{	
		checkVariable(varDeclNoInit);
		return null;
	}
	
	@Override
	public Type visit(VarDeclInit varDeclInit)
	{
		checkVariable(varDeclInit);
		varDeclInit.getExpr().accept(this);
		return null;
	}

	@Override
	public Type visit(ArgDecl ad)
	{
		ad.getId().accept(this);
		return null;
	}

	public void checkFunction(FunctionDecl functionDecl)
	{
		String functionName = functionDecl.getFunctionName().getVarID();

		if (hsFunction.contains(functionName)) {
			return;
		} else {
			hsFunction.add(functionName);
		}

		functionDecl.getReturnType().accept(this);

		if (sClass == null) {
			sFunction = sProgram.getFunction(functionName);
		} else {
			sFunction = sClass.getFunction(functionName);
		}

		functionDecl.getFunctionName().setB(sFunction);

		for (int i = 0; i < functionDecl.getArgListSize(); i++) {
			ArgDecl ad = functionDecl.getArgDeclAt(i);
			ad.accept(this);
		}

		for (int i = 0; i < functionDecl.getDeclListSize(); i++) {
			Declaration vd = functionDecl.getDeclAt(i);
			vd.accept(this);
		}
	}

	@Override
	public Type visit(FunctionVoid functionVoid)
	{
		checkFunction(functionVoid);
		sFunction = null;
		return null;
	}

	@Override
	public Type visit(FunctionReturn functionReturn)
	{
		checkFunction(functionReturn);
		functionReturn.getReturnExpr().accept(this);
		sFunction = null;
		return null;
	}

	@Override
	public Type visit(Program program)
	{
		//checkInheritanceCycle(program);

		for (int i = 0; i < program.getDeclListSize(); i++) {
			program.getDeclAt(i).accept(this);
		}
		return null;
	}

	// private void checkInheritanceCycle(Program program)
	// {
	// 	Map<String, List<String>> adjList = new HashMap<>();

	// 	for (int i = 0; i < program.getClassListSize(); i++) {
	// 		ClassDecl cd = program.getClassDeclAt(i);
	// 		if (cd instanceof ClassDeclExtends) {
	// 			String cid = ((ClassDeclExtends) cd).getId().getVarID();
	// 			String pid = ((ClassDeclExtends) cd).getParent().getId().getVarID();
	// 			List<String> cList = adjList.get(pid);
	// 			if (cList == null) {
	// 				cList = new ArrayList<String>();
	// 				adjList.put(pid, cList);
	// 			}
	// 			cList.add(cid);
	// 		}
	// 	}

	// 	String[] nodes = adjList.keySet().toArray(new String[0]);
	// 	List<String> visited = new ArrayList<>();
	// 	for (int i = 0; i < nodes.length; i++) {
	// 		if (!visited.contains(nodes[i])) {
	// 			List<String> rStack = new ArrayList<>();
	// 			dfs(nodes[i], adjList, visited, rStack, program);
	// 		}
	// 	}
	// }

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
				Token sym = getClassSymbol(cid, p);
				if (sym != null) {
					addError(sym.getRow(), sym.getCol(), "Inheretence cycle found in class hierarchy class " + cid + " extends class " + src);
				} else {
					addError(0, 0, "Inheretence cycle found in class hierarchy class " + cid + " extends parent " + src);
				}
			}
		}
	}

	public Token getClassSymbol(String id, Program p)
	{
		for (int i = 0; i < p.getDeclListSize(); i++) {
			Declaration cd = p.getDeclAt(i);
			if (cd instanceof ClassDecl) {
				if (id.equals(((ClassDecl) cd).getId().getVarID())) {
					return cd.getToken();
				}
			}

			if (cd instanceof ClassDeclInheritance) {
				if (id.equals(((ClassDeclInheritance) cd).getId().getVarID())) {
					return cd.getToken();
				}
			}
		}
		return null;
	}

	@Override
	public Type visit(ReturnStatement returnStatement)
	{
		returnStatement.getReturnExpr().accept(this);
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
	public Type visit(ClassDecl cd)
	{
		String id = cd.getId().getVarID();
		if (hsClass.contains(id)) { 
			return null;
		} else {
			hsClass.add(id);
		}

		sClass = sProgram.getClass(id);
		cd.getId().setB(sClass);

		for (int i = 0; i < cd.getDeclListSize(); i++) {
			Declaration vd = cd.getDeclAt(i);
			vd.accept(this);
		}

		hsFunction.clear();

		return null;
	}

	@Override
	public Type visit(ClassDeclInheritance cd)
	{
		String id = cd.getId().getVarID();
		if (hsClass.contains(id)) {
			return null;
		} else {
			hsClass.add(id);
		}

		sClass = sProgram.getClass(id);
		cd.getId().setB(sClass);

		String parent = sClass.parent();
		SClass parentKlass = sProgram.getClass(parent);
		if (parentKlass == null) {
			Token sym = cd.getParent().getToken();
			addError(sym.getRow(), sym.getCol(), "parent class " + parent + " not declared");
		} else {
			cd.getParent().getId().setB(parentKlass);
		}

		for (int i = 0; i < cd.getDeclListSize(); i++) {
			Declaration vd = cd.getDeclAt(i);
			vd.accept(this);
		}

		hsFunction.clear();

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

	@Override
	public Type visit(Extends extends1) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'visit'");
	}

	@Override
	public Type visit(Implements implements1) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'visit'");
	}
}