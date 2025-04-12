package knight.compiler.passes.optimizations;

import java.util.ArrayList;
import java.util.List;

import knight.compiler.ast.AST;
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
import knight.compiler.ast.ASTPointerAssign;
import knight.compiler.ast.ASTProgram;
import knight.compiler.ast.ASTProperty;
import knight.compiler.ast.ASTReturnStatement;
import knight.compiler.ast.ASTStringLiteral;
import knight.compiler.ast.ASTStringType;
import knight.compiler.ast.ASTThis;
import knight.compiler.ast.ASTTimes;
import knight.compiler.ast.ASTTrue;
import knight.compiler.ast.ASTType;
import knight.compiler.ast.ASTVariable;
import knight.compiler.ast.ASTVariableInit;
import knight.compiler.ast.ASTVisitor;
import knight.compiler.ast.ASTVoidType;
import knight.compiler.ast.ASTWhile;
import knight.compiler.parser.ParseException;

/*
 * File: ConstantFolding.java
 * @author: Mart van der Zalm
 * Date: 2025-04-10
 */
public class ConstantFolding implements ASTVisitor<ASTExpression>
{
	private int changes;

	@Override
	public ASTExpression visit(ASTAssign assign)
	{
		assign.setExpr(assign.getExpr().accept(this));
		return null;
	}

	@Override
	public ASTExpression visit(ASTBody body)
	{
		// for (int i = 0; i < body.getStatementListSize(); i++) {
		// ASTStatement st = body.getStatementAt(i);
		// st.accept(this);
		// }
		return null;
	}

//	@Override
//	public ASTExpression visit(ASTIfThenElse ifThenElse)
//	{
//		ifThenElse.setExpr(ifThenElse.getExpr().accept(this));
//		ifThenElse.getThen().accept(this);
//		ifThenElse.getElze().accept(this);
//		return null;
//	}

	@Override
	public ASTExpression visit(ASTWhile w)
	{
		w.setCondition(w.getCondition().accept(this));
		w.getBody().accept(this);
		return null;
	}

	@Override
	public ASTExpression visit(ASTIntLiteral n)
	{
		return n;
	}

	@Override
	public ASTExpression visit(ASTPlus plus)
	{
		plus.setLeftSide(plus.getLeftSide().accept(this));
		plus.setRightSide(plus.getRightSide().accept(this));

		if (plus.getType() instanceof ASTIntType) {

			if (plus.getLeftSide() instanceof ASTIntLiteral && plus.getRightSide() instanceof ASTIntLiteral) {
				ASTIntLiteral l = (ASTIntLiteral) plus.getLeftSide();
				ASTIntLiteral r = (ASTIntLiteral) plus.getRightSide();
				int result = l.getValue() + r.getValue();
				incChanges();
				ASTIntLiteral op = new ASTIntLiteral(l.getToken(), result);
				op.setType(plus.getType());
				return op;
			}

		} else {

			if (plus.getLeftSide() instanceof ASTStringLiteral && plus.getRightSide() instanceof ASTStringLiteral) {
				ASTStringLiteral l = (ASTStringLiteral) plus.getLeftSide();
				ASTStringLiteral r = (ASTStringLiteral) plus.getRightSide();
				StringBuilder sb = new StringBuilder();
				sb.append(l.getValue());
				sb.append(r.getValue());
				incChanges();
				ASTStringLiteral op = new ASTStringLiteral(l.getToken(), sb.toString());
				op.setType(new ASTStringType(l.getToken()));
				return op;

			} else if (plus.getLeftSide() instanceof ASTIntLiteral && plus.getRightSide() instanceof ASTStringLiteral) {
				ASTIntLiteral l = (ASTIntLiteral) plus.getLeftSide();
				ASTStringLiteral r = (ASTStringLiteral) plus.getRightSide();
				StringBuilder sb = new StringBuilder();
				sb.append(l.getValue());
				sb.append(r.getValue());
				incChanges();
				ASTStringLiteral op = new ASTStringLiteral(r.getToken(), sb.toString());
				op.setType(new ASTStringType(r.getToken()));
				return op;

			} else if (plus.getLeftSide() instanceof ASTStringLiteral && plus.getRightSide() instanceof ASTIntLiteral) {
				ASTStringLiteral l = (ASTStringLiteral) plus.getLeftSide();
				ASTIntLiteral r = (ASTIntLiteral) plus.getRightSide();
				StringBuilder sb = new StringBuilder();
				sb.append(l.getValue());
				sb.append(r.getValue());
				incChanges();
				ASTStringLiteral op = new ASTStringLiteral(l.getToken(), sb.toString());
				op.setType(new ASTStringType(l.getToken()));
				return op;
			}
		}
		return plus;
	}

