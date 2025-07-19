package knight.compiler.optimizations;

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
import knight.compiler.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

public class ConstantFolding implements ASTVisitor<ASTExpression>
{
	private int changes;
	private boolean debug = false;

	public static void optimize(AST program) throws ParseException
	{
		ConstantFolding optimizer = new ConstantFolding();
		optimizer.setDebug(true);
		int totalChanges = 0;

		do {
			optimizer.changes = 0;
			program.accept(optimizer);
			totalChanges += optimizer.changes;
		} while (optimizer.changes > 0);

		if (optimizer.debug) {
			System.out.println("[ConstantFolding] Total optimizations: " + totalChanges);
		}
	}

	public void setDebug(boolean debug)
	{
		this.debug = debug;
	}

	private void incChanges()
	{
		changes++;
	}

	private void logOptimization(String message)
	{
		if (debug) {
			System.out.println("[Optimization] " + message);
		}
	}

	private ASTExpression createIntLiteral(AST source, int value)
	{
		incChanges();
		ASTIntLiteral lit = new ASTIntLiteral(source.getToken(), value);
		lit.setType(new ASTIntType(source.getToken()));
		return lit;
	}

	private ASTExpression createBoolLiteral(AST source, boolean value)
	{
		incChanges();
		ASTExpression lit = value ? new ASTTrue(source.getToken()) : new ASTFalse(source.getToken());
		lit.setType(new ASTBooleanType(source.getToken()));
		return lit;
	}

	private ASTExpression createStringLiteral(AST source, String value)
	{
		incChanges();
		ASTStringLiteral lit = new ASTStringLiteral(source.getToken(), value);
		lit.setType(new ASTStringType(source.getToken()));
		return lit;
	}

	private boolean isIntLiteral(ASTExpression expr, int value)
	{
		return expr instanceof ASTIntLiteral lit && lit.getValue() == value;
	}

	private boolean isConstant(ASTExpression expr)
	{
		return expr instanceof ASTIntLiteral || expr instanceof ASTStringLiteral || expr instanceof ASTTrue
				|| expr instanceof ASTFalse;
	}

	@Override
	public ASTExpression visit(ASTAssign astAssign)
	{
		ASTExpression newExpr = astAssign.getExpr().accept(this);
		astAssign.setExpr(newExpr);
		if (newExpr != astAssign.getExpr()) {
			logOptimization("Optimized assignment expression");
		}
		return null;
	}

	@Override
	public ASTExpression visit(ASTBody astBody)
	{
		for (AST node : astBody.getNodesList()) {
			node.accept(this);
		}

		return null;
	}

	@Override
	public ASTExpression visit(ASTWhile astWhile)
	{
		ASTExpression condition = astWhile.getCondition().accept(this);
		astWhile.setCondition(condition);

		if (condition instanceof ASTFalse) {
			logOptimization("Removed while(false) loop");
			astWhile.getBody().getNodesList().clear();
		}

		astWhile.getBody().accept(this);
		return null;
	}

	@Override
	public ASTExpression visit(ASTIntLiteral astIntLiteral)
	{
		return astIntLiteral;
	}

	@Override
	public ASTExpression visit(ASTPlus astPlus)
	{
		astPlus.setLeftSide(astPlus.getLeftSide().accept(this));
		astPlus.setRightSide(astPlus.getRightSide().accept(this));

		if (astPlus.getType() instanceof ASTIntType) {

			if (astPlus.getLeftSide()instanceof ASTIntLiteral left
					&& astPlus.getRightSide()instanceof ASTIntLiteral right) {
				int result = left.getValue() + right.getValue();
				logOptimization("Folded " + left.getValue() + " + " + right.getValue());
				return createIntLiteral(astPlus, result);
			}

		} else {
			if (astPlus.getLeftSide()instanceof ASTStringLiteral leftStr
					&& astPlus.getRightSide()instanceof ASTStringLiteral rightStr) {
				String result = leftStr.getValue() + rightStr.getValue();
				logOptimization("Folded string concatenation");
				return createStringLiteral(astPlus, result);
			} else if (isConstant(astPlus.getLeftSide()) && isConstant(astPlus.getRightSide())) {
				String left = astPlus.getLeftSide()instanceof ASTStringLiteral sl ? sl.getValue()
						: astPlus.getLeftSide()instanceof ASTIntLiteral il ? String.valueOf(il.getValue()) : "";
				String right = astPlus.getRightSide()instanceof ASTStringLiteral sl ? sl.getValue()
						: astPlus.getRightSide()instanceof ASTIntLiteral il ? String.valueOf(il.getValue()) : "";
				logOptimization("Folded mixed concatenation");
				return createStringLiteral(astPlus, left + right);
			}
		}
		return astPlus;
	}

