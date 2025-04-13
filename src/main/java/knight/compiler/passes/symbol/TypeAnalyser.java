package knight.compiler.passes.symbol;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import knight.compiler.ast.ASTAnd;
import knight.compiler.ast.ASTArgument;
import knight.compiler.ast.ASTArrayAssign;
import knight.compiler.ast.ASTArrayIndexExpr;
import knight.compiler.ast.ASTAssign;
import knight.compiler.ast.ASTBody;
import knight.compiler.ast.ASTBooleanType;
import knight.compiler.ast.ASTCallFunctionExpr;
import knight.compiler.ast.ASTCallFunctionStat;
import knight.compiler.ast.ASTClass;
import knight.compiler.ast.ASTConditionalBranch;
import knight.compiler.ast.ASTDivision;
import knight.compiler.ast.ASTEquals;
import knight.compiler.ast.ASTExpression;
import knight.compiler.ast.ASTFalse;
import knight.compiler.ast.ASTFunction;
import knight.compiler.ast.ASTFunctionReturn;
import knight.compiler.ast.ASTFunctionType;
import knight.compiler.ast.ASTGreaterThan;
import knight.compiler.ast.ASTGreaterThanOrEqual;
import knight.compiler.ast.ASTIdentifier;
import knight.compiler.ast.ASTIdentifierExpr;
import knight.compiler.ast.ASTIdentifierType;
import knight.compiler.ast.ASTIfChain;
import knight.compiler.ast.ASTIntArrayType;
import knight.compiler.ast.ASTIntLiteral;
import knight.compiler.ast.ASTIntType;
import knight.compiler.ast.ASTLessThan;
import knight.compiler.ast.ASTLessThanOrEqual;
import knight.compiler.ast.ASTMinus;
import knight.compiler.ast.ASTModulus;
import knight.compiler.ast.ASTNewArray;
import knight.compiler.ast.ASTNewInstance;
import knight.compiler.ast.ASTNotEquals;
import knight.compiler.ast.ASTOr;
import knight.compiler.ast.ASTPlus;
import knight.compiler.ast.ASTProgram;
import knight.compiler.ast.ASTProperty;
import knight.compiler.ast.ASTReturnStatement;
import knight.compiler.ast.ASTStatement;
import knight.compiler.ast.ASTStringLiteral;
import knight.compiler.ast.ASTStringType;
import knight.compiler.ast.ASTTimes;
import knight.compiler.ast.ASTTrue;
import knight.compiler.ast.ASTType;
import knight.compiler.ast.ASTVariable;
import knight.compiler.ast.ASTVariableInit;
import knight.compiler.ast.ASTVisitor;
import knight.compiler.ast.ASTVoidType;
import knight.compiler.ast.ASTWhile;
import knight.compiler.passes.symbol.diagnostics.SemanticErrors;
import knight.compiler.passes.symbol.model.Binding;
import knight.compiler.passes.symbol.model.SymbolClass;
import knight.compiler.passes.symbol.model.SymbolFunction;
import knight.compiler.passes.symbol.model.SymbolProgram;
import knight.compiler.passes.symbol.model.SymbolVariable;
import knight.compiler.passes.symbol.utils.BuiltInFunctions;
import knight.compiler.passes.symbol.utils.BuiltInFunctions.FunctionSignature;

