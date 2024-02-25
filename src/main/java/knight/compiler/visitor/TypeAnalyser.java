package knight.compiler.visitor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import knight.compiler.ast.declarations.*;
import knight.compiler.ast.expressions.*;
import knight.compiler.ast.expressions.operations.*;
import knight.compiler.ast.statements.*;
import knight.compiler.ast.statements.conditionals.*;
import knight.compiler.ast.types.*;
import knight.compiler.ast.*;

import knight.compiler.lexer.*;
import knight.compiler.semantics.*;

import knight.compiler.symbol.SymbolClass;
import knight.compiler.symbol.SymbolFunction;
import knight.compiler.symbol.SymbolProgram;
import knight.compiler.symbol.SymbolVariable;

public class TypeAnalyser implements ASTVisitor<ASTType>
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
	public ASTType visit(ASTInclude include)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTAssign n)
	{
		ASTType rhs = n.getExpr().accept(this);
		ASTType lhs = n.getId().accept(this);

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
	public ASTType visit(ASTBlock n)
	{
		for (int i = 0; i < n.getStatListSize(); i++) {
			ASTStatement st = n.getStatAt(i);
			st.accept(this);
		}
		return null;
	}

	@Override
	public ASTType visit(ASTIfThenElse n)
	{
		ASTType texp = n.getExpr().accept(this);
		if (!(texp instanceof ASTBooleanType)) {
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
	public ASTType visit(ASTSkip skip)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTWhile n)
	{
		ASTType texp = n.getExpr().accept(this);
		if (!(texp instanceof ASTBooleanType)) {
			Token sym = n.getExpr().getToken();
			addError(sym.getRow(), sym.getCol(), "Expression must be of type boolean");
		} else {
			n.getExpr().setType(texp);
		}

		n.getBody().accept(this);

		return null;
	}
	
	@Override
	public ASTType visit(ASTIntLiteral n)
	{
		ASTType t = new ASTIntType(n.getToken());
		n.setType(t);
		return t;
	}

	@Override
	public ASTType visit(ASTPlus n)
	{
		ASTType tlhs = n.getLhs().accept(this);
		ASTType trhs = n.getRhs().accept(this);

		if (tlhs == null || !(tlhs instanceof ASTIntType || tlhs instanceof ASTStringType) || trhs == null
				|| !(trhs instanceof ASTIntType || trhs instanceof ASTStringType)) {
			Token sym = n.getToken();
			addError(sym.getRow(), sym.getCol(), "Incompatible Types used with + operator");
			return new ASTIntType(n.getToken());
		}

		if (tlhs instanceof ASTIntType) {
			n.setType(trhs);
			return trhs;
		}

		if (tlhs instanceof ASTStringType) {
			n.setType(tlhs);
			return tlhs;
		}

		ASTType t = new ASTIntType(n.getToken());
		n.setType(t);
		return t;
	}

	@Override
	public ASTType visit(ASTMinus n)
	{
		ASTType lhs = n.getLhs().accept(this);
		ASTType rhs = n.getRhs().accept(this);

		if (lhs == null || rhs == null) { 
			Token sym = n.getToken();
			addError(sym.getRow(), sym.getCol(), "Improper Type used with - operator");
			return new ASTIntType(n.getToken());
		}

		if (!(lhs instanceof ASTIntType) || !(rhs instanceof ASTIntType)) {
			Token sym = n.getLhs().getToken();
			addError(sym.getRow(), sym.getCol(), "Operator - cannot be applied to " + lhs + ", " + rhs);
		}

		ASTType t = new ASTIntType(n.getToken());
		n.setType(t);
		return t;
	}

	@Override
	public ASTType visit(ASTTimes times)
	{	
		ASTType lhs = times.getLhs().accept(this);
		ASTType rhs = times.getRhs().accept(this);

		if (lhs == null || rhs == null) { 
			Token sym = times.getToken();
			addError(sym.getRow(), sym.getCol(), "Improper Type used with * operator");
			return new ASTIntType(times.getToken());
		}

		if (!(lhs instanceof ASTIntType) || !(rhs instanceof ASTIntType)) {
			Token sym = times.getLhs().getToken();
			addError(sym.getRow(), sym.getCol(), "Operator * cannot be applied to " + lhs + ", " + rhs);
		}

		ASTType type = new ASTIntType(times.getToken());
		times.setType(type);
		return type;
	}

	@Override
	public ASTType visit(ASTIncrement increment)
	{
		ASTType expr = increment.getExpr().accept(this);

		if (!(expr instanceof ASTIntType)) {
			Token sym = increment.getExpr().getToken();
			addError(sym.getRow(), sym.getCol(), "Operator ++ cannot be applied to " + expr);
		}

		ASTType t = new ASTIntType(increment.getToken());
		increment.setType(t);
		return t;
	}

	@Override
	public ASTType visit(ASTModulus modulus)
	{
		ASTType lhs = modulus.getLhs().accept(this);
		ASTType rhs = modulus.getRhs().accept(this);

		if (lhs == null || rhs == null) {
			Token sym = modulus.getToken();
			addError(sym.getRow(), sym.getCol(), "Improper Type used with % operator");
			return new ASTIntType(modulus.getToken());
		}

		if (!(lhs instanceof ASTIntType) || !(rhs instanceof ASTIntType)) {
			Token sym = modulus.getLhs().getToken();
			addError(sym.getRow(), sym.getCol(), "Operator % cannot be applied to " + lhs + ", " + rhs);
		}

		ASTType t = new ASTIntType(modulus.getToken());
		modulus.setType(t);
		return t;
	}

	@Override
	public ASTType visit(ASTDivision n)
	{
		ASTType lhs = n.getLhs().accept(this);
		ASTType rhs = n.getRhs().accept(this);

		if (lhs == null || rhs == null) { 
			Token sym = n.getToken();
			addError(sym.getRow(), sym.getCol(), "Improper Type used with / operator");
			return new ASTIntType(n.getToken());
		}

		if (!(lhs instanceof ASTIntType) || !(rhs instanceof ASTIntType)) {
			Token sym = n.getLhs().getToken();
			addError(sym.getRow(), sym.getCol(), "Operator / cannot be applied to " + lhs + ", " + rhs);
		}
		ASTType t = new ASTIntType(n.getToken());
		n.setType(t);
		return t;
	}

	@Override
	public ASTType visit(ASTEquals n)
	{
		ASTType t1 = n.getLhs().accept(this);
		ASTType t2 = n.getRhs().accept(this);

		if (t1 == null || t2 == null) {
			Token sym = n.getToken();
			addError(sym.getRow(), sym.getCol(), "Incorrect types used with == oprator");
		} else if ((t1 instanceof ASTIntType && t2 instanceof ASTIntType)
				|| (t1 instanceof ASTBooleanType && t2 instanceof ASTBooleanType)
				|| (t1 instanceof ASTStringType && t2 instanceof ASTStringType)
				|| (t1 instanceof ASTIntArrayType && t2 instanceof ASTIntArrayType)
				|| (t1 instanceof ASTIdentifierType && t2 instanceof ASTIdentifierType)) {
			n.setType(t1);
		} else {
			Token sym = n.getToken();
			addError(sym.getRow(), sym.getCol(), "Oprator == cannot be applied to " + t1 + ", " + t2);
		}
		return new ASTBooleanType(n.getToken());
	}

	@Override
	public ASTType visit(ASTLessThan lessThan)
	{
		ASTType t1 = lessThan.getLhs().accept(this);
		ASTType t2 = lessThan.getRhs().accept(this);

		if (t1 == null || t2 == null) {
			Token sym = lessThan.getToken();
			addError(sym.getRow(), sym.getCol(), "Incorrect types used with < oprator");
		} else if (!(t1 instanceof ASTIntType) || !(t2 instanceof ASTIntType)) {
			Token sym = lessThan.getToken();
			addError(sym.getRow(), sym.getCol(), "Operator < cannot be applied to " + t1 + ", " + t2);
		}

		ASTType t = new ASTBooleanType(lessThan.getToken());
		lessThan.setType(t);
		return t;
	}

	@Override
	public ASTType visit(ASTLessThanOrEqual lessThanOrEqual)
	{
		ASTType t1 = lessThanOrEqual.getLhs().accept(this);
		ASTType t2 = lessThanOrEqual.getRhs().accept(this);

		if (t1 == null || t2 == null) {
			Token sym = lessThanOrEqual.getToken();
			addError(sym.getRow(), sym.getCol(), "Incorrect types used with <= oprator");
		} else if (!(t1 instanceof ASTIntType) || !(t2 instanceof ASTIntType)) {
			Token sym = lessThanOrEqual.getToken();
			addError(sym.getRow(), sym.getCol(), "Operator <= cannot be applied to " + t1 + ", " + t2);
		}
		ASTType t = new ASTBooleanType(lessThanOrEqual.getToken());
		lessThanOrEqual.setType(t);
		return t;
	}

	@Override
	public ASTType visit(ASTGreaterThan greaterThan)
	{
		ASTType t1 = greaterThan.getLhs().accept(this);
		ASTType t2 = greaterThan.getRhs().accept(this);

		if (t1 == null || t2 == null) {
			Token sym = greaterThan.getToken();
			addError(sym.getRow(), sym.getCol(), "Incorrect types used with > oprator");
		} else if (!(t1 instanceof ASTIntType) || !(t2 instanceof ASTIntType)) {
			Token sym = greaterThan.getToken();
			addError(sym.getRow(), sym.getCol(), "Operator > cannot be applied to " + t1 + ", " + t2);
		}
		ASTType t = new ASTBooleanType(greaterThan.getToken());
		greaterThan.setType(t);
		return t;
	}

	@Override
	public ASTType visit(ASTGreaterThanOrEqual greaterThanOrEqual)
	{
		ASTType t1 = greaterThanOrEqual.getLhs().accept(this);
		ASTType t2 = greaterThanOrEqual.getRhs().accept(this);

		if (t1 == null || t2 == null) {
			Token sym = greaterThanOrEqual.getToken();
			addError(sym.getRow(), sym.getCol(), "Incorrect types used with >= oprator");
		} else if (!(t1 instanceof ASTIntType) || !(t2 instanceof ASTIntType)) {
			Token sym = greaterThanOrEqual.getToken();
			addError(sym.getRow(), sym.getCol(), "Operator >= cannot be applied to " + t1 + ", " + t2);
		}
		ASTType t = new ASTBooleanType(greaterThanOrEqual.getToken());
		greaterThanOrEqual.setType(t);
		return t;
	}

	@Override
	public ASTType visit(ASTAnd and)
	{
		ASTType t1 = and.getLhs().accept(this);
		ASTType t2 = and.getRhs().accept(this);

		if (t1 == null || t2 == null) {
			Token sym = and.getToken();
			addError(sym.getRow(), sym.getCol(), "Incorrect types used with && oprator");
		} else if (!(t1 instanceof ASTBooleanType) || !(t2 instanceof ASTBooleanType)) {
			Token sym = and.getLhs().getToken();
			addError(sym.getRow(), sym.getCol(), "Operator && cannot be applied to " + t1 + ", " + t2);
		}
		ASTType t = new ASTBooleanType(and.getToken());
		and.setType(t);
		return t;
	}

	@Override
	public ASTType visit(ASTOr or)
	{
		ASTType t1 = or.getLhs().accept(this);
		ASTType t2 = or.getRhs().accept(this);

		if (t1 == null || t2 == null) {
			Token sym = or.getToken();
			addError(sym.getRow(), sym.getCol(), "Incorrect types used with || oprator");
		} else if (!(t1 instanceof ASTBooleanType) || !(t2 instanceof ASTBooleanType)) {
			Token sym = or.getLhs().getToken();
			addError(sym.getRow(), sym.getCol(), "Operator || cannot be applied to " + t1 + ", " + t2);
		}
		ASTType t = new ASTBooleanType(or.getToken());
		or.setType(t);
		return t;
	}

	@Override
	public ASTType visit(ASTTrue true1)
	{
		ASTType t = new ASTBooleanType(true1.getToken());
		true1.setType(t);
		return t;
	}

	@Override
	public ASTType visit(ASTFalse false1)
	{
		ASTType t = new ASTBooleanType(false1.getToken());
		false1.setType(t);
		return t;
	}

	@Override
	public ASTType visit(ASTIdentifierExpr i)
	{
		Binding b = i.getB();
		if (b != null) {
			ASTType t = ((SymbolVariable) b).getType();
			i.setType(t);
			return t;
		}
		return null;
	}

	@Override
	public ASTType visit(ASTNewArray na)
	{
		ASTType tl = na.getArrayLength().accept(this);
		if (tl == null || !(tl instanceof ASTIntType)) {
			Token sym = na.getArrayLength().getToken();
			addError(sym.getRow(), sym.getCol(), "Array length must be of type int");
		}

		ASTType t = new ASTIntArrayType(na.getToken());
		na.setType(t);
		return t;
	}

	@Override
	public ASTType visit(ASTNewInstance ni)
	{
		Binding b = ni.getClassName().getB();
		if (b != null) {
			SymbolClass klass = (SymbolClass) b;
			ni.setType(klass.type());
			return klass.type();
		}
		return new ASTIdentifierType(ni.getToken(), ni.getClassName().getId());
	}

	@Override
	public ASTType visit(ASTCallFunctionExpr cm)
	{
		if (cm.getInstanceName() == null) {
			ASTIdentifierExpr callFunc = cm.getMethodId();

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
	public ASTType visit(ASTCallFunctionStat cm)
	{
		for (int i = 0; i < cm.getArgExprListSize(); i++) {
			cm.getArgExprAt(i).accept(this);
		}

		return null;
	}

	private void checkCallArguments(ASTCallFunctionExpr cm, SymbolFunction m)
	{
		List<ASTType> argTypes = new ArrayList<>();
		for (int i = 0; i < cm.getArgExprListSize(); i++) {
			ASTType t2 = cm.getArgExprAt(i).accept(this);
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
			ASTType t1 = var.getType();
			ASTType t2 = argTypes.get(i);

			if (!symbolProgram.compareTypes(t1, t2)) {
				Token sym = cm.getArgExprAt(i).getToken();
				addError(sym.getRow(), sym.getCol(), "The method " + m.toString()
						+ " is not applicable for the arguments (" + getArguments(argTypes) + ")");
				return;
			}

		}
	}

	private String getArguments(List<ASTType> argList)
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
	public ASTType visit(ASTFunctionType functionType)
	{
		return functionType;
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
	public ASTType visit(ASTIdentifierType referenceType)
	{
		return referenceType;
	}

	@Override
	public ASTType visit(ASTVariable vd)
	{
		
		return vd.getType();
	}

	@Override
	public ASTType visit(ASTVariableInit vd)
	{
		ASTType rhs = vd.getExpr().accept(this);
		ASTType lhs = vd.getId().accept(this);

		if (vd.getExpr() instanceof ASTCallFunctionExpr) {
			ASTCallFunctionExpr callFunc = (ASTCallFunctionExpr) vd.getExpr();
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
	public ASTType visit(ASTArgument ad)
	{
		return ad.getType();
	}

	@Override
	public ASTType visit(ASTFunction functionDecl)
	{
		String functionName = functionDecl.getId().getId();

		if (hsymbolFunction.contains(functionName)) {
			return functionDecl.getReturnType();
		}

		if (!(functionDecl.getReturnType() instanceof ASTVoidType)) {
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
	public ASTType visit(ASTFunctionReturn functionReturn)
	{
		String functionName = functionReturn.getId().getId();

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

		ASTType t1 = functionReturn.getReturnType();
		ASTType t2 = functionReturn.getReturnExpr().accept(this);

		if (!symbolProgram.compareTypes(t1, t2)) {
			Token tok = functionReturn.getReturnExpr().getToken();
			addError(tok.getRow(), tok.getCol(), "Function " + functionName + " must return a result of Type " + t1);
		}

		functionReturn.getReturnExpr().setType(t2);
		symbolFunction = null;
		return null;
	}

	@Override
	public ASTType visit(ASTProgram program)
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
	public ASTType visit(ASTIdentifier identifier)
	{
		Binding b = identifier.getB();
		if (b != null) {
			return ((SymbolVariable) b).getType();
		}
		return null;
	}

	@Override
	public ASTType visit(ASTReturnStatement returnStatement)
	{
		returnStatement.getReturnExpr().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTArrayIndexExpr arrayIndexExpr)
	{
		// Check array type
		ASTType tid = arrayIndexExpr.getArray().accept(this);
		if (tid == null || !(tid instanceof ASTIntArrayType)) {
			Token sym = arrayIndexExpr.getArray().getToken();
			addError(sym.getRow(), sym.getCol(), "Array expression must evaluate to be of Type int[]");
		} else {
			arrayIndexExpr.getArray().setType(tid);
		}

		// Check index type
		ASTType tin = arrayIndexExpr.getIndex().accept(this);
		if (tin == null || !(tin instanceof ASTIntType)) {
			Token sym = arrayIndexExpr.getIndex().getToken();
			addError(sym.getRow(), sym.getCol(), "Index expression must evaluate to be of Type int");
		} else {
			arrayIndexExpr.getIndex().setType(tin);
		}

		ASTType t = new ASTIntType(arrayIndexExpr.getToken());
		arrayIndexExpr.setType(t);
		return t;
	}

	@Override
	public ASTType visit(ASTArrayAssign arrayAssign)
	{
		// Check identifier type
		ASTType tid = arrayAssign.getIdentifier().accept(this);
		if (tid == null || !(tid instanceof ASTIntArrayType)) {
			Token sym = arrayAssign.getIdentifier().getToken();
			addError(sym.getRow(), sym.getCol(), "Identifier must be of Type int[]");
		}

		// Check expression type
		ASTType texp1 = arrayAssign.getE1().accept(this);
		if (texp1 == null || !(texp1 instanceof ASTIntType)) {
			Token sym = arrayAssign.getE1().getToken();
			addError(sym.getRow(), sym.getCol(), "Expression must be of Type int");
		} else {
			arrayAssign.getE1().setType(texp1);
		}

		// Check assigned expression type
		ASTType texp2 = arrayAssign.getE2().accept(this);
		if (texp2 == null || !(texp2 instanceof ASTIntType)) {
			Token sym = arrayAssign.getE2().getToken();
			addError(sym.getRow(), sym.getCol(), "Expression must be of Type int");
		} else {
			arrayAssign.getE2().setType(texp2);
		}

		return null;
	}

	@Override
	public ASTType visit(ASTStringLiteral stringLiteral)
	{
		ASTType t = new ASTStringType(stringLiteral.getToken());
		stringLiteral.setType(t);
		return t;
	}

	@Override
	public ASTType visit(ASTClass classDecl)
	{
		String id = classDecl.getId().getId();
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
	public ASTType visit(ASTEnumeration enumDecl)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTInterface interDecl)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTForLoop forLoop)
	{
		return null;
	}
}