	@Override
	public ASTExpression visit(ASTMinus minus)
	{
		minus.setLeftSide(minus.getLeftSide().accept(this));
		minus.setRightSide(minus.getRightSide().accept(this));

		if (minus.getLeftSide() instanceof ASTIntLiteral && minus.getRightSide() instanceof ASTIntLiteral) {
			ASTIntLiteral l = (ASTIntLiteral) minus.getLeftSide();
			ASTIntLiteral r = (ASTIntLiteral) minus.getRightSide();
			int result = l.getValue() - r.getValue();
			incChanges();
			ASTIntLiteral op = new ASTIntLiteral(l.getToken(), result);
			op.setType(new ASTIntType(l.getToken()));
			return op;
		}
		return minus;
	}

	@Override
	public ASTExpression visit(ASTTimes times)
	{
		times.setLeftSide(times.getLeftSide().accept(this));
		times.setRightSide(times.getRightSide().accept(this));

		if (times.getLeftSide() instanceof ASTIntLiteral && times.getRightSide() instanceof ASTIntLiteral) {
			ASTIntLiteral l = (ASTIntLiteral) times.getLeftSide();
			ASTIntLiteral r = (ASTIntLiteral) times.getRightSide();
			int result = l.getValue() * r.getValue();
			incChanges();
			ASTIntLiteral op = new ASTIntLiteral(l.getToken(), result);
			op.setType(new ASTIntType(l.getToken()));
			return op;
		}
		return times;
	}

	@Override
	public ASTExpression visit(ASTDivision division)
	{
		division.setLeftSide(division.getLeftSide().accept(this));
		division.setRightSide(division.getRightSide().accept(this));

		if (division.getLeftSide() instanceof ASTIntLiteral && division.getRightSide() instanceof ASTIntLiteral) {
			ASTIntLiteral l = (ASTIntLiteral) division.getLeftSide();
			ASTIntLiteral r = (ASTIntLiteral) division.getRightSide();
			if (r.getValue() != 0) {
				int result = l.getValue() / r.getValue();
				incChanges();
				ASTIntLiteral op = new ASTIntLiteral(l.getToken(), result);
				op.setType(new ASTIntType(l.getToken()));
				return op;
			}
		}

		return division;
	}

	@Override
	public ASTExpression visit(ASTEquals equals)
	{
		equals.setLeftSide(equals.getLeftSide().accept(this));
		equals.setRightSide(equals.getRightSide().accept(this));

		ASTType t = equals.getType();
		if (t instanceof ASTIntType) {
			if (equals.getLeftSide() instanceof ASTIntLiteral && equals.getRightSide() instanceof ASTIntLiteral) {
				ASTIntLiteral l = (ASTIntLiteral) equals.getLeftSide();
				ASTIntLiteral r = (ASTIntLiteral) equals.getRightSide();
				if (l.getValue() == r.getValue()) {
					incChanges();
					ASTTrue op = new ASTTrue(equals.getToken());
					op.setType(new ASTBooleanType(equals.getToken()));
					return op;
				} else {
					incChanges();
					ASTFalse op = new ASTFalse(equals.getToken());
					op.setType(new ASTBooleanType(equals.getToken()));
					return op;
				}
			}
		} else if (t instanceof ASTBooleanType) {
			if ((equals.getLeftSide() instanceof ASTTrue && equals.getRightSide() instanceof ASTTrue)
					|| (equals.getLeftSide() instanceof ASTFalse && equals.getRightSide() instanceof ASTFalse)) {
				incChanges();
				ASTTrue op = new ASTTrue(equals.getToken());
				op.setType(new ASTBooleanType(equals.getToken()));
				return op;

			} else if ((equals.getLeftSide() instanceof ASTTrue && equals.getRightSide() instanceof ASTFalse)
					|| (equals.getLeftSide() instanceof ASTFalse && equals.getRightSide() instanceof ASTTrue)) {
				incChanges();
				ASTFalse op = new ASTFalse(equals.getToken());
				op.setType(new ASTBooleanType(equals.getToken()));
				return op;
			}
		} else if (t instanceof ASTStringType) {
			if (equals.getLeftSide() instanceof ASTStringLiteral && equals.getRightSide() instanceof ASTStringLiteral) {
				ASTStringLiteral l = (ASTStringLiteral) equals.getLeftSide();
				ASTStringLiteral r = (ASTStringLiteral) equals.getRightSide();

				if (l.getValue() == null) {
					if (r.getValue() == null) {
						incChanges();
						ASTTrue op = new ASTTrue(equals.getToken());
						op.setType(new ASTBooleanType(equals.getToken()));
						return op;
					}
				} else if (l.getValue().equals(r.getValue())) {
					incChanges();
					ASTTrue op = new ASTTrue(equals.getToken());
					op.setType(new ASTBooleanType(equals.getToken()));
					return op;
				} else {
					incChanges();
					ASTFalse op = new ASTFalse(equals.getToken());
					op.setType(new ASTBooleanType(equals.getToken()));
					return op;
				}
			}
		} else if (t instanceof ASTIdentifierType) {
			if (equals.getLeftSide() instanceof ASTIdentifierExpr
					&& equals.getRightSide() instanceof ASTIdentifierExpr) {
				ASTIdentifierExpr l = (ASTIdentifierExpr) equals.getLeftSide();
				ASTIdentifierExpr r = (ASTIdentifierExpr) equals.getRightSide();
				if (l.getId().equals(r.getId())) {
					incChanges();
					ASTTrue op = new ASTTrue(equals.getToken());
					op.setType(new ASTBooleanType(equals.getToken()));
					return op;
				}
			}
		}

		return equals;
	}