/*
 * File: TypeAnalyser.java
 * @author: Mart van der Zalm
 * Date: 2025-04-10
 */
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
	public ASTType visit(ASTAssign astAssign)
	{
		ASTType rightSide = astAssign.getExpr().accept(this);
		ASTType leftSide = astAssign.getIdentifier().accept(this);

		if (!symbolProgram.compareTypes(leftSide, rightSide)) {
			if (leftSide == null || rightSide == null) {
				SemanticErrors.addError(astAssign.getToken(), "Incompatible types used with assignment Operator = ");
			} else {
				SemanticErrors.addError(astAssign.getToken(),
						"Operator = cannot be applied to " + leftSide + ", " + rightSide);
			}

		} else {
			astAssign.getExpr().setType(rightSide);
		}
		return null;
	}

	@Override
	public ASTType visit(ASTBody astBody)
	{
		for (ASTVariable astVariable : astBody.getVariableList()) {
			astVariable.accept(this);
		}

		for (ASTStatement astStatement : astBody.getStatementList()) {
			astStatement.accept(this);
		}
		return null;
	}

	@Override
	public ASTType visit(ASTWhile astWhile)
	{
		ASTType astType = astWhile.getCondition().accept(this);
		if (!(astType instanceof ASTBooleanType)) {
			SemanticErrors.addError(astWhile.getCondition().getToken(), "Expression must be of type boolean");
		} else {
			astWhile.getCondition().setType(astType);
		}
		astWhile.getBody().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTIntLiteral astIntLiteral)
	{
		ASTType astType = new ASTIntType(astIntLiteral.getToken());
		astIntLiteral.setType(astType);
		return astType;
	}

	@Override
	public ASTType visit(ASTPlus astPlus)
	{
		ASTType leftSide = astPlus.getLeftSide().accept(this);
		ASTType rightSide = astPlus.getRightSide().accept(this);

		if (leftSide == null || !(leftSide instanceof ASTIntType || leftSide instanceof ASTStringType)
				|| rightSide == null || !(rightSide instanceof ASTIntType || rightSide instanceof ASTStringType)) {
			SemanticErrors.addError(astPlus.getToken(), "Incompatible Types used with + operator");
			return new ASTIntType(astPlus.getToken());
		}

		if (leftSide instanceof ASTIntType) {
			astPlus.setType(rightSide);
			return rightSide;
		}

		if (leftSide instanceof ASTStringType) {
			astPlus.setType(leftSide);
			return leftSide;
		}

		ASTType astType = new ASTIntType(astPlus.getToken());
		astPlus.setType(astType);
		return astType;
	}

	@Override
	public ASTType visit(ASTMinus astMinus)
	{
		ASTType leftSide = astMinus.getLeftSide().accept(this);
		ASTType rightSide = astMinus.getRightSide().accept(this);

		if (leftSide == null || rightSide == null) {
			SemanticErrors.addError(astMinus.getToken(), "Improper Type used with - operator");
			return new ASTIntType(astMinus.getToken());
		}

		if (!(leftSide instanceof ASTIntType) || !(rightSide instanceof ASTIntType)) {
			SemanticErrors.addError(astMinus.getLeftSide().getToken(),
					"Operator - cannot be applied to " + leftSide + ", " + rightSide);
		}

		ASTType astType = new ASTIntType(astMinus.getToken());
		astMinus.setType(astType);
		return astType;
	}

	@Override
	public ASTType visit(ASTTimes astTimes)
	{
		ASTType leftSide = astTimes.getLeftSide().accept(this);
		ASTType rightSide = astTimes.getRightSide().accept(this);

		if (leftSide == null || rightSide == null) {
			SemanticErrors.addError(astTimes.getToken(), "Improper Type used with * operator");
			return new ASTIntType(astTimes.getToken());
		}

		if (!(leftSide instanceof ASTIntType) || !(rightSide instanceof ASTIntType)) {
			SemanticErrors.addError(astTimes.getLeftSide().getToken(),
					"Operator * cannot be applied to " + leftSide + ", " + rightSide);
		}

		ASTType astType = new ASTIntType(astTimes.getToken());
		astTimes.setType(astType);
		return astType;
	}

	@Override
	public ASTType visit(ASTModulus astModulus)
	{
		ASTType leftSide = astModulus.getLeftSide().accept(this);
		ASTType rightSide = astModulus.getRightSide().accept(this);

		if (leftSide == null || rightSide == null) {
			SemanticErrors.addError(astModulus.getToken(), "Improper Type used with % operator");
			return new ASTIntType(astModulus.getToken());
		}

		if (!(leftSide instanceof ASTIntType) || !(rightSide instanceof ASTIntType)) {
			SemanticErrors.addError(astModulus.getLeftSide().getToken(),
					"Operator % cannot be applied to " + leftSide + ", " + rightSide);
		}

		ASTType astType = new ASTIntType(astModulus.getToken());
		astModulus.setType(astType);
		return astType;
	}

	@Override
	public ASTType visit(ASTDivision astDivision)
	{
		ASTType leftSide = astDivision.getLeftSide().accept(this);
		ASTType rightSide = astDivision.getRightSide().accept(this);

		if (leftSide == null || rightSide == null) {
			SemanticErrors.addError(astDivision.getToken(), "Improper Type used with / operator");
			return new ASTIntType(astDivision.getToken());
		}

		if (!(leftSide instanceof ASTIntType) || !(rightSide instanceof ASTIntType)) {
			SemanticErrors.addError(astDivision.getLeftSide().getToken(),
					"Operator / cannot be applied to " + leftSide + ", " + rightSide);
		}
		ASTType astType = new ASTIntType(astDivision.getToken());
		astDivision.setType(astType);
		return astType;
	}

	@Override
	public ASTType visit(ASTEquals astEquals)
	{
		ASTType typeLeftSide = astEquals.getLeftSide().accept(this);
		ASTType typeRightSide = astEquals.getRightSide().accept(this);

		if (typeLeftSide == null || typeRightSide == null) {
			SemanticErrors.addError(astEquals.getToken(), "Incorrect types used with == oprator");
		} else if ((typeLeftSide instanceof ASTIntType && typeRightSide instanceof ASTIntType)
				|| (typeLeftSide instanceof ASTBooleanType && typeRightSide instanceof ASTBooleanType)
				|| (typeLeftSide instanceof ASTStringType && typeRightSide instanceof ASTStringType)
				|| (typeLeftSide instanceof ASTIntArrayType && typeRightSide instanceof ASTIntArrayType)
				|| (typeLeftSide instanceof ASTIdentifierType && typeRightSide instanceof ASTIdentifierType)) {
			astEquals.setType(typeLeftSide);
		} else {
			SemanticErrors.addError(astEquals.getToken(),
					"Oprator == cannot be applied to " + typeLeftSide + ", " + typeRightSide);
		}
		return new ASTBooleanType(astEquals.getToken());
	}

	@Override
	public ASTType visit(ASTNotEquals astNotEquals)
	{
		ASTType typeLeftSide = astNotEquals.getLeftSide().accept(this);
		ASTType typeRightSide = astNotEquals.getRightSide().accept(this);

		if (typeLeftSide == null || typeRightSide == null) {
			SemanticErrors.addError(astNotEquals.getToken(), "Incorrect types used with == oprator");
		} else if ((typeLeftSide instanceof ASTIntType && typeRightSide instanceof ASTIntType)
				|| (typeLeftSide instanceof ASTBooleanType && typeRightSide instanceof ASTBooleanType)
				|| (typeLeftSide instanceof ASTStringType && typeRightSide instanceof ASTStringType)
				|| (typeLeftSide instanceof ASTIntArrayType && typeRightSide instanceof ASTIntArrayType)
				|| (typeLeftSide instanceof ASTIdentifierType && typeRightSide instanceof ASTIdentifierType)) {
			astNotEquals.setType(typeLeftSide);
		} else {
			SemanticErrors.addError(astNotEquals.getToken(),
					"Oprator == cannot be applied to " + typeLeftSide + ", " + typeRightSide);
		}
		return new ASTBooleanType(astNotEquals.getToken());
	}

	@Override
	public ASTType visit(ASTLessThan astLessThan)
	{
		ASTType typeLeftSide = astLessThan.getLeftSide().accept(this);
		ASTType typeRightSide = astLessThan.getRightSide().accept(this);

		if (typeLeftSide == null || typeRightSide == null) {
			SemanticErrors.addError(astLessThan.getToken(), "Incorrect types used with < oprator");
		} else if (!(typeLeftSide instanceof ASTIntType) || !(typeRightSide instanceof ASTIntType)) {
			SemanticErrors.addError(astLessThan.getToken(),
					"Operator < cannot be applied to " + typeLeftSide + ", " + typeRightSide);
		}

		ASTType astType = new ASTBooleanType(astLessThan.getToken());
		astLessThan.setType(astType);
		return astType;
	}

	@Override
	public ASTType visit(ASTLessThanOrEqual astLessThanOrEqual)
	{
		ASTType typeLeftSide = astLessThanOrEqual.getLeftSide().accept(this);
		ASTType typeRightSide = astLessThanOrEqual.getRightSide().accept(this);

		if (typeLeftSide == null || typeRightSide == null) {
			SemanticErrors.addError(astLessThanOrEqual.getToken(), "Incorrect types used with <= oprator");
		} else if (!(typeLeftSide instanceof ASTIntType) || !(typeRightSide instanceof ASTIntType)) {
			SemanticErrors.addError(astLessThanOrEqual.getToken(),
					"Operator <= cannot be applied to " + typeLeftSide + ", " + typeRightSide);
		}
		ASTType astType = new ASTBooleanType(astLessThanOrEqual.getToken());
		astLessThanOrEqual.setType(astType);
		return astType;
	}

	@Override
	public ASTType visit(ASTGreaterThan astGreaterThan)
	{
		ASTType typeLeftSide = astGreaterThan.getLeftSide().accept(this);
		ASTType typeRightSide = astGreaterThan.getRightSide().accept(this);

		if (typeLeftSide == null || typeRightSide == null) {
			SemanticErrors.addError(astGreaterThan.getToken(), "Incorrect types used with > oprator");
		} else if (!(typeLeftSide instanceof ASTIntType) || !(typeRightSide instanceof ASTIntType)) {
			SemanticErrors.addError(astGreaterThan.getToken(),
					"Operator > cannot be applied to " + typeLeftSide + ", " + typeRightSide);
		}
		ASTType astType = new ASTBooleanType(astGreaterThan.getToken());
		astGreaterThan.setType(astType);
		return astType;
	}

	@Override
	public ASTType visit(ASTGreaterThanOrEqual astGreaterThanOrEqual)
	{
		ASTType typeLeftSide = astGreaterThanOrEqual.getLeftSide().accept(this);
		ASTType typeRightSide = astGreaterThanOrEqual.getRightSide().accept(this);

		if (typeLeftSide == null || typeRightSide == null) {
			SemanticErrors.addError(astGreaterThanOrEqual.getToken(), "Incorrect types used with >= oprator");
		} else if (!(typeLeftSide instanceof ASTIntType) || !(typeRightSide instanceof ASTIntType)) {
			SemanticErrors.addError(astGreaterThanOrEqual.getToken(),
					"Operator >= cannot be applied to " + typeLeftSide + ", " + typeRightSide);
		}
		ASTType astType = new ASTBooleanType(astGreaterThanOrEqual.getToken());
		astGreaterThanOrEqual.setType(astType);
		return astType;
	}

	@Override
	public ASTType visit(ASTAnd astAnd)
	{
		ASTType typeLeftSide = astAnd.getLeftSide().accept(this);
		ASTType typeRightSide = astAnd.getRightSide().accept(this);

		if (typeLeftSide == null || typeRightSide == null) {
			SemanticErrors.addError(astAnd.getToken(), "Incorrect types used with && oprator");
		} else if (!(typeLeftSide instanceof ASTBooleanType) || !(typeRightSide instanceof ASTBooleanType)) {
			SemanticErrors.addError(astAnd.getLeftSide().getToken(),
					"Operator && cannot be applied to " + typeLeftSide + ", " + typeRightSide);
		}
		ASTType astType = new ASTBooleanType(astAnd.getToken());
		astAnd.setType(astType);
		return astType;
	}

	@Override
	public ASTType visit(ASTOr astOr)
	{
		ASTType typeLeftSide = astOr.getLeftSide().accept(this);
		ASTType typeRightSide = astOr.getRightSide().accept(this);

		if (typeLeftSide == null || typeRightSide == null) {
			SemanticErrors.addError(astOr.getToken(), "Incorrect types used with || oprator");
		} else if (!(typeLeftSide instanceof ASTBooleanType) || !(typeRightSide instanceof ASTBooleanType)) {
			SemanticErrors.addError(astOr.getLeftSide().getToken(),
					"Operator || cannot be applied to " + typeLeftSide + ", " + typeRightSide);
		}
		ASTType astType = new ASTBooleanType(astOr.getToken());
		astOr.setType(astType);
		return astType;
	}

	@Override
	public ASTType visit(ASTTrue astTrue)
	{
		ASTType astType = new ASTBooleanType(astTrue.getToken());
		astTrue.setType(astType);
		return astType;
	}

	@Override
	public ASTType visit(ASTFalse astFalse)
	{
		ASTType astType = new ASTBooleanType(astFalse.getToken());
		astFalse.setType(astType);
		return astType;
	}

	@Override
	public ASTType visit(ASTIdentifierExpr astIdentifierExpr)
	{
		Binding binding = astIdentifierExpr.getB();
		if (binding != null) {
			ASTType astType = ((SymbolVariable) binding).getType();
			astIdentifierExpr.setType(astType);
			return astType;
		}
		return null;
	}

	@Override
	public ASTType visit(ASTNewArray astNewArray)
	{
		ASTType typeArrayLength = astNewArray.getArrayLength().accept(this);
		if (typeArrayLength == null || !(typeArrayLength instanceof ASTIntType)) {
			SemanticErrors.addError(astNewArray.getArrayLength().getToken(), "Array length must be of type int");
		}

		ASTType astType = new ASTIntArrayType(astNewArray.getToken());
		astNewArray.setType(astType);
		return astType;
	}

	@Override
	public ASTType visit(ASTNewInstance astNewInstance)
	{
		Binding binding = astNewInstance.getClassName().getB();
		if (binding != null) {
			SymbolClass symbolClass = (SymbolClass) binding;
			astNewInstance.setType(symbolClass.type());
			return symbolClass.type();
		}
		return new ASTIdentifierType(astNewInstance.getToken(), astNewInstance.getClassName().getId());
	}

	@Override
	public ASTType visit(ASTCallFunctionExpr astCallFunctionExpr)
	{
		ASTIdentifierExpr functionName = astCallFunctionExpr.getFunctionName();

		if (BuiltInFunctions.isBuiltIn(functionName.toString())) {
			FunctionSignature signature = BuiltInFunctions.getSignature(functionName.toString());
			checkBuiltInCall(astCallFunctionExpr, signature);
			astCallFunctionExpr.setType(signature.returnType);
			return signature.returnType;
		}

		SymbolFunction symbolFunction = null;
		if (symbolClass == null) {
			symbolFunction = symbolProgram.getFunction(functionName.toString());
		} else {
			symbolFunction = symbolProgram.getFunction(functionName.toString(), symbolClass.getId());
		}

		if (symbolFunction == null) {
			SemanticErrors.addError(functionName.getToken(), "Function " + functionName + " not declared");
			return null;
		}

		astCallFunctionExpr.getFunctionName().setB(symbolFunction);
		checkCallArguments(astCallFunctionExpr, symbolFunction);
		astCallFunctionExpr.setType(symbolFunction.getType());
		return symbolFunction.getType();
	}

	@Override
	public ASTType visit(ASTCallFunctionStat astCallFunctionStat)
	{
		for (ASTExpression astExpression : astCallFunctionStat.getArgumentList()) {
			astExpression.accept(this);
		}
		return null;
	}

	private void checkBuiltInCall(ASTCallFunctionExpr call, FunctionSignature signature)
	{
		List<ASTType> argumentTypes = new ArrayList<>();

		for (ASTExpression astExpression : call.getArgumentList()) {
			ASTType astType = astExpression.accept(this);
			argumentTypes.add(astType);
		}

		// Check argument count
		if (call.getArgumentListSize() != signature.parameterTypes.size()) {
			SemanticErrors.addError(call.getToken(), "Built-in function '" + call.getFunctionName() + "' expects "
					+ signature.parameterTypes.size() + " arguments but got " + call.getArgumentListSize());
			return;
		}

		// Check argument types
		for (int i = 0; i < call.getArgumentListSize(); i++) {
			ASTType expected = signature.parameterTypes.get(i);
			ASTType actual = argumentTypes.get(i);

			if (!symbolProgram.compareTypes(expected, actual)) {
				SemanticErrors.addError(call.getArgumentAt(i).getToken(), "Argument " + (i + 1) + " of '"
						+ call.getFunctionName() + "' must be " + expected + " but got " + actual);
			}
		}
	}

	private void checkCallArguments(ASTCallFunctionExpr astCallFunctionExpr, SymbolFunction symbolFunction)
	{
		List<ASTType> argumentTypes = new ArrayList<>();

		for (ASTExpression astExpression : astCallFunctionExpr.getArgumentList()) {
			ASTType astType = astExpression.accept(this);
			argumentTypes.add(astType);
		}

		if (astCallFunctionExpr.getArgumentListSize() != symbolFunction.getParamsSize()) {
			SemanticErrors.addError(astCallFunctionExpr.getToken(), "The function " + symbolFunction.toString()
					+ " is not applicable for the arguments (" + getArguments(argumentTypes) + ")");
			return;
		}

		for (int i = 0; i < argumentTypes.size(); i++) {
			SymbolVariable symbolVariable = symbolFunction.getParamAt(i);
			ASTType type1 = symbolVariable.getType();
			ASTType type2 = argumentTypes.get(i);

			if (!symbolProgram.compareTypes(type1, type2)) {
				SemanticErrors.addError(astCallFunctionExpr.getArgumentAt(i).getToken(),
						"The function " + symbolFunction.toString() + " is not applicable for the arguments ("
								+ getArguments(argumentTypes) + ")");
				return;
			}

		}
	}

	private String getArguments(List<ASTType> argumentList)
	{
		if (argumentList == null || argumentList.size() == 0) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < argumentList.size(); i++) {
			sb.append(argumentList.get(i));
			if (i < argumentList.size() - 1) {
				sb.append(", ");
			}
		}
		return sb.toString();
	}

	@Override
	public ASTType visit(ASTFunctionType astFunctionType)
	{
		return astFunctionType;
	}

	@Override
	public ASTType visit(ASTIntType astIntType)
	{
		return astIntType;
	}

	@Override
	public ASTType visit(ASTStringType astStringType)
	{
		return astStringType;
	}

	@Override
	public ASTType visit(ASTVoidType astVoidType)
	{
		return astVoidType;
	}

	@Override
	public ASTType visit(ASTBooleanType astBooleanType)
	{
		return astBooleanType;
	}

	@Override
	public ASTType visit(ASTIntArrayType astIntArrayType)
	{
		return astIntArrayType;
	}

	@Override
	public ASTType visit(ASTIdentifierType astIdentifierType)
	{
		return astIdentifierType;
	}

	@Override
	public ASTType visit(ASTVariable astVariable)
	{
		return astVariable.getType();
	}

	@Override
	public ASTType visit(ASTVariableInit astVariableInit)
	{
		ASTType typeRightSide = astVariableInit.getExpr().accept(this);
		ASTType typeLeftSide = astVariableInit.getId().accept(this);

		if (astVariableInit.getExpr() instanceof ASTCallFunctionExpr) {
			ASTCallFunctionExpr astCallFunctionExpr = (ASTCallFunctionExpr) astVariableInit.getExpr();

			SymbolFunction symbolFunction = null;
			if (symbolClass != null) {
				symbolFunction = symbolProgram.getFunction(astCallFunctionExpr.getFunctionName().toString(),
						symbolClass.getId());
			} else {
				symbolFunction = symbolProgram.getFunction(astCallFunctionExpr.getFunctionName().toString());
			}

			if (symbolFunction != null) {
				typeRightSide = symbolFunction.getType();
			}
		}

		if (!symbolProgram.compareTypes(typeLeftSide, typeRightSide)) {
			if (typeLeftSide == null || typeRightSide == null) {
				SemanticErrors.addError(astVariableInit.getToken(),
						"Incompatible types used with assignment Operator = ");
			} else {
				SemanticErrors.addError(astVariableInit.getToken(),
						"Operator = cannot be applied to " + typeLeftSide + ", " + typeRightSide);
			}

		} else {
			astVariableInit.getExpr().setType(typeRightSide);
		}

		return null;
	}

	@Override
	public ASTType visit(ASTFunction astFunction)
	{
		String functionName = astFunction.getFunctionName().getId();

		if (hsymbolFunction.contains(functionName)) {
			return astFunction.getReturnType();
		}

		if (!(astFunction.getReturnType() instanceof ASTVoidType)) {
			SemanticErrors.addError(astFunction.getReturnType().getToken(),
					"Function " + functionName + " must return a result of Type " + astFunction.getReturnType());
		}

		hsymbolFunction.add(functionName);
		symbolFunction = (SymbolFunction) astFunction.getFunctionName().getB();

		for (ASTArgument astArgument : astFunction.getArgumentList()) {
			astArgument.accept(this);;
		}

		astFunction.getBody().accept(this);
		symbolFunction = null;
		return null;
	}

	@Override
	public ASTType visit(ASTFunctionReturn astFunctionReturn)
	{
		String functionName = astFunctionReturn.getFunctionName().getId();

		if (hsymbolFunction.contains(functionName)) {
			return astFunctionReturn.getReturnType();
		}

		hsymbolFunction.add(functionName);
		symbolFunction = (SymbolFunction) astFunctionReturn.getFunctionName().getB();

		for (ASTArgument astArgument : astFunctionReturn.getArgumentList()) {
			astArgument.accept(this);;
		}

		astFunctionReturn.getBody().accept(this);

		ASTType type1 = astFunctionReturn.getReturnType();
		ASTType type2 = astFunctionReturn.getReturnExpr().accept(this);

		if (!symbolProgram.compareTypes(type1, type2)) {
			SemanticErrors.addError(astFunctionReturn.getReturnExpr().getToken(),
					"Function " + functionName + " must return a result of Type " + type1);
		}

		astFunctionReturn.getReturnExpr().setType(type2);
		symbolFunction = null;
		return null;
	}

	@Override
	public ASTType visit(ASTProgram astProgram)
	{
		for (ASTClass astClass : astProgram.getClassList()) {
			astClass.accept(this);
		}

		for (ASTFunction astFunction : astProgram.getFunctionList()) {
			astFunction.accept(this);
		}

		for (ASTVariable astVariable : astProgram.getVariableList()) {
			astVariable.accept(this);
		}

		return null;
	}

	@Override
	public ASTType visit(ASTIdentifier astIdentifier)
	{
		Binding binding = astIdentifier.getB();
		if (binding != null) {
			return ((SymbolVariable) binding).getType();
		}
		return null;
	}

	@Override
	public ASTType visit(ASTReturnStatement astReturnStatement)
	{
		astReturnStatement.getReturnExpr().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTArrayIndexExpr astArrayIndexExpr)
	{
		ASTType typeArray = astArrayIndexExpr.getArray().accept(this);
		if (typeArray == null || !(typeArray instanceof ASTIntArrayType)) {
			SemanticErrors.addError(astArrayIndexExpr.getArray().getToken(),
					"Array expression must evaluate to be of Type int[]");
		} else {
			astArrayIndexExpr.getArray().setType(typeArray);
		}

		ASTType typeArrayIndex = astArrayIndexExpr.getIndex().accept(this);
		if (typeArrayIndex == null || !(typeArrayIndex instanceof ASTIntType)) {
			SemanticErrors.addError(astArrayIndexExpr.getIndex().getToken(),
					"Index expression must evaluate to be of Type int");
		} else {
			astArrayIndexExpr.getIndex().setType(typeArrayIndex);
		}

		ASTType astType = new ASTIntType(astArrayIndexExpr.getToken());
		astArrayIndexExpr.setType(astType);
		return astType;
	}

	@Override
	public ASTType visit(ASTArrayAssign astArrayAssign)
	{
		ASTType typeArrayId = astArrayAssign.getId().accept(this);
		if (typeArrayId == null || !(typeArrayId instanceof ASTIntArrayType)) {
			SemanticErrors.addError(astArrayAssign.getId().getToken(), "Identifier must be of Type int[]");
		}

		ASTType typeArrayExpr1 = astArrayAssign.getExpression1().accept(this);
		if (typeArrayExpr1 == null || !(typeArrayExpr1 instanceof ASTIntType)) {
			SemanticErrors.addError(astArrayAssign.getExpression1().getToken(), "Expression must be of Type int");
		} else {
			astArrayAssign.getExpression1().setType(typeArrayExpr1);
		}

		ASTType typeArrayExpr2 = astArrayAssign.getExpression2().accept(this);
		if (typeArrayExpr2 == null || !(typeArrayExpr2 instanceof ASTIntType)) {
			SemanticErrors.addError(astArrayAssign.getExpression2().getToken(), "Expression must be of Type int");
		} else {
			astArrayAssign.getExpression2().setType(typeArrayExpr2);
		}

		return null;
	}

	@Override
	public ASTType visit(ASTStringLiteral astStringLiteral)
	{
		ASTType astType = new ASTStringType(astStringLiteral.getToken());
		astStringLiteral.setType(astType);
		return astType;
	}

	@Override
	public ASTType visit(ASTClass astClass)
	{
		String className = astClass.getClassName().getId();
		if (hsymbolClass.contains(className)) {
			return null;
		}
		hsymbolClass.add(className);

		Binding binding = astClass.getClassName().getB();
		symbolClass = (SymbolClass) binding;
		hsymbolFunction.clear();

		for (ASTProperty astProperty : astClass.getPropertyList()) {
			astProperty.accept(this);
		}

		for (ASTFunction astFunction : astClass.getFunctionList()) {
			astFunction.accept(this);
		}

		return null;
	}

	@Override
	public ASTType visit(ASTProperty astProperty)
	{
		return astProperty.getType();
	}

	@Override
	public ASTType visit(ASTIfChain astIfChain)
	{
		for (ASTConditionalBranch astConditionalBranch : astIfChain.getBranches()) {
			astConditionalBranch.accept(this);
		}

		if (astIfChain.getElseBody() != null) {
			astIfChain.getElseBody().accept(this);
		}

		return null;
	}

	@Override
	public ASTType visit(ASTConditionalBranch astConditionalBranch)
	{
		ASTType typeCondition = astConditionalBranch.getCondition().accept(this);

		if (!(typeCondition instanceof ASTBooleanType)) {
			SemanticErrors.addError(astConditionalBranch.getCondition().getToken(),
					"Expression must be of type boolean");
		} else {
			astConditionalBranch.getCondition().setType(typeCondition);
		}

		astConditionalBranch.getBody().accept(this);
		return null;
	}

	@Override
	public ASTType visit(ASTArgument astArgument)
	{
		return astArgument.getType();
	}
}
