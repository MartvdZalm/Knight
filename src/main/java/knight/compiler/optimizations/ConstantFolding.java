package knight.compiler.optimizations;

import knight.compiler.ast.AST;
import knight.compiler.ast.ASTVisitor;
import knight.compiler.ast.controlflow.ASTConditionalBranch;
import knight.compiler.ast.controlflow.ASTForEach;
import knight.compiler.ast.controlflow.ASTIfChain;
import knight.compiler.ast.controlflow.ASTWhile;
import knight.compiler.ast.expressions.*;
import knight.compiler.ast.program.*;
import knight.compiler.ast.statements.*;
import knight.compiler.ast.types.*;

import java.util.ArrayList;
import java.util.List;

public class ConstantFolding implements ASTVisitor<ASTExpression>
{
	private int changes;
	private boolean debug = false;

	public static void optimize(AST program)
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
		ASTExpression newExpr = astAssign.getExpression().accept(this);
		astAssign.setExpression(newExpr);
		if (newExpr != astAssign.getExpression()) {
			logOptimization("Optimized assignment expression");
		}
		return null;
	}

	@Override
	public ASTExpression visit(ASTFieldAssign astFieldAssign)
	{
		return null;
	}

	@Override
	public ASTExpression visit(ASTBody astBody)
	{
		for (AST node : astBody.getNodes()) {
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
			astWhile.getBody().getNodes().clear();
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
		astPlus.setLeft(astPlus.getLeft().accept(this));
		astPlus.setRight(astPlus.getRight().accept(this));

		if (astPlus.getType() instanceof ASTIntType) {

			if (astPlus.getLeft()instanceof ASTIntLiteral left && astPlus.getRight()instanceof ASTIntLiteral right) {
				int result = left.getValue() + right.getValue();
				logOptimization("Folded " + left.getValue() + " + " + right.getValue());
				return createIntLiteral(astPlus, result);
			}

		} else {
			if (astPlus.getLeft()instanceof ASTStringLiteral leftStr
					&& astPlus.getRight()instanceof ASTStringLiteral rightStr) {
				String result = leftStr.getValue() + rightStr.getValue();
				logOptimization("Folded string concatenation");
				return createStringLiteral(astPlus, result);
			} else if (isConstant(astPlus.getLeft()) && isConstant(astPlus.getRight())) {
				String left = astPlus.getLeft()instanceof ASTStringLiteral sl ? sl.getValue()
						: astPlus.getLeft()instanceof ASTIntLiteral il ? String.valueOf(il.getValue()) : "";
				String right = astPlus.getRight()instanceof ASTStringLiteral sl ? sl.getValue()
						: astPlus.getRight()instanceof ASTIntLiteral il ? String.valueOf(il.getValue()) : "";
				logOptimization("Folded mixed concatenation");
				return createStringLiteral(astPlus, left + right);
			}
		}
		return astPlus;
	}

	@Override
	public ASTExpression visit(ASTMinus astMinus)
	{
		astMinus.setLeft(astMinus.getLeft().accept(this));
		astMinus.setRight(astMinus.getRight().accept(this));

		if (astMinus.getLeft()instanceof ASTIntLiteral left && astMinus.getRight()instanceof ASTIntLiteral right) {
			int result = left.getValue() - right.getValue();
			logOptimization("Folded " + left.getValue() + " - " + right.getValue());
			return createIntLiteral(astMinus, result);
		}
		return astMinus;
	}

	@Override
	public ASTExpression visit(ASTTimes astTimes)
	{
		astTimes.setLeft(astTimes.getLeft().accept(this));
		astTimes.setRight(astTimes.getRight().accept(this));

		if (astTimes.getLeft()instanceof ASTIntLiteral left && astTimes.getRight()instanceof ASTIntLiteral right) {
			int result = left.getValue() * right.getValue();
			logOptimization("Folded " + left.getValue() + " * " + right.getValue());
			return createIntLiteral(astTimes, result);
		} else if (isIntLiteral(astTimes.getRight(), 2)) {
			logOptimization("Strength reduction: multiplication by 2 to addition");
			ASTPlus plus = new ASTPlus(astTimes.getToken(), astTimes.getLeft(), astTimes.getRight());
			plus.setType(astTimes.getType());
			return plus.accept(this);
		} else if (isIntLiteral(astTimes.getRight(), 1)) {
			logOptimization("Identity optimization: x * 1 = x");
			return astTimes.getLeft();
		} else if (isIntLiteral(astTimes.getLeft(), 1)) {
			logOptimization("Identity optimization: 1 * x = x");
			return astTimes.getRight();
		} else if (isIntLiteral(astTimes.getLeft(), 0) || isIntLiteral(astTimes.getRight(), 0)) {
			logOptimization("Zero optimization: x * 0 = 0");
			return createIntLiteral(astTimes, 0);
		}
		return astTimes;
	}

	@Override
	public ASTExpression visit(ASTDivision astDivision)
	{
		astDivision.setLeft(astDivision.getLeft().accept(this));
		astDivision.setRight(astDivision.getRight().accept(this));

		if (astDivision.getLeft()instanceof ASTIntLiteral left
				&& astDivision.getRight()instanceof ASTIntLiteral right) {
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
		astModulus.setLeft(astModulus.getLeft().accept(this));
		astModulus.setRight(astModulus.getRight().accept(this));

		if (astModulus.getLeft()instanceof ASTIntLiteral left && astModulus.getRight()instanceof ASTIntLiteral right) {
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
		astEquals.setLeft(astEquals.getLeft().accept(this));
		astEquals.setRight(astEquals.getRight().accept(this));

		ASTType type = astEquals.getType();
		if (type instanceof ASTIntType) {
			if (astEquals.getLeft()instanceof ASTIntLiteral left
					&& astEquals.getRight()instanceof ASTIntLiteral right) {
				boolean result = left.getValue() == right.getValue();
				logOptimization("Folded int comparison");
				return createBoolLiteral(astEquals, result);
			}
		} else if (type instanceof ASTBooleanType) {
			if ((astEquals.getLeft() instanceof ASTTrue && astEquals.getRight() instanceof ASTTrue)
					|| (astEquals.getLeft() instanceof ASTFalse && astEquals.getRight() instanceof ASTFalse)) {
				logOptimization("Folded boolean equality (true)");
				return createBoolLiteral(astEquals, true);
			} else if ((astEquals.getLeft() instanceof ASTTrue && astEquals.getRight() instanceof ASTFalse)
					|| (astEquals.getLeft() instanceof ASTFalse && astEquals.getRight() instanceof ASTTrue)) {
				logOptimization("Folded boolean equality (false)");
				return createBoolLiteral(astEquals, false);
			}
		} else if (type instanceof ASTStringType) {
			if (astEquals.getLeft()instanceof ASTStringLiteral left
					&& astEquals.getRight()instanceof ASTStringLiteral right) {
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
		notEquals.setLeft(notEquals.getLeft().accept(this));
		notEquals.setRight(notEquals.getRight().accept(this));

		if (notEquals.getLeft()instanceof ASTIntLiteral left && notEquals.getRight()instanceof ASTIntLiteral right) {
			boolean result = left.getValue() != right.getValue();
			logOptimization(String.format("Folded %d != %d to %b", left.getValue(), right.getValue(), result));
			return createBoolLiteral(notEquals, result);
		}

		return notEquals;
	}

	@Override
	public ASTExpression visit(ASTLessThan astLessThan)
	{
		astLessThan.setLeft(astLessThan.getLeft().accept(this));
		astLessThan.setRight(astLessThan.getRight().accept(this));

		if (astLessThan.getLeft()instanceof ASTIntLiteral left
				&& astLessThan.getRight()instanceof ASTIntLiteral right) {
			boolean result = left.getValue() < right.getValue();
			logOptimization(String.format("Folded %d < %d to %b", left.getValue(), right.getValue(), result));
			return createBoolLiteral(astLessThan, result);
		}

		return astLessThan;
	}

	@Override
	public ASTExpression visit(ASTLessThanOrEqual astLessThanOrEqual)
	{
		astLessThanOrEqual.setLeft(astLessThanOrEqual.getLeft().accept(this));
		astLessThanOrEqual.setRight(astLessThanOrEqual.getRight().accept(this));

		if (astLessThanOrEqual.getLeft()instanceof ASTIntLiteral left
				&& astLessThanOrEqual.getRight()instanceof ASTIntLiteral right) {
			boolean result = left.getValue() <= right.getValue();
			logOptimization(String.format("Folded %d <= %d to %b", left.getValue(), right.getValue(), result));
			return createBoolLiteral(astLessThanOrEqual, result);
		}

		return astLessThanOrEqual;
	}

	@Override
	public ASTExpression visit(ASTGreaterThan astGreaterThan)
	{
		astGreaterThan.setLeft(astGreaterThan.getLeft().accept(this));
		astGreaterThan.setRight(astGreaterThan.getRight().accept(this));

		if (astGreaterThan.getLeft()instanceof ASTIntLiteral left
				&& astGreaterThan.getRight()instanceof ASTIntLiteral right) {
			boolean result = left.getValue() > right.getValue();
			logOptimization(String.format("Folded %d > %d to %b", left.getValue(), right.getValue(), result));
			return createBoolLiteral(astGreaterThan, result);
		}

		return astGreaterThan;
	}

	@Override
	public ASTExpression visit(ASTGreaterThanOrEqual astGreaterThanOrEqual)
	{
		astGreaterThanOrEqual.setLeft(astGreaterThanOrEqual.getLeft().accept(this));
		astGreaterThanOrEqual.setRight(astGreaterThanOrEqual.getRight().accept(this));

		if (astGreaterThanOrEqual.getLeft()instanceof ASTIntLiteral left
				&& astGreaterThanOrEqual.getRight()instanceof ASTIntLiteral right) {
			boolean result = left.getValue() >= right.getValue();
			logOptimization(String.format("Folded %d >= %d to %b", left.getValue(), right.getValue(), result));
			return createBoolLiteral(astGreaterThanOrEqual, result);
		}

		return astGreaterThanOrEqual;
	}

	@Override
	public ASTExpression visit(ASTAnd astAnd)
	{
		astAnd.setLeft(astAnd.getLeft().accept(this));
		astAnd.setRight(astAnd.getRight().accept(this));

		if (astAnd.getLeft() instanceof ASTFalse) {
			logOptimization("Short-circuited AND (left false)");
			return createBoolLiteral(astAnd, false);
		}

		if (astAnd.getRight() instanceof ASTFalse) {
			logOptimization("Short-circuited AND (right false)");
			return createBoolLiteral(astAnd, false);
		}

		if (astAnd.getLeft() instanceof ASTTrue && astAnd.getRight() instanceof ASTTrue) {
			logOptimization("Folded AND (true && true)");
			return createBoolLiteral(astAnd, true);
		}

		return astAnd;
	}

	@Override
	public ASTExpression visit(ASTOr astOr)
	{
		astOr.setLeft(astOr.getLeft().accept(this));
		astOr.setRight(astOr.getRight().accept(this));

		if (astOr.getLeft() instanceof ASTTrue) {
			logOptimization("Short-circuited OR (left true)");
			return createBoolLiteral(astOr, true);
		}

		if (astOr.getRight() instanceof ASTTrue) {
			logOptimization("Short-circuited OR (right true)");
			return createBoolLiteral(astOr, true);
		}

		if (astOr.getLeft() instanceof ASTFalse && astOr.getRight() instanceof ASTFalse) {
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

		for (int i = 0; i < astCallFunctionStat.getArgumentCount(); i++) {
			argumentList.add(astCallFunctionStat.getArgument(i).accept(this));
		}

		astCallFunctionStat.setArguments(argumentList);

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
		astVariableInit.setExpression(astVariableInit.getExpression().accept(this));
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
		for (AST node : astProgram.getNodes()) {
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
		astArrayAssign.setArray(astArrayAssign.getArray().accept(this));
		astArrayAssign.setValue(astArrayAssign.getValue().accept(this));
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
		for (ASTProperty property : astClass.getProperties()) {
			property.accept(this);
		}
		for (ASTFunction function : astClass.getFunctions()) {
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
	public ASTExpression visit(ASTForEach astForEach)
	{
		astForEach.getIterable().accept(this);
		astForEach.getVariable().accept(this);
		astForEach.getBody().accept(this);
		return null;
	}

	@Override
	public ASTExpression visit(ASTLambda astLambda)
	{
		astLambda.getArguments().forEach(arg -> arg.accept(this));
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