	@Override
	public ASTExpression visit(ASTMinus astMinus)
	{
		astMinus.setLeftSide(astMinus.getLeftSide().accept(this));
		astMinus.setRightSide(astMinus.getRightSide().accept(this));

		if (astMinus.getLeftSide()instanceof ASTIntLiteral left
				&& astMinus.getRightSide()instanceof ASTIntLiteral right) {
			int result = left.getValue() - right.getValue();
			logOptimization("Folded " + left.getValue() + " - " + right.getValue());
			return createIntLiteral(astMinus, result);
		}
		return astMinus;
	}

	@Override
	public ASTExpression visit(ASTTimes astTimes)
	{
		astTimes.setLeftSide(astTimes.getLeftSide().accept(this));
		astTimes.setRightSide(astTimes.getRightSide().accept(this));

		if (astTimes.getLeftSide()instanceof ASTIntLiteral left
				&& astTimes.getRightSide()instanceof ASTIntLiteral right) {
			int result = left.getValue() * right.getValue();
			logOptimization("Folded " + left.getValue() + " * " + right.getValue());
			return createIntLiteral(astTimes, result);
		} else if (isIntLiteral(astTimes.getRightSide(), 2)) {
			logOptimization("Strength reduction: multiplication by 2 to addition");
			ASTPlus plus = new ASTPlus(astTimes.getToken(), astTimes.getLeftSide(), astTimes.getRightSide());
			plus.setType(astTimes.getType());
			return plus.accept(this);
		} else if (isIntLiteral(astTimes.getRightSide(), 1)) {
			logOptimization("Identity optimization: x * 1 = x");
			return astTimes.getLeftSide();
		} else if (isIntLiteral(astTimes.getLeftSide(), 1)) {
			logOptimization("Identity optimization: 1 * x = x");
			return astTimes.getRightSide();
		} else if (isIntLiteral(astTimes.getLeftSide(), 0) || isIntLiteral(astTimes.getRightSide(), 0)) {
			logOptimization("Zero optimization: x * 0 = 0");
			return createIntLiteral(astTimes, 0);
		}
		return astTimes;
	}

	@Override
	public ASTExpression visit(ASTDivision astDivision)
	{
		astDivision.setLeftSide(astDivision.getLeftSide().accept(this));
		astDivision.setRightSide(astDivision.getRightSide().accept(this));

		if (astDivision.getLeftSide()instanceof ASTIntLiteral left
				&& astDivision.getRightSide()instanceof ASTIntLiteral right) {
			if (right.getValue() == 0) {
				logOptimization("Division by zero left as-is for runtime error");
				return astDivision;
			}
			int result = left.getValue() / right.getValue();
			logOptimization("Folded " + left.getValue() + " / " + right.getValue());
			return createIntLiteral(astDivision, result);
		}
		return astDivision;
	}

	@Override
	public ASTExpression visit(ASTModulus astModulus)
	{
		astModulus.setLeftSide(astModulus.getLeftSide().accept(this));
		astModulus.setRightSide(astModulus.getRightSide().accept(this));

		if (astModulus.getLeftSide()instanceof ASTIntLiteral left
				&& astModulus.getRightSide()instanceof ASTIntLiteral right) {
			if (right.getValue() == 0) {
				logOptimization("Modulus by zero left as-is for runtime error");
				return astModulus;
			}
			int result = left.getValue() % right.getValue();
			logOptimization("Folded " + left.getValue() + " % " + right.getValue());
			return createIntLiteral(astModulus, result);
		}
		return astModulus;
	}

