package src.visitor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import src.ast.*;
import src.ast.Class;
import src.lexer.*;
import src.semantics.*;

import src.symbol.SymbolClass;
import src.symbol.SymbolFunction;
import src.symbol.SymbolProgram;
import src.symbol.SymbolVariable;

public class TypeAnalyser implements Visitor<Type>
{
	private SymbolProgram symbolProgram;
	private SymbolClass symbolClass;
	private SymbolFunction symbolFunction;

	private Set<String> hsymbolClass = new HashSet<>();
	private Set<String> hsymbolFunction = new HashSet<>();

	public TypeAnalyser(SymbolProgram symbolProgram)
	{
		this.symbolProgram = symbolProgram;
	}

	@Override
	public Type visit(Include include)
	{
		return null;
	}

	@Override
	public Type visit(Assign n)
	{
		Type rhs = n.getExpr().accept(this);
		Type lhs = n.getId().accept(this);

		if (!symbolProgram.compareTypes(lhs, rhs)) {
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
	public Type visit(Skip skip)
	{
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
	public Type visit(Increment increment)
	{
		Type expr = increment.getExpr().accept(this);

		if (!(expr instanceof IntType)) {
			Token sym = increment.getExpr().getToken();
			addError(sym.getRow(), sym.getCol(), "Operator ++ cannot be applied to " + expr);
		}

		Type t = new IntType(increment.getToken());
		increment.setType(t);
		return t;
	}

	@Override
	public Type visit(Modulus modulus)
	{
		Type lhs = modulus.getLhs().accept(this);
		Type rhs = modulus.getRhs().accept(this);

		if (lhs == null || rhs == null) {
			Token sym = modulus.getToken();
			addError(sym.getRow(), sym.getCol(), "Improper Type used with % operator");
			return new IntType(modulus.getToken());
		}

		if (!(lhs instanceof IntType) || !(rhs instanceof IntType)) {
			Token sym = modulus.getLhs().getToken();
			addError(sym.getRow(), sym.getCol(), "Operator % cannot be applied to " + lhs + ", " + rhs);
		}

		Type t = new IntType(modulus.getToken());
		modulus.setType(t);
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
	public Type visit(LessThan lessThan)
	{
		Type t1 = lessThan.getLhs().accept(this);
		Type t2 = lessThan.getRhs().accept(this);

		if (t1 == null || t2 == null) {
			Token sym = lessThan.getToken();
			addError(sym.getRow(), sym.getCol(), "Incorrect types used with < oprator");
		} else if (!(t1 instanceof IntType) || !(t2 instanceof IntType)) {
			Token sym = lessThan.getToken();
			addError(sym.getRow(), sym.getCol(), "Operator < cannot be applied to " + t1 + ", " + t2);
		}

		Type t = new BooleanType(lessThan.getToken());
		lessThan.setType(t);
		return t;
	}

	@Override
	public Type visit(LessThanOrEqual lessThanOrEqual)
	{
		Type t1 = lessThanOrEqual.getLhs().accept(this);
		Type t2 = lessThanOrEqual.getRhs().accept(this);

		if (t1 == null || t2 == null) {
			Token sym = lessThanOrEqual.getToken();
			addError(sym.getRow(), sym.getCol(), "Incorrect types used with <= oprator");
		} else if (!(t1 instanceof IntType) || !(t2 instanceof IntType)) {
			Token sym = lessThanOrEqual.getToken();
			addError(sym.getRow(), sym.getCol(), "Operator <= cannot be applied to " + t1 + ", " + t2);
		}
		Type t = new BooleanType(lessThanOrEqual.getToken());
		lessThanOrEqual.setType(t);
		return t;
	}

	@Override
	public Type visit(GreaterThan greaterThan)
	{
		Type t1 = greaterThan.getLhs().accept(this);
		Type t2 = greaterThan.getRhs().accept(this);

		if (t1 == null || t2 == null) {
			Token sym = greaterThan.getToken();
			addError(sym.getRow(), sym.getCol(), "Incorrect types used with > oprator");
		} else if (!(t1 instanceof IntType) || !(t2 instanceof IntType)) {
			Token sym = greaterThan.getToken();
			addError(sym.getRow(), sym.getCol(), "Operator > cannot be applied to " + t1 + ", " + t2);
		}
		Type t = new BooleanType(greaterThan.getToken());
		greaterThan.setType(t);
		return t;
	}

	@Override
	public Type visit(GreaterThanOrEqual greaterThanOrEqual)
	{
		Type t1 = greaterThanOrEqual.getLhs().accept(this);
		Type t2 = greaterThanOrEqual.getRhs().accept(this);

		if (t1 == null || t2 == null) {
			Token sym = greaterThanOrEqual.getToken();
			addError(sym.getRow(), sym.getCol(), "Incorrect types used with >= oprator");
		} else if (!(t1 instanceof IntType) || !(t2 instanceof IntType)) {
			Token sym = greaterThanOrEqual.getToken();
			addError(sym.getRow(), sym.getCol(), "Operator >= cannot be applied to " + t1 + ", " + t2);
		}
		Type t = new BooleanType(greaterThanOrEqual.getToken());
		greaterThanOrEqual.setType(t);
		return t;
	}

	@Override
	public Type visit(And and)
	{
		Type t1 = and.getLhs().accept(this);
		Type t2 = and.getRhs().accept(this);

		if (t1 == null || t2 == null) {
			Token sym = and.getToken();
			addError(sym.getRow(), sym.getCol(), "Incorrect types used with && oprator");
		} else if (!(t1 instanceof BooleanType) || !(t2 instanceof BooleanType)) {
			Token sym = and.getLhs().getToken();
			addError(sym.getRow(), sym.getCol(), "Operator && cannot be applied to " + t1 + ", " + t2);
		}
		Type t = new BooleanType(and.getToken());
		and.setType(t);
		return t;
	}

	@Override
	public Type visit(Or or)
	{
		Type t1 = or.getLhs().accept(this);
		Type t2 = or.getRhs().accept(this);

		if (t1 == null || t2 == null) {
			Token sym = or.getToken();
			addError(sym.getRow(), sym.getCol(), "Incorrect types used with || oprator");
		} else if (!(t1 instanceof BooleanType) || !(t2 instanceof BooleanType)) {
			Token sym = or.getLhs().getToken();
			addError(sym.getRow(), sym.getCol(), "Operator || cannot be applied to " + t1 + ", " + t2);
		}
		Type t = new BooleanType(or.getToken());
		or.setType(t);
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
			Type t = ((SymbolVariable) b).getType();
			i.setType(t);
			return t;
		}
		return null;
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
			SymbolClass klass = (SymbolClass) b;
			ni.setType(klass.type());
			return klass.type();
		}
		return new IdentifierType(ni.getToken(), ni.getClassName().getVarID());
	}

	@Override
	public Type visit(CallFunctionExpr cm)
	{
		if (cm.getInstanceName() == null) {
			IdentifierExpr callFunc = cm.getMethodId();

			SymbolFunction func = null;
			if (symbolClass == null) {
				func = symbolProgram.getFunction(callFunc.toString());
			} else {
				func = symbolProgram.getFunction(callFunc.toString(), symbolClass.getId());
			}
			
			if (func == null) {
				Token sym = callFunc.getToken();
				addError(sym.getRow(), sym.getCol(), "Method " + callFunc + " not declared");
				return null;
			} else {
				callFunc.setB(func);
				checkCallArguments(cm, func);
				cm.setType(func.getType());
				return func.getType();
			}
		} 
		return null;
	}

	@Override
	public Type visit(CallFunctionStat cm)
	{
		for (int i = 0; i < cm.getArgExprListSize(); i++) {
			cm.getArgExprAt(i).accept(this);
		}

		return null;
	}

	private void checkCallArguments(CallFunctionExpr cm, SymbolFunction m)
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
			SymbolVariable var = m.getParamAt(i);
			Type t1 = var.getType();
			Type t2 = argTypes.get(i);

			if (!symbolProgram.compareTypes(t1, t2)) {
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
	public Type visit(FunctionType functionType)
	{
		return functionType;
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
	public Type visit(Variable vd)
	{
		
		return vd.getType();
	}

	@Override
	public Type visit(VariableInit vd)
	{
		Type rhs = vd.getExpr().accept(this);
		Type lhs = vd.getId().accept(this);

		if (vd.getExpr() instanceof CallFunctionExpr) {
			CallFunctionExpr callFunc = (CallFunctionExpr) vd.getExpr();
			if (callFunc.getInstanceName() == null) {
				SymbolFunction func = null;
				if (symbolClass != null) {
					func = symbolProgram.getFunction(callFunc.getMethodId().toString(), symbolClass.getId());
				} else {
					func = symbolProgram.getFunction(callFunc.getMethodId().toString());
				}
				
				if (func != null) {
					rhs = func.getType();
				}
			} else {
				return null;
			}
		}

		if (!symbolProgram.compareTypes(lhs, rhs)) {
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
	public Type visit(Argument ad)
	{
		return ad.getType();
	}

	@Override
	public Type visit(Function functionDecl)
	{
		String functionName = functionDecl.getId().getVarID();

		if (hsymbolFunction.contains(functionName)) {
			return functionDecl.getReturnType();
		}

		if (!(functionDecl.getReturnType() instanceof VoidType)) {
			Token tok = functionDecl.getReturnType().getToken();
			addError(tok.getRow(), tok.getCol(), "Function " + functionName + " must return a result of Type " + functionDecl.getReturnType());
		}

		hsymbolFunction.add(functionName);

		symbolFunction = (SymbolFunction) functionDecl.getId().getB();

		for (int i = 0; i < functionDecl.getArgumentListSize(); i++) {
			functionDecl.getArgumentDeclAt(i).accept(this);
		}

		for (int i = 0; i < functionDecl.getVariableListSize(); i++) {
			functionDecl.getVariableDeclAt(i).accept(this);
		}

		for (int i = 0; i < functionDecl.getStatementListSize(); i++) {
			functionDecl.getStatementDeclAt(i).accept(this);
		}

		symbolFunction = null;
		return null;
	}

	@Override
	public Type visit(FunctionReturn functionReturn)
	{
		String functionName = functionReturn.getId().getVarID();

		if (hsymbolFunction.contains(functionName)) {
			return functionReturn.getReturnType();
		}

		hsymbolFunction.add(functionName);

		symbolFunction = (SymbolFunction) functionReturn.getId().getB();

		for (int i = 0; i < functionReturn.getArgumentListSize(); i++) {
			functionReturn.getArgumentDeclAt(i).accept(this);
		}

		for (int i = 0; i < functionReturn.getVariableListSize(); i++) {
			functionReturn.getVariableDeclAt(i).accept(this);
		}

		for (int i = 0; i < functionReturn.getStatementListSize(); i++) {
			functionReturn.getStatementDeclAt(i).accept(this);
		}

		Type t1 = functionReturn.getReturnType();
		Type t2 = functionReturn.getReturnExpr().accept(this);

		if (!symbolProgram.compareTypes(t1, t2)) {
			Token tok = functionReturn.getReturnExpr().getToken();
			addError(tok.getRow(), tok.getCol(), "Function " + functionName + " must return a result of Type " + t1);
		}

		functionReturn.getReturnExpr().setType(t2);
		symbolFunction = null;
		return null;
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
	public Type visit(Identifier identifier)
	{
		Binding b = identifier.getB();
		if (b != null) {
			return ((SymbolVariable) b).getType();
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
	public Type visit(ArrayIndexExpr arrayIndexExpr)
	{
		// Check array type
		Type tid = arrayIndexExpr.getArray().accept(this);
		if (tid == null || !(tid instanceof IntArrayType)) {
			Token sym = arrayIndexExpr.getArray().getToken();
			addError(sym.getRow(), sym.getCol(), "Array expression must evaluate to be of Type int[]");
		} else {
			arrayIndexExpr.getArray().setType(tid);
		}

		// Check index type
		Type tin = arrayIndexExpr.getIndex().accept(this);
		if (tin == null || !(tin instanceof IntType)) {
			Token sym = arrayIndexExpr.getIndex().getToken();
			addError(sym.getRow(), sym.getCol(), "Index expression must evaluate to be of Type int");
		} else {
			arrayIndexExpr.getIndex().setType(tin);
		}

		Type t = new IntType(arrayIndexExpr.getToken());
		arrayIndexExpr.setType(t);
		return t;
	}

	@Override
	public Type visit(ArrayAssign arrayAssign)
	{
		// Check identifier type
		Type tid = arrayAssign.getIdentifier().accept(this);
		if (tid == null || !(tid instanceof IntArrayType)) {
			Token sym = arrayAssign.getIdentifier().getToken();
			addError(sym.getRow(), sym.getCol(), "Identifier must be of Type int[]");
		}

		// Check expression type
		Type texp1 = arrayAssign.getE1().accept(this);
		if (texp1 == null || !(texp1 instanceof IntType)) {
			Token sym = arrayAssign.getE1().getToken();
			addError(sym.getRow(), sym.getCol(), "Expression must be of Type int");
		} else {
			arrayAssign.getE1().setType(texp1);
		}

		// Check assigned expression type
		Type texp2 = arrayAssign.getE2().accept(this);
		if (texp2 == null || !(texp2 instanceof IntType)) {
			Token sym = arrayAssign.getE2().getToken();
			addError(sym.getRow(), sym.getCol(), "Expression must be of Type int");
		} else {
			arrayAssign.getE2().setType(texp2);
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
	public Type visit(Class classDecl)
	{
		String id = classDecl.getId().getVarID();
		if (hsymbolClass.contains(id)) {
			return null;
		}
		hsymbolClass.add(id);

		Binding b = classDecl.getId().getB();
		symbolClass = (SymbolClass) b;

		hsymbolFunction.clear();

		for (int i = 0; i < classDecl.getVariableListSize(); i++) {
			classDecl.getVariableDeclAt(i).accept(this);
		}

		for (int i = 0; i < classDecl.getFunctionListSize(); i++) {
			classDecl.getFunctionDeclAt(i).accept(this);
		}

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