	@Override
	public ASTExpression visit(ASTNotEquals equals)
	{
		equals.setLeftSide(equals.getLeftSide().accept(this));
		equals.setRightSide(equals.getRightSide().accept(this));
		return equals;
	}

	@Override
	public ASTExpression visit(ASTLessThan lessThan)
	{
		lessThan.setLeftSide(lessThan.getLeftSide().accept(this));
		lessThan.setRightSide(lessThan.getRightSide().accept(this));

		ASTType t = lessThan.getType();
		if (t instanceof ASTIntType) {
			if (lessThan.getLeftSide() instanceof ASTIntLiteral && lessThan.getRightSide() instanceof ASTIntLiteral) {
				ASTIntLiteral l = (ASTIntLiteral) lessThan.getLeftSide();
				ASTIntLiteral r = (ASTIntLiteral) lessThan.getRightSide();
				if (l.getValue() < r.getValue()) {
					incChanges();
					ASTTrue op = new ASTTrue(lessThan.getToken());
					op.setType(new ASTBooleanType(lessThan.getToken()));
					return op;
				} else {
					incChanges();
					ASTFalse op = new ASTFalse(lessThan.getToken());
					op.setType(new ASTBooleanType(lessThan.getToken()));
					return op;
				}
			}
		}
		return lessThan;
	}

	@Override
	public ASTExpression visit(ASTAnd and)
	{
		and.setLeftSide(and.getLeftSide().accept(this));
		and.setRightSide(and.getRightSide().accept(this));

		ASTType t = and.getType();
		if (t instanceof ASTBooleanType) {
			if ((and.getLeftSide() instanceof ASTTrue) && (and.getRightSide() instanceof ASTTrue)) {
				incChanges();
				ASTTrue op = new ASTTrue(and.getToken());
				op.setType(new ASTBooleanType(and.getToken()));
				return op;

			} else if ((and.getLeftSide() instanceof ASTFalse) || (and.getRightSide() instanceof ASTFalse)) {
				incChanges();
				ASTFalse op = new ASTFalse(and.getToken());
				op.setType(new ASTBooleanType(and.getToken()));
				return op;

			}
		}

		return and;
	}

	@Override
	public ASTExpression visit(ASTOr or)
	{
		or.setLeftSide(or.getLeftSide().accept(this));
		or.setRightSide(or.getRightSide().accept(this));

		ASTType t = or.getType();
		if (t instanceof ASTBooleanType) {
			if ((or.getLeftSide() instanceof ASTTrue) || (or.getRightSide() instanceof ASTTrue)) {
				incChanges();
				ASTTrue op = new ASTTrue(or.getToken());
				op.setType(new ASTBooleanType(or.getToken()));
				return op;

			} else if ((or.getLeftSide() instanceof ASTFalse) && (or.getRightSide() instanceof ASTFalse)) {
				incChanges();
				ASTFalse op = new ASTFalse(or.getToken());
				op.setType(new ASTBooleanType(or.getToken()));
				return op;

			}
		}

		return or;
	}

	@Override
	public ASTExpression visit(ASTTrue true1)
	{
		return true1;
	}

	@Override
	public ASTExpression visit(ASTFalse false1)
	{
		return false1;
	}

	@Override
	public ASTExpression visit(ASTIdentifierExpr identifier)
	{
		return identifier;
	}

	@Override
	public ASTExpression visit(ASTNewArray newArray)
	{
		return newArray;
	}

	@Override
	public ASTExpression visit(ASTNewInstance newInstance)
	{
		return newInstance;
	}

	@Override
	public ASTExpression visit(ASTCallFunctionExpr callFunctionExpr)
	{
		return callFunctionExpr;
	}

	@Override
	public ASTExpression visit(ASTCallFunctionStat callFunctionStat)
	{
		List<ASTExpression> argExprList = new ArrayList<>();

		for (int i = 0; i < callFunctionStat.getArgumentListSize(); i++) {
			argExprList.add(callFunctionStat.getArgumentAt(i).accept(this));
		}

		callFunctionStat.setArgumentList(argExprList);

		return null;
	}

	@Override
	public ASTExpression visit(ASTIntType intType)
	{
		return null;
	}

	@Override
	public ASTExpression visit(ASTStringType stringType)
	{
		return null;
	}

	@Override
	public ASTExpression visit(ASTBooleanType booleanType)
	{
		return null;
	}

	@Override
	public ASTExpression visit(ASTFunctionType functionType)
	{
		return null;
	}

	@Override
	public ASTExpression visit(ASTIntArrayType intArrayType)
	{
		return null;
	}

	@Override
	public ASTExpression visit(ASTVoidType voidType)
	{
		return null;
	}

	@Override
	public ASTExpression visit(ASTIdentifierType referenceType)
	{
		return null;
	}

	@Override
	public ASTExpression visit(ASTVariable variable)
	{
		return null;
	}

	@Override
	public ASTExpression visit(ASTVariableInit variable)
	{
		variable.setExpr(variable.getExpr().accept(this));

		return null;
	}

//	@Override
//	public ASTExpression visit(ASTArgument argument)
//	{
//		return null;
//	}

	@Override
	public ASTExpression visit(ASTFunction function)
	{
//		for (int i = 0; i < function.getStatementListSize(); i++) {
//			function.getStatementDeclAt(i).accept(this);
//		}

		return null;
	}

	@Override
	public ASTExpression visit(ASTFunctionReturn function)
	{
//		for (int i = 0; i < function.getStatementListSize(); i++) {
//			function.getStatementDeclAt(i).accept(this);
//		}
//
//		for (int i = 0; i < function.getVariableListSize(); i++) {
//			function.getVariableDeclAt(i).accept(this);
//		}

		function.setReturnExpr(function.getReturnExpr().accept(this));
		return null;
	}

	@Override
	public ASTExpression visit(ASTProgram program)
	{
		for (int i = 0; i < program.getFunctionListSize(); i++) {
			program.getFunctionDeclAt(i).accept(this);
		}

		for (int i = 0; i < program.getVariableListSize(); i++) {
			program.getVariableDeclAt(i).accept(this);
		}

		return null;
	}

	@Override
	public ASTExpression visit(ASTIdentifier id)
	{
		return null;
	}

	@Override
	public ASTExpression visit(ASTArrayIndexExpr arrayIndexExpr)
	{
		arrayIndexExpr.setArray(arrayIndexExpr.getArray().accept(this));
		arrayIndexExpr.setIndex(arrayIndexExpr.getIndex().accept(this));
		return arrayIndexExpr;
	}

	@Override
	public ASTExpression visit(ASTArrayAssign arrayAssign)
	{
		arrayAssign.setE1(arrayAssign.getExpression1().accept(this));
		arrayAssign.setE2(arrayAssign.getExpression2().accept(this));
		return null;
	}

	@Override
	public ASTExpression visit(ASTStringLiteral stringLiteral)
	{
		return stringLiteral;
	}

	@Override
	public ASTExpression visit(ASTClass classDecl)
	{
		for (ASTProperty property : classDecl.getPropertyList()) {
			property.accept(this);
		}
		for (ASTFunction function : classDecl.getFunctionList()) {
			function.accept(this);
		}
		return null;
	}

	@Override
	public ASTExpression visit(ASTReturnStatement returnStatement)
	{
		return null;
	}

	@Override
	public ASTExpression visit(ASTPointerAssign pointerAssign)
	{
		return null;
	}

	@Override
	public ASTExpression visit(ASTThis astThis)
	{
		return null;
	}

	@Override
	public ASTExpression visit(ASTProperty property)
	{
		return null;
	}

	private void incChanges()
	{
		changes++;
	}

	public static void optimize(AST prog) throws ParseException
	{
		int total = 0;
		ConstantFolding optimizer = new ConstantFolding();
		optimizer.changes = 0;
		prog.accept(optimizer);
		while (optimizer.changes > 0) {
			total += optimizer.changes;
			optimizer.changes = 0;
			prog.accept(optimizer);
		}
		System.out.println("Total optimization changes = " + total);
	}

	@Override
	public ASTExpression visit(ASTIfChain ifChain)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ASTExpression visit(ASTConditionalBranch astConditionalBranch)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ASTExpression visit(ASTArgument astArgument)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ASTExpression visit(ASTLessThanOrEqual astLessThanOrEqual)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ASTExpression visit(ASTGreaterThan astGreaterThan)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ASTExpression visit(ASTGreaterThanOrEqual astGreaterThanOrEqual)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ASTExpression visit(ASTModulus astModulus)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