	@Override
	public ASTExpression visit(ASTEquals astEquals)
	{
		astEquals.setLeftSide(astEquals.getLeftSide().accept(this));
		astEquals.setRightSide(astEquals.getRightSide().accept(this));

		ASTType type = astEquals.getType();
		if (type instanceof ASTIntType) {
			if (astEquals.getLeftSide()instanceof ASTIntLiteral left
					&& astEquals.getRightSide()instanceof ASTIntLiteral right) {
				boolean result = left.getValue() == right.getValue();
				logOptimization("Folded int comparison");
				return createBoolLiteral(astEquals, result);
			}
		} else if (type instanceof ASTBooleanType) {
			if ((astEquals.getLeftSide() instanceof ASTTrue && astEquals.getRightSide() instanceof ASTTrue)
					|| (astEquals.getLeftSide() instanceof ASTFalse && astEquals.getRightSide() instanceof ASTFalse)) {
				logOptimization("Folded boolean equality (true)");
				return createBoolLiteral(astEquals, true);
			} else if ((astEquals.getLeftSide() instanceof ASTTrue && astEquals.getRightSide() instanceof ASTFalse)
					|| (astEquals.getLeftSide() instanceof ASTFalse && astEquals.getRightSide() instanceof ASTTrue)) {
				logOptimization("Folded boolean equality (false)");
				return createBoolLiteral(astEquals, false);
			}
		} else if (type instanceof ASTStringType) {
			if (astEquals.getLeftSide()instanceof ASTStringLiteral left
					&& astEquals.getRightSide()instanceof ASTStringLiteral right) {
				boolean result = left.getValue().equals(right.getValue());
				logOptimization("Folded string equality");
				return createBoolLiteral(astEquals, result);
			}
		}
		return astEquals;
	}

	@Override
	public ASTExpression visit(ASTNotEquals notEquals)
	{
		notEquals.setLeftSide(notEquals.getLeftSide().accept(this));
		notEquals.setRightSide(notEquals.getRightSide().accept(this));

		if (notEquals.getLeftSide()instanceof ASTIntLiteral left
				&& notEquals.getRightSide()instanceof ASTIntLiteral right) {
			boolean result = left.getValue() != right.getValue();
			logOptimization(String.format("Folded %d != %d to %b", left.getValue(), right.getValue(), result));
			return createBoolLiteral(notEquals, result);
		}

		return notEquals;
	}

	@Override
	public ASTExpression visit(ASTLessThan astLessThan)
	{
		astLessThan.setLeftSide(astLessThan.getLeftSide().accept(this));
		astLessThan.setRightSide(astLessThan.getRightSide().accept(this));

		if (astLessThan.getLeftSide()instanceof ASTIntLiteral left
				&& astLessThan.getRightSide()instanceof ASTIntLiteral right) {
			boolean result = left.getValue() < right.getValue();
			logOptimization(String.format("Folded %d < %d to %b", left.getValue(), right.getValue(), result));
			return createBoolLiteral(astLessThan, result);
		}

		return astLessThan;
	}

	@Override
	public ASTExpression visit(ASTLessThanOrEqual astLessThanOrEqual)
	{
		astLessThanOrEqual.setLeftSide(astLessThanOrEqual.getLeftSide().accept(this));
		astLessThanOrEqual.setRightSide(astLessThanOrEqual.getRightSide().accept(this));

		if (astLessThanOrEqual.getLeftSide()instanceof ASTIntLiteral left
				&& astLessThanOrEqual.getRightSide()instanceof ASTIntLiteral right) {
			boolean result = left.getValue() <= right.getValue();
			logOptimization(String.format("Folded %d <= %d to %b", left.getValue(), right.getValue(), result));
			return createBoolLiteral(astLessThanOrEqual, result);
		}

		return astLessThanOrEqual;
	}

	@Override
	public ASTExpression visit(ASTGreaterThan astGreaterThan)
	{
		astGreaterThan.setLeftSide(astGreaterThan.getLeftSide().accept(this));
		astGreaterThan.setRightSide(astGreaterThan.getRightSide().accept(this));

		if (astGreaterThan.getLeftSide()instanceof ASTIntLiteral left
				&& astGreaterThan.getRightSide()instanceof ASTIntLiteral right) {
			boolean result = left.getValue() > right.getValue();
			logOptimization(String.format("Folded %d > %d to %b", left.getValue(), right.getValue(), result));
			return createBoolLiteral(astGreaterThan, result);
		}

		return astGreaterThan;
	}

