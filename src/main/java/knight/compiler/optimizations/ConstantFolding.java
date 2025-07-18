package knight.compiler.optimizations;

import java.util.ArrayList;
import java.util.List;

import knight.compiler.ast.*;
import knight.compiler.ast.controlflow.ASTConditionalBranch;
import knight.compiler.ast.controlflow.ASTForeach;
import knight.compiler.ast.controlflow.ASTIfChain;
import knight.compiler.ast.controlflow.ASTWhile;
import knight.compiler.ast.expressions.*;
import knight.compiler.ast.program.*;
import knight.compiler.ast.statements.*;
import knight.compiler.ast.types.ASTBooleanType;
import knight.compiler.ast.types.ASTFunctionType;
import knight.compiler.ast.types.ASTIdentifierType;
import knight.compiler.ast.types.ASTIntArrayType;
import knight.compiler.ast.types.ASTIntType;
import knight.compiler.ast.types.ASTParameterizedType;
import knight.compiler.ast.types.ASTStringArrayType;
import knight.compiler.ast.types.ASTStringType;
import knight.compiler.ast.types.ASTType;
import knight.compiler.ast.types.ASTVoidType;
import knight.compiler.parser.ParseException;

public class ConstantFolding implements ASTVisitor<ASTExpression>
{
	private int changes;

	public static void optimize(AST program) throws ParseException
	{
		int total = 0;
		ConstantFolding optimizer = new ConstantFolding();
		optimizer.changes = 0;
		program.accept(optimizer);

		while (optimizer.changes > 0) {
			total += optimizer.changes;
			optimizer.changes = 0;
			program.accept(optimizer);
		}

		System.out.println("Total optimization changes: " + total);
	}

