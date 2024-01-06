package knight.visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import knight.ast.*;
import knight.ast.Class;
import knight.lexer.*;
import knight.semantics.*;
import knight.symbol.SymbolClass;
import knight.symbol.SymbolFunction;
import knight.symbol.SymbolProgram;
import knight.symbol.SymbolVariable;

public class NameAnalyserTreeVisitor implements Visitor<Type>
{
	private SymbolProgram symbolProgram;
	private SymbolClass symbolClass;
	private SymbolFunction symbolFunction;

	private Set<String> hsymbolClass = new HashSet<>();
	private Set<String> hsymbolFunction = new HashSet<>();

	public NameAnalyserTreeVisitor(SymbolProgram symbolProgram)
	{
		this.symbolProgram = symbolProgram;
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
		SymbolClass klass = symbolProgram.getClass(id);
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
		SymbolVariable var = symbolProgram.getVariable(id, symbolClass, symbolFunction);

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
		SymbolClass klass = symbolProgram.getClass(id);
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
		SymbolVariable var = symbolProgram.getVariable(id, symbolClass, symbolFunction);
		if (var == null) {
			Token sym = identifierExpr.getToken();
			addError(sym.getRow(), sym.getCol(), "variable " + id + " is not declared");
		}
		
		identifierExpr.setB(var);
		return null;
	}

	public void checkVariable(Variable varDecl)
	{
		String id = varDecl.getId().getVarID();
		
		varDecl.getType().accept(this);
		varDecl.getId().accept(this);
	}

	@Override
	public Type visit(Variable varDeclNoInit)
	{	
		checkVariable(varDeclNoInit);
		return null;
	}
	
	@Override
	public Type visit(VariableInit varDeclInit)
	{
		checkVariable(varDeclInit);
		varDeclInit.getExpr().accept(this);
		return null;
	}

	@Override
	public Type visit(Argument ad)
	{
		ad.getId().accept(this);
		return null;
	}

	public void checkFunction(Function functionDecl)
	{
		String functionName = functionDecl.getId().getVarID();

		if (hsymbolFunction.contains(functionName)) {
			return;
		} else {
			hsymbolFunction.add(functionName);
		}

		functionDecl.getReturnType().accept(this);

		if (symbolClass == null) {
			symbolFunction = symbolProgram.getFunction(functionName);
		} else {
			symbolFunction = symbolClass.getFunction(functionName);
		}

		functionDecl.getId().setB(symbolFunction);

		for (int i = 0; i < functionDecl.getArgumentListSize(); i++) {
			functionDecl.getArgumentDeclAt(i).accept(this);
		}

		for (int i = 0; i < functionDecl.getVariableListSize(); i++) {
			functionDecl.getVariableDeclAt(i).accept(this);
		}

		for (int i = 0; i < functionDecl.getStatementListSize(); i++) {
			functionDecl.getStatementDeclAt(i).accept(this);
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
	public Type visit(Class cd)
	{
		String id = cd.getId().getVarID();
		if (hsymbolClass.contains(id)) { 
			return null;
		} else {
			hsymbolClass.add(id);
		}

		symbolClass = symbolProgram.getClass(id);
		cd.getId().setB(symbolClass);

		for (int i = 0; i < cd.getVariableListSize(); i++) {
			cd.getVariableDeclAt(i).accept(this);;
		}

		for (int i = 0; i < cd.getFunctionListSize(); i++) {
			cd.getFunctionDeclAt(i).accept(this);;
		}

		hsymbolFunction.clear();

		symbolClass = null;
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