	@Override
	public ASTExpression visit(ASTGreaterThanOrEqual astGreaterThanOrEqual)
	{
		astGreaterThanOrEqual.setLeftSide(astGreaterThanOrEqual.getLeftSide().accept(this));
		astGreaterThanOrEqual.setRightSide(astGreaterThanOrEqual.getRightSide().accept(this));

		if (astGreaterThanOrEqual.getLeftSide()instanceof ASTIntLiteral left
				&& astGreaterThanOrEqual.getRightSide()instanceof ASTIntLiteral right) {
			boolean result = left.getValue() >= right.getValue();
			logOptimization(String.format("Folded %d >= %d to %b", left.getValue(), right.getValue(), result));
			return createBoolLiteral(astGreaterThanOrEqual, result);
		}

		return astGreaterThanOrEqual;
	}

	@Override
	public ASTExpression visit(ASTAnd astAnd)
	{
		astAnd.setLeftSide(astAnd.getLeftSide().accept(this));
		astAnd.setRightSide(astAnd.getRightSide().accept(this));

		if (astAnd.getLeftSide() instanceof ASTFalse) {
			logOptimization("Short-circuited AND (left false)");
			return createBoolLiteral(astAnd, false);
		}

		if (astAnd.getRightSide() instanceof ASTFalse) {
			logOptimization("Short-circuited AND (right false)");
			return createBoolLiteral(astAnd, false);
		}

		if (astAnd.getLeftSide() instanceof ASTTrue && astAnd.getRightSide() instanceof ASTTrue) {
			logOptimization("Folded AND (true && true)");
			return createBoolLiteral(astAnd, true);
		}

		return astAnd;
	}

	@Override
	public ASTExpression visit(ASTOr astOr)
	{
		astOr.setLeftSide(astOr.getLeftSide().accept(this));
		astOr.setRightSide(astOr.getRightSide().accept(this));

		if (astOr.getLeftSide() instanceof ASTTrue) {
			logOptimization("Short-circuited OR (left true)");
			return createBoolLiteral(astOr, true);
		}

		if (astOr.getRightSide() instanceof ASTTrue) {
			logOptimization("Short-circuited OR (right true)");
			return createBoolLiteral(astOr, true);
		}

		if (astOr.getLeftSide() instanceof ASTFalse && astOr.getRightSide() instanceof ASTFalse) {
			logOptimization("Folded OR (false || false)");
			return createBoolLiteral(astOr, false);
		}

		return astOr;
	}

	@Override
	public ASTExpression visit(ASTIfChain astIfChain)
	{
		List<ASTConditionalBranch> liveBranches = new ArrayList<>();

		for (ASTConditionalBranch branch : astIfChain.getBranches()) {
			ASTExpression cond = branch.getCondition().accept(this);

			if (cond instanceof ASTFalse) {
				logOptimization("Removed branch with false condition");
				continue;
			}

			if (cond instanceof ASTTrue) {
				logOptimization("Pruning if-chain after true condition");
				liveBranches.add(branch);
				break;
			}

			branch.setCondition(cond);
			liveBranches.add(branch);
		}

		astIfChain.setBranches(liveBranches);

		if (astIfChain.getElseBody() != null && !liveBranches.isEmpty()
				&& !(liveBranches.get(liveBranches.size() - 1).getCondition() instanceof ASTTrue)) {
			astIfChain.getElseBody().accept(this);
		} else {
			astIfChain.setElseBody(null);
		}

		return null;
	}

	@Override
	public ASTExpression visit(ASTTrue astTrue)
	{
		return astTrue;
	}

	@Override
	public ASTExpression visit(ASTFalse astFalse)
	{
		return astFalse;
	}

	@Override
	public ASTExpression visit(ASTIdentifierExpr astIdentifier)
	{
		return astIdentifier;
	}

	@Override
	public ASTExpression visit(ASTNewArray astNewArray)
	{
		return astNewArray;
	}

	@Override
	public ASTExpression visit(ASTNewInstance astNewInstance)
	{
		return astNewInstance;
	}

	@Override
	public ASTExpression visit(ASTCallFunctionExpr astCallFunctionExpr)
	{
		return astCallFunctionExpr;
	}

	@Override
	public ASTExpression visit(ASTCallFunctionStat astCallFunctionStat)
	{
		List<ASTExpression> argumentList = new ArrayList<>();

		for (int i = 0; i < astCallFunctionStat.getArgumentListSize(); i++) {
			argumentList.add(astCallFunctionStat.getArgumentAt(i).accept(this));
		}

		astCallFunctionStat.setArgumentList(argumentList);

		return null;
	}

	@Override
	public ASTExpression visit(ASTIntType astIntType)
	{
		return null;
	}

	@Override
	public ASTExpression visit(ASTStringType astStringType)
	{
		return null;
	}

	@Override
	public ASTExpression visit(ASTBooleanType astBooleanType)
	{
		return null;
	}

	@Override
	public ASTExpression visit(ASTFunctionType astFunctionType)
	{
		return null;
	}

	@Override
	public ASTExpression visit(ASTIntArrayType astIntArrayType)
	{
		return null;
	}

	@Override
	public ASTExpression visit(ASTVoidType astVoidType)
	{
		return null;
	}

	@Override
	public ASTExpression visit(ASTIdentifierType astIdentifierType)
	{
		return null;
	}

	@Override
	public ASTExpression visit(ASTVariable astVariable)
	{
		return null;
	}

	@Override
	public ASTExpression visit(ASTVariableInit astVariableInit)
	{
		astVariableInit.setExpr(astVariableInit.getExpr().accept(this));
		return null;
	}

	@Override
	public ASTExpression visit(ASTFunction astFunction)
	{
		astFunction.getBody().accept(this);
		return null;
	}

	@Override
	public ASTExpression visit(ASTProgram astProgram)
	{
		for (AST node : astProgram.getNodeList()) {
			node.accept(this);
		}
		return null;
	}

	@Override
	public ASTExpression visit(ASTIdentifier astIdentifier)
	{
		return null;
	}

	@Override
	public ASTExpression visit(ASTArrayIndexExpr astArrayIndexExpr)
	{
		astArrayIndexExpr.setArray(astArrayIndexExpr.getArray().accept(this));
		astArrayIndexExpr.setIndex(astArrayIndexExpr.getIndex().accept(this));
		return astArrayIndexExpr;
	}

	@Override
	public ASTExpression visit(ASTArrayAssign astArrayAssign)
	{
		astArrayAssign.setE1(astArrayAssign.getExpression1().accept(this));
		astArrayAssign.setE2(astArrayAssign.getExpression2().accept(this));
		return null;
	}

	@Override
	public ASTExpression visit(ASTStringLiteral astStringLiteral)
	{
		return astStringLiteral;
	}

	@Override
	public ASTExpression visit(ASTClass astClass)
	{
		for (ASTProperty property : astClass.getPropertyList()) {
			property.accept(this);
		}
		for (ASTFunction function : astClass.getFunctionList()) {
			function.accept(this);
		}
		return null;
	}

	@Override
	public ASTExpression visit(ASTReturnStatement astReturnStatement)
	{
		return null;
	}

	@Override
	public ASTExpression visit(ASTProperty astProperty)
	{
		return null;
	}

	@Override
	public ASTExpression visit(ASTConditionalBranch astConditionalBranch)
	{
		astConditionalBranch.setCondition(astConditionalBranch.getCondition().accept(this));
		astConditionalBranch.getBody().accept(this);
		return null;
	}

	@Override
	public ASTExpression visit(ASTArgument astArgument)
	{
		return null;
	}

	@Override
	public ASTExpression visit(ASTStringArrayType astStringArrayType)
	{
		return null;
	}

	@Override
	public ASTExpression visit(ASTArrayLiteral astArrayLiteral)
	{
		return astArrayLiteral;
	}

	@Override
	public ASTExpression visit(ASTForeach astForeach)
	{
		astForeach.getIterable().accept(this);
		astForeach.getVariable().accept(this);
		astForeach.getBody().accept(this);
		return null;
	}

	@Override
	public ASTExpression visit(ASTLambda astLambda)
	{
		astLambda.getArgumentList().forEach(arg -> arg.accept(this));
		astLambda.getReturnType().accept(this);
		astLambda.getBody().accept(this);
		return null;
	}

	@Override
	public ASTExpression visit(ASTImport astImport)
	{
		return null;
	}

	@Override
	public ASTExpression visit(ASTParameterizedType astParameterizedType)
	{
		return null;
	}

	@Override
	public ASTExpression visit(ASTInterface astInterface)
	{
		return null;
	}
}