	@Override
	public ASTExpression visit(ASTAssign astAssign)
	{
		astAssign.setExpr(astAssign.getExpr().accept(this));
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
		astWhile.setCondition(astWhile.getCondition().accept(this));
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

			if (astPlus.getLeftSide() instanceof ASTIntLiteral && astPlus.getRightSide() instanceof ASTIntLiteral) {
				ASTIntLiteral leftSide = (ASTIntLiteral) astPlus.getLeftSide();
				ASTIntLiteral rightSide = (ASTIntLiteral) astPlus.getRightSide();
				int result = leftSide.getValue() + rightSide.getValue();
				incChanges();
				ASTIntLiteral astIntLiteral = new ASTIntLiteral(leftSide.getToken(), result);
				astIntLiteral.setType(astPlus.getType());
				return astIntLiteral;
			}

		} else {

			if (astPlus.getLeftSide() instanceof ASTStringLiteral
					&& astPlus.getRightSide() instanceof ASTStringLiteral) {
				ASTStringLiteral l = (ASTStringLiteral) astPlus.getLeftSide();
				ASTStringLiteral r = (ASTStringLiteral) astPlus.getRightSide();
				StringBuilder sb = new StringBuilder();
				sb.append(l.getValue());
				sb.append(r.getValue());
				incChanges();
				ASTStringLiteral op = new ASTStringLiteral(l.getToken(), sb.toString());
				op.setType(new ASTStringType(l.getToken()));
				return op;

			} else if (astPlus.getLeftSide() instanceof ASTIntLiteral
					&& astPlus.getRightSide() instanceof ASTStringLiteral) {
				ASTIntLiteral l = (ASTIntLiteral) astPlus.getLeftSide();
				ASTStringLiteral r = (ASTStringLiteral) astPlus.getRightSide();
				StringBuilder sb = new StringBuilder();
				sb.append(l.getValue());
				sb.append(r.getValue());
				incChanges();
				ASTStringLiteral op = new ASTStringLiteral(r.getToken(), sb.toString());
				op.setType(new ASTStringType(r.getToken()));
				return op;

			} else if (astPlus.getLeftSide() instanceof ASTStringLiteral
					&& astPlus.getRightSide() instanceof ASTIntLiteral) {
				ASTStringLiteral l = (ASTStringLiteral) astPlus.getLeftSide();
				ASTIntLiteral r = (ASTIntLiteral) astPlus.getRightSide();
				StringBuilder sb = new StringBuilder();
				sb.append(l.getValue());
				sb.append(r.getValue());
				incChanges();
				ASTStringLiteral op = new ASTStringLiteral(l.getToken(), sb.toString());
				op.setType(new ASTStringType(l.getToken()));
				return op;
			}
		}
		return astPlus;
	}

	@Override
	public ASTExpression visit(ASTMinus astMinus)
	{
		astMinus.setLeftSide(astMinus.getLeftSide().accept(this));
		astMinus.setRightSide(astMinus.getRightSide().accept(this));

		if (astMinus.getLeftSide() instanceof ASTIntLiteral && astMinus.getRightSide() instanceof ASTIntLiteral) {
			ASTIntLiteral leftSide = (ASTIntLiteral) astMinus.getLeftSide();
			ASTIntLiteral rightSide = (ASTIntLiteral) astMinus.getRightSide();
			int result = leftSide.getValue() - rightSide.getValue();
			incChanges();
			ASTIntLiteral astIntLiteral = new ASTIntLiteral(leftSide.getToken(), result);
			astIntLiteral.setType(new ASTIntType(leftSide.getToken()));
			return astIntLiteral;
		}
		return astMinus;
	}

	@Override
	public ASTExpression visit(ASTTimes astTimes)
	{
		astTimes.setLeftSide(astTimes.getLeftSide().accept(this));
		astTimes.setRightSide(astTimes.getRightSide().accept(this));

		if (astTimes.getLeftSide() instanceof ASTIntLiteral && astTimes.getRightSide() instanceof ASTIntLiteral) {
			ASTIntLiteral leftSide = (ASTIntLiteral) astTimes.getLeftSide();
			ASTIntLiteral rightSide = (ASTIntLiteral) astTimes.getRightSide();
			int result = leftSide.getValue() * rightSide.getValue();
			incChanges();
			ASTIntLiteral astIntLiteral = new ASTIntLiteral(leftSide.getToken(), result);
			astIntLiteral.setType(new ASTIntType(leftSide.getToken()));
			return astIntLiteral;
		}
		return astTimes;
	}

	@Override
	public ASTExpression visit(ASTDivision astDivision)
	{
		astDivision.setLeftSide(astDivision.getLeftSide().accept(this));
		astDivision.setRightSide(astDivision.getRightSide().accept(this));

		if (astDivision.getLeftSide() instanceof ASTIntLiteral && astDivision.getRightSide() instanceof ASTIntLiteral) {
			ASTIntLiteral leftSide = (ASTIntLiteral) astDivision.getLeftSide();
			ASTIntLiteral rightSide = (ASTIntLiteral) astDivision.getRightSide();
			if (rightSide.getValue() != 0) {
				int result = leftSide.getValue() / rightSide.getValue();
				incChanges();
				ASTIntLiteral astIntLiteral = new ASTIntLiteral(leftSide.getToken(), result);
				astIntLiteral.setType(new ASTIntType(leftSide.getToken()));
				return astIntLiteral;
			}
		}

		return astDivision;
	}

	@Override
	public ASTExpression visit(ASTEquals astEquals)
	{
		astEquals.setLeftSide(astEquals.getLeftSide().accept(this));
		astEquals.setRightSide(astEquals.getRightSide().accept(this));

		ASTType t = astEquals.getType();
		if (t instanceof ASTIntType) {
			if (astEquals.getLeftSide() instanceof ASTIntLiteral && astEquals.getRightSide() instanceof ASTIntLiteral) {
				ASTIntLiteral l = (ASTIntLiteral) astEquals.getLeftSide();
				ASTIntLiteral r = (ASTIntLiteral) astEquals.getRightSide();
				if (l.getValue() == r.getValue()) {
					incChanges();
					ASTTrue op = new ASTTrue(astEquals.getToken());
					op.setType(new ASTBooleanType(astEquals.getToken()));
					return op;
				} else {
					incChanges();
					ASTFalse op = new ASTFalse(astEquals.getToken());
					op.setType(new ASTBooleanType(astEquals.getToken()));
					return op;
				}
			}
		} else if (t instanceof ASTBooleanType) {
			if ((astEquals.getLeftSide() instanceof ASTTrue && astEquals.getRightSide() instanceof ASTTrue)
					|| (astEquals.getLeftSide() instanceof ASTFalse && astEquals.getRightSide() instanceof ASTFalse)) {
				incChanges();
				ASTTrue op = new ASTTrue(astEquals.getToken());
				op.setType(new ASTBooleanType(astEquals.getToken()));
				return op;

			} else if ((astEquals.getLeftSide() instanceof ASTTrue && astEquals.getRightSide() instanceof ASTFalse)
					|| (astEquals.getLeftSide() instanceof ASTFalse && astEquals.getRightSide() instanceof ASTTrue)) {
				incChanges();
				ASTFalse op = new ASTFalse(astEquals.getToken());
				op.setType(new ASTBooleanType(astEquals.getToken()));
				return op;
			}
		} else if (t instanceof ASTStringType) {
			if (astEquals.getLeftSide() instanceof ASTStringLiteral
					&& astEquals.getRightSide() instanceof ASTStringLiteral) {
				ASTStringLiteral l = (ASTStringLiteral) astEquals.getLeftSide();
				ASTStringLiteral r = (ASTStringLiteral) astEquals.getRightSide();

				if (l.getValue() == null) {
					if (r.getValue() == null) {
						incChanges();
						ASTTrue op = new ASTTrue(astEquals.getToken());
						op.setType(new ASTBooleanType(astEquals.getToken()));
						return op;
					}
				} else if (l.getValue().equals(r.getValue())) {
					incChanges();
					ASTTrue op = new ASTTrue(astEquals.getToken());
					op.setType(new ASTBooleanType(astEquals.getToken()));
					return op;
				} else {
					incChanges();
					ASTFalse op = new ASTFalse(astEquals.getToken());
					op.setType(new ASTBooleanType(astEquals.getToken()));
					return op;
				}
			}
		} else if (t instanceof ASTIdentifierType) {
			if (astEquals.getLeftSide() instanceof ASTIdentifierExpr
					&& astEquals.getRightSide() instanceof ASTIdentifierExpr) {
				ASTIdentifierExpr l = (ASTIdentifierExpr) astEquals.getLeftSide();
				ASTIdentifierExpr r = (ASTIdentifierExpr) astEquals.getRightSide();
				if (l.getId().equals(r.getId())) {
					incChanges();
					ASTTrue op = new ASTTrue(astEquals.getToken());
					op.setType(new ASTBooleanType(astEquals.getToken()));
					return op;
				}
			}
		}

		return astEquals;
	}

	@Override
	public ASTExpression visit(ASTNotEquals notEquals)
	{
		notEquals.setLeftSide(notEquals.getLeftSide().accept(this));
		notEquals.setRightSide(notEquals.getRightSide().accept(this));
		return notEquals;
	}

	@Override
	public ASTExpression visit(ASTLessThan astLessThan)
	{
		astLessThan.setLeftSide(astLessThan.getLeftSide().accept(this));
		astLessThan.setRightSide(astLessThan.getRightSide().accept(this));

		ASTType astType = astLessThan.getType();
		if (astType instanceof ASTIntType) {
			if (astLessThan.getLeftSide() instanceof ASTIntLiteral
					&& astLessThan.getRightSide() instanceof ASTIntLiteral) {
				ASTIntLiteral leftSide = (ASTIntLiteral) astLessThan.getLeftSide();
				ASTIntLiteral rightSide = (ASTIntLiteral) astLessThan.getRightSide();
				if (leftSide.getValue() < rightSide.getValue()) {
					incChanges();
					ASTTrue astTrue = new ASTTrue(astLessThan.getToken());
					astTrue.setType(new ASTBooleanType(astLessThan.getToken()));
					return astTrue;
				} else {
					incChanges();
					ASTFalse astFalse = new ASTFalse(astLessThan.getToken());
					astFalse.setType(new ASTBooleanType(astLessThan.getToken()));
					return astFalse;
				}
			}
		}
		return astLessThan;
	}

	@Override
	public ASTExpression visit(ASTAnd astAnd)
	{
		astAnd.setLeftSide(astAnd.getLeftSide().accept(this));
		astAnd.setRightSide(astAnd.getRightSide().accept(this));

		ASTType astType = astAnd.getType();
		if (astType instanceof ASTBooleanType) {
			if ((astAnd.getLeftSide() instanceof ASTTrue) && (astAnd.getRightSide() instanceof ASTTrue)) {
				incChanges();
				ASTTrue astTrue = new ASTTrue(astAnd.getToken());
				astTrue.setType(new ASTBooleanType(astAnd.getToken()));
				return astTrue;

			} else if ((astAnd.getLeftSide() instanceof ASTFalse) || (astAnd.getRightSide() instanceof ASTFalse)) {
				incChanges();
				ASTFalse astFalse = new ASTFalse(astAnd.getToken());
				astFalse.setType(new ASTBooleanType(astAnd.getToken()));
				return astFalse;

			}
		}

		return astAnd;
	}

	@Override
	public ASTExpression visit(ASTOr astOr)
	{
		astOr.setLeftSide(astOr.getLeftSide().accept(this));
		astOr.setRightSide(astOr.getRightSide().accept(this));

		ASTType astType = astOr.getType();
		if (astType instanceof ASTBooleanType) {
			if ((astOr.getLeftSide() instanceof ASTTrue) || (astOr.getRightSide() instanceof ASTTrue)) {
				incChanges();
				ASTTrue astTrue = new ASTTrue(astOr.getToken());
				astTrue.setType(new ASTBooleanType(astOr.getToken()));
				return astTrue;

			} else if ((astOr.getLeftSide() instanceof ASTFalse) && (astOr.getRightSide() instanceof ASTFalse)) {
				incChanges();
				ASTFalse astFalse = new ASTFalse(astOr.getToken());
				astFalse.setType(new ASTBooleanType(astOr.getToken()));
				return astFalse;
			}
		}

		return astOr;
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

//	@Override
//	public ASTExpression visit(ASTFunctionReturn astFunctionReturn)
//	{
//		astFunctionReturn.getBody().accept(this);
//		astFunctionReturn.setReturnExpr(astFunctionReturn.getReturnExpr().accept(this));
//		return null;
//	}

	@Override
	public ASTExpression visit(ASTProgram astProgram)
	{
		for (AST node : astProgram.getNodeList()) {
			node.accept(this);
		}

//		for (ASTFunction astFunction : astProgram.getFunctionList()) {
//			astFunction.accept(this);
//		}
//
//		for (ASTVariable astVariable : astProgram.getVariableList()) {
//			astVariable.accept(this);
//		}

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
	public ASTExpression visit(ASTIfChain astIfChain)
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
	public ASTExpression visit(ASTLessThanOrEqual astLessThanOrEqual)
	{
		astLessThanOrEqual.setLeftSide(astLessThanOrEqual.getLeftSide().accept(this));
		astLessThanOrEqual.setRightSide(astLessThanOrEqual.getRightSide().accept(this));

		ASTType astType = astLessThanOrEqual.getType();
		if (astType instanceof ASTIntType) {
			if (astLessThanOrEqual.getLeftSide() instanceof ASTIntLiteral
					&& astLessThanOrEqual.getRightSide() instanceof ASTIntLiteral) {
				ASTIntLiteral leftSide = (ASTIntLiteral) astLessThanOrEqual.getLeftSide();
				ASTIntLiteral rightSide = (ASTIntLiteral) astLessThanOrEqual.getRightSide();
				if (leftSide.getValue() > rightSide.getValue()) {
					incChanges();
					ASTTrue astTrue = new ASTTrue(astLessThanOrEqual.getToken());
					astTrue.setType(new ASTBooleanType(astLessThanOrEqual.getToken()));
					return astTrue;
				} else {
					incChanges();
					ASTFalse astFalse = new ASTFalse(astLessThanOrEqual.getToken());
					astFalse.setType(new ASTBooleanType(astLessThanOrEqual.getToken()));
					return astFalse;
				}
			}
		}
		return astLessThanOrEqual;
	}

	@Override
	public ASTExpression visit(ASTGreaterThan astGreaterThan)
	{
		astGreaterThan.setLeftSide(astGreaterThan.getLeftSide().accept(this));
		astGreaterThan.setRightSide(astGreaterThan.getRightSide().accept(this));

		ASTType t = astGreaterThan.getType();
		if (t instanceof ASTIntType) {
			if (astGreaterThan.getLeftSide() instanceof ASTIntLiteral
					&& astGreaterThan.getRightSide() instanceof ASTIntLiteral) {
				ASTIntLiteral l = (ASTIntLiteral) astGreaterThan.getLeftSide();
				ASTIntLiteral r = (ASTIntLiteral) astGreaterThan.getRightSide();
				if (l.getValue() > r.getValue()) {
					incChanges();
					ASTTrue astTrue = new ASTTrue(astGreaterThan.getToken());
					astTrue.setType(new ASTBooleanType(astGreaterThan.getToken()));
					return astTrue;
				} else {
					incChanges();
					ASTFalse astFalse = new ASTFalse(astGreaterThan.getToken());
					astFalse.setType(new ASTBooleanType(astGreaterThan.getToken()));
					return astFalse;
				}
			}
		}
		return astGreaterThan;
	}

	@Override
	public ASTExpression visit(ASTGreaterThanOrEqual astGreaterThanOrEqual)
	{
		astGreaterThanOrEqual.setLeftSide(astGreaterThanOrEqual.getLeftSide().accept(this));
		astGreaterThanOrEqual.setRightSide(astGreaterThanOrEqual.getRightSide().accept(this));

		ASTType astType = astGreaterThanOrEqual.getType();
		if (astType instanceof ASTIntType) {
			if (astGreaterThanOrEqual.getLeftSide() instanceof ASTIntLiteral
					&& astGreaterThanOrEqual.getRightSide() instanceof ASTIntLiteral) {
				ASTIntLiteral leftSide = (ASTIntLiteral) astGreaterThanOrEqual.getLeftSide();
				ASTIntLiteral rightSide = (ASTIntLiteral) astGreaterThanOrEqual.getRightSide();
				if (leftSide.getValue() >= leftSide.getValue()) {
					incChanges();
					ASTTrue astTrue = new ASTTrue(astGreaterThanOrEqual.getToken());
					astTrue.setType(new ASTBooleanType(astGreaterThanOrEqual.getToken()));
					return astTrue;
				} else {
					incChanges();
					ASTFalse astFalse = new ASTFalse(astGreaterThanOrEqual.getToken());
					astFalse.setType(new ASTBooleanType(astGreaterThanOrEqual.getToken()));
					return astFalse;
				}
			}
		}
		return astGreaterThanOrEqual;
	}

	@Override
	public ASTExpression visit(ASTModulus astModulus)
	{
		return null;
	}

	private void incChanges()
	{
		changes++;
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
		return null;
	}

	@Override
	public ASTExpression visit(ASTLambda astLambda)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ASTExpression visit(ASTImport astImport)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ASTExpression visit(ASTParameterizedType astParameterizedType)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ASTExpression visit(ASTInterface astInterface)
	{
		return null;
	}
}
