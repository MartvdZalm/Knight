package knight.compiler.semantics;

import knight.compiler.ast.AST;
import knight.compiler.ast.ASTVisitor;
import knight.compiler.ast.controlflow.ASTConditionalBranch;
import knight.compiler.ast.controlflow.ASTForeach;
import knight.compiler.ast.controlflow.ASTIfChain;
import knight.compiler.ast.controlflow.ASTWhile;
import knight.compiler.ast.expressions.*;
import knight.compiler.ast.program.*;
import knight.compiler.ast.statements.*;
import knight.compiler.ast.types.*;
import knight.compiler.semantics.diagnostics.DiagnosticReporter;
import knight.compiler.semantics.model.*;

import java.util.*;

public class TypeAnalyser implements ASTVisitor<ASTType>
{
	private SymbolProgram symbolProgram;
	private SymbolClass symbolClass;
	private SymbolFunction symbolFunction;
	private final Set<String> processedClasses = new HashSet<>();
	private final Set<String> processedFunctions = new HashSet<>();

	public TypeAnalyser(SymbolProgram symbolProgram)
	{
		this.symbolProgram = symbolProgram;
	}

	public void setSymbolProgram(SymbolProgram symbolProgram)
	{
		this.symbolProgram = symbolProgram;
	}

	public void setSymbolClass(SymbolClass symbolClass)
	{
		this.symbolClass = symbolClass;
	}

	public void setSymbolFunction(SymbolFunction symbolFunction)
	{
		this.symbolFunction = symbolFunction;
	}

	@Override
	public ASTType visit(ASTAssign astAssign)
	{
		ASTType rightSide = astAssign.getExpr().accept(this);
		ASTType leftSide = astAssign.getIdentifier().accept(this);

		if (!symbolProgram.compareTypes(leftSide, rightSide)) {
			if (leftSide == null || rightSide == null) {
				DiagnosticReporter.error(astAssign.getToken(), "Incompatible types used with assignment Operator = ");
			} else {
				DiagnosticReporter.error(astAssign.getToken(),
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
		for (AST node : astBody.getNodesList()) {
			node.accept(this);
		}
		return null;
	}

	@Override
	public ASTType visit(ASTWhile astWhile)
	{
		ASTType astType = astWhile.getCondition().accept(this);
		if (!(astType instanceof ASTBooleanType)) {
			DiagnosticReporter.error(astWhile.getCondition().getToken(), "Expression must be of type boolean");
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
		ASTType leftType = astPlus.getLeftSide().accept(this);
		ASTType rightType = astPlus.getRightSide().accept(this);

		if (leftType == null || rightType == null) {
			DiagnosticReporter.error(astPlus.getToken(), "Operands must have valid types for + operator");
			return new ASTIntType(astPlus.getToken());
		}

		if (leftType instanceof ASTIntType && rightType instanceof ASTIntType) {
			ASTType resultType = new ASTIntType(astPlus.getToken());
			astPlus.setType(resultType);
			return resultType;
		}

		if (leftType instanceof ASTStringType || rightType instanceof ASTStringType) {
			ASTType resultType = new ASTStringType(astPlus.getToken());
			astPlus.setType(resultType);

			if (!(leftType instanceof ASTStringType) || !(rightType instanceof ASTStringType)) {
				DiagnosticReporter.warning(astPlus.getToken(), "Implicit conversion to string in concatenation");
			}
			return resultType;
		}

		DiagnosticReporter.error(astPlus.getToken(), "Operator + cannot be applied to " + leftType + " and " + rightType
				+ ". Supported: int + int or string + any");
		return new ASTIntType(astPlus.getToken());
	}

	@Override
	public ASTType visit(ASTMinus astMinus)
	{
		ASTType leftSide = astMinus.getLeftSide().accept(this);
		ASTType rightSide = astMinus.getRightSide().accept(this);

		if (leftSide == null || rightSide == null) {
			DiagnosticReporter.error(astMinus.getToken(), "Improper Type used with - operator");
			return new ASTIntType(astMinus.getToken());
		}

		if (!(leftSide instanceof ASTIntType) || !(rightSide instanceof ASTIntType)) {
			DiagnosticReporter.error(astMinus.getLeftSide().getToken(),
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
			DiagnosticReporter.error(astTimes.getToken(), "Improper Type used with * operator");
			return new ASTIntType(astTimes.getToken());
		}

		if (!(leftSide instanceof ASTIntType) || !(rightSide instanceof ASTIntType)) {
			DiagnosticReporter.error(astTimes.getLeftSide().getToken(),
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
			DiagnosticReporter.error(astModulus.getToken(), "Improper Type used with % operator");
			return new ASTIntType(astModulus.getToken());
		}

		if (!(leftSide instanceof ASTIntType) || !(rightSide instanceof ASTIntType)) {
			DiagnosticReporter.error(astModulus.getLeftSide().getToken(),
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
			DiagnosticReporter.error(astDivision.getToken(), "Improper Type used with / operator");
			return new ASTIntType(astDivision.getToken());
		}

		if (!(leftSide instanceof ASTIntType) || !(rightSide instanceof ASTIntType)) {
			DiagnosticReporter.error(astDivision.getLeftSide().getToken(),
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
			DiagnosticReporter.error(astEquals.getToken(), "Incorrect types used with == oprator");
		} else if ((typeLeftSide instanceof ASTIntType && typeRightSide instanceof ASTIntType)
				|| (typeLeftSide instanceof ASTBooleanType && typeRightSide instanceof ASTBooleanType)
				|| (typeLeftSide instanceof ASTStringType && typeRightSide instanceof ASTStringType)
				|| (typeLeftSide instanceof ASTIntArrayType && typeRightSide instanceof ASTIntArrayType)
				|| (typeLeftSide instanceof ASTIdentifierType && typeRightSide instanceof ASTIdentifierType)) {
			astEquals.setType(typeLeftSide);
		} else {
			DiagnosticReporter.error(astEquals.getToken(),
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
			DiagnosticReporter.error(astNotEquals.getToken(), "Incorrect types used with == oprator");
		} else if ((typeLeftSide instanceof ASTIntType && typeRightSide instanceof ASTIntType)
				|| (typeLeftSide instanceof ASTBooleanType && typeRightSide instanceof ASTBooleanType)
				|| (typeLeftSide instanceof ASTStringType && typeRightSide instanceof ASTStringType)
				|| (typeLeftSide instanceof ASTIntArrayType && typeRightSide instanceof ASTIntArrayType)
				|| (typeLeftSide instanceof ASTIdentifierType && typeRightSide instanceof ASTIdentifierType)) {
			astNotEquals.setType(typeLeftSide);
		} else {
			DiagnosticReporter.error(astNotEquals.getToken(),
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
			DiagnosticReporter.error(astLessThan.getToken(), "Incorrect types used with < oprator");
		} else if (!(typeLeftSide instanceof ASTIntType) || !(typeRightSide instanceof ASTIntType)) {
			DiagnosticReporter.error(astLessThan.getToken(),
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
			DiagnosticReporter.error(astLessThanOrEqual.getToken(), "Incorrect types used with <= oprator");
		} else if (!(typeLeftSide instanceof ASTIntType) || !(typeRightSide instanceof ASTIntType)) {
			DiagnosticReporter.error(astLessThanOrEqual.getToken(),
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
			DiagnosticReporter.error(astGreaterThan.getToken(), "Incorrect types used with > oprator");
		} else if (!(typeLeftSide instanceof ASTIntType) || !(typeRightSide instanceof ASTIntType)) {
			DiagnosticReporter.error(astGreaterThan.getToken(),
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
			DiagnosticReporter.error(astGreaterThanOrEqual.getToken(), "Incorrect types used with >= oprator");
		} else if (!(typeLeftSide instanceof ASTIntType) || !(typeRightSide instanceof ASTIntType)) {
			DiagnosticReporter.error(astGreaterThanOrEqual.getToken(),
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
			DiagnosticReporter.error(astAnd.getToken(), "Incorrect types used with && oprator");
		} else if (!(typeLeftSide instanceof ASTBooleanType) || !(typeRightSide instanceof ASTBooleanType)) {
			DiagnosticReporter.error(astAnd.getLeftSide().getToken(),
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
			DiagnosticReporter.error(astOr.getToken(), "Incorrect types used with || oprator");
		} else if (!(typeLeftSide instanceof ASTBooleanType) || !(typeRightSide instanceof ASTBooleanType)) {
			DiagnosticReporter.error(astOr.getLeftSide().getToken(),
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
			DiagnosticReporter.error(astNewArray.getArrayLength().getToken(), "Array length must be of type int");
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

		SymbolFunction symbolFunction = null;
		if (symbolClass == null) {
			symbolFunction = symbolProgram.getFunction(functionName.toString());
		} else {
			symbolFunction = symbolProgram.getFunction(functionName.toString(), symbolClass.getId());
		}

		if (symbolFunction == null) {
			DiagnosticReporter.error(functionName.getToken(), "Function " + functionName + " not declared");
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

	private void checkCallArguments(ASTCallFunctionExpr astCallFunctionExpr, SymbolFunction symbolFunction)
	{
		List<ASTType> argumentTypes = new ArrayList<>();

		for (ASTExpression astExpression : astCallFunctionExpr.getArgumentList()) {
			ASTType astType = astExpression.accept(this);
			argumentTypes.add(astType);
		}

		if (astCallFunctionExpr.getArgumentListSize() != symbolFunction.getParamsSize()) {
			DiagnosticReporter.error(astCallFunctionExpr.getToken(), "The function " + symbolFunction.toString()
					+ " is not applicable for the arguments (" + getArguments(argumentTypes) + ")");
			return;
		}

		for (int i = 0; i < argumentTypes.size(); i++) {
			SymbolVariable symbolVariable = symbolFunction.getParamAt(i);
			ASTType type1 = symbolVariable.getType();
			ASTType type2 = argumentTypes.get(i);

			if (!symbolProgram.compareTypes(type1, type2)) {
				DiagnosticReporter.error(astCallFunctionExpr.getArgumentAt(i).getToken(),
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
				DiagnosticReporter.error(astVariableInit.getToken(),
						"Incompatible types used with assignment Operator = ");
			} else {
				DiagnosticReporter.error(astVariableInit.getToken(),
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

		if (processedFunctions.contains(functionName)) {
			return astFunction.getReturnType();
		}

		processedFunctions.add(functionName);
		symbolFunction = (SymbolFunction) astFunction.getFunctionName().getB();

		for (ASTArgument astArgument : astFunction.getArgumentList()) {
			astArgument.accept(this);;
		}

		astFunction.getBody().accept(this);
		symbolFunction = null;
		return null;
	}

	@Override
	public ASTType visit(ASTProgram astProgram)
	{
		for (AST node : astProgram.getNodeList()) {
			node.accept(this);
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
			DiagnosticReporter.error(astArrayIndexExpr.getArray().getToken(),
					"Array expression must evaluate to be of Type int[]");
		} else {
			astArrayIndexExpr.getArray().setType(typeArray);
		}

		ASTType typeArrayIndex = astArrayIndexExpr.getIndex().accept(this);
		if (typeArrayIndex == null || !(typeArrayIndex instanceof ASTIntType)) {
			DiagnosticReporter.error(astArrayIndexExpr.getIndex().getToken(),
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
			DiagnosticReporter.error(astArrayAssign.getId().getToken(), "Identifier must be of Type int[]");
		}

		ASTType typeArrayExpr1 = astArrayAssign.getExpression1().accept(this);
		if (typeArrayExpr1 == null || !(typeArrayExpr1 instanceof ASTIntType)) {
			DiagnosticReporter.error(astArrayAssign.getExpression1().getToken(), "Expression must be of Type int");
		} else {
			astArrayAssign.getExpression1().setType(typeArrayExpr1);
		}

		ASTType typeArrayExpr2 = astArrayAssign.getExpression2().accept(this);
		if (typeArrayExpr2 == null || !(typeArrayExpr2 instanceof ASTIntType)) {
			DiagnosticReporter.error(astArrayAssign.getExpression2().getToken(), "Expression must be of Type int");
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
		if (processedClasses.contains(className)) {
			return null;
		}
		processedClasses.add(className);

		Binding binding = astClass.getClassName().getB();
		symbolClass = (SymbolClass) binding;
		processedFunctions.clear();

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
			DiagnosticReporter.error(astConditionalBranch.getCondition().getToken(),
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

	@Override
	public ASTType visit(ASTStringArrayType astStringArrayType)
	{
		return astStringArrayType;
	}

	@Override
	public ASTType visit(ASTArrayLiteral astArrayLiteral)
	{
		ASTType astType = new ASTStringArrayType(astArrayLiteral.getToken());
		astArrayLiteral.setType(astType);
		return astType;
	}

	@Override
	public ASTType visit(ASTForeach astForeach)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTLambda astLambda)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTImport astImport)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTParameterizedType astParameterizedType)
	{
		return null;
	}

	@Override
	public ASTType visit(ASTInterface astInterface)
	{
		return null;
	}
}
