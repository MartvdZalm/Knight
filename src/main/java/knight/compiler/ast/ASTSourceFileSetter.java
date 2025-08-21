package knight.compiler.ast;

import knight.compiler.ast.program.*;
import knight.compiler.ast.controlflow.*;
import knight.compiler.ast.expressions.*;
import knight.compiler.ast.statements.*;
import knight.compiler.ast.types.*;

public class ASTSourceFileSetter implements ASTVisitor<Void>
{
	private final String sourceFile;

	public ASTSourceFileSetter(String sourceFile)
	{
		this.sourceFile = sourceFile;
	}

	public void setSourceFileRecursively(AST ast)
	{
		if (ast != null) {
			ast.accept(this);
		}
	}

	@Override
	public Void visit(ASTProgram astProgram)
	{
		astProgram.setSourceFile(sourceFile);
		for (ASTImport astImport : astProgram.getImportList()) {
			astImport.accept(this);
		}
		for (AST node : astProgram.getNodeList()) {
			node.accept(this);
		}
		return null;
	}

	@Override
	public Void visit(ASTImport astImport)
	{
		astImport.setSourceFile(sourceFile);
		return null;
	}

	@Override
	public Void visit(ASTClass astClass)
	{
		astClass.setSourceFile(sourceFile);
		astClass.getClassName().accept(this);
		if (astClass.getExtendsClass() != null) {
			astClass.getExtendsClass().accept(this);
		}
		for (ASTIdentifier astInterface : astClass.getImplementsInterfaces()) {
			astInterface.accept(this);
		}
		for (ASTProperty astProperty : astClass.getPropertyList()) {
			astProperty.accept(this);
		}
		for (ASTFunction astFunction : astClass.getFunctionList()) {
			astFunction.accept(this);
		}
		return null;
	}

	@Override
	public Void visit(ASTInterface astInterface)
	{
		astInterface.setSourceFile(sourceFile);
		astInterface.getName().accept(this);
		for (ASTFunction astFunction : astInterface.getFunctionSignatures()) {
			astFunction.accept(this);
		}
		return null;
	}

	@Override
	public Void visit(ASTFunction astFunction)
	{
		astFunction.setSourceFile(sourceFile);
		astFunction.getFunctionName().accept(this);
		astFunction.getReturnType().accept(this);
		for (ASTArgument astArgument : astFunction.getArgumentList()) {
			astArgument.accept(this);
		}
		astFunction.getBody().accept(this);
		return null;
	}

	@Override
	public Void visit(ASTVariable astVariable)
	{
		astVariable.setSourceFile(sourceFile);
		astVariable.getType().accept(this);
		astVariable.getId().accept(this);
		return null;
	}

	@Override
	public Void visit(ASTVariableInit astVariableInit)
	{
		astVariableInit.setSourceFile(sourceFile);
		astVariableInit.getType().accept(this);
		astVariableInit.getId().accept(this);
		astVariableInit.getExpr().accept(this);
		return null;
	}

	@Override
	public Void visit(ASTArgument astArgument)
	{
		astArgument.setSourceFile(sourceFile);
		astArgument.getType().accept(this);
		astArgument.getIdentifier().accept(this);
		return null;
	}

	@Override
	public Void visit(ASTProperty astProperty)
	{
		astProperty.setSourceFile(sourceFile);
		astProperty.getType().accept(this);
		astProperty.getId().accept(this);
		return null;
	}

	@Override
	public Void visit(ASTBody astBody)
	{
		astBody.setSourceFile(sourceFile);
		for (AST node : astBody.getNodesList()) {
			node.accept(this);
		}
		return null;
	}

	@Override
	public Void visit(ASTAssign astAssign)
	{
		astAssign.setSourceFile(sourceFile);
		astAssign.getIdentifier().accept(this);
		astAssign.getExpr().accept(this);
		return null;
	}

	@Override
	public Void visit(ASTArrayAssign astArrayAssign)
	{
		astArrayAssign.setSourceFile(sourceFile);
		astArrayAssign.getId().accept(this);
		astArrayAssign.getExpression1().accept(this);
		astArrayAssign.getExpression2().accept(this);
		return null;
	}

	@Override
	public Void visit(ASTIfChain astIfChain)
	{
		astIfChain.setSourceFile(sourceFile);
		for (ASTConditionalBranch branch : astIfChain.getBranches()) {
			branch.accept(this);
		}
		if (astIfChain.getElseBody() != null) {
			astIfChain.getElseBody().accept(this);
		}
		return null;
	}

	@Override
	public Void visit(ASTConditionalBranch astConditionalBranch)
	{
		astConditionalBranch.setSourceFile(sourceFile);
		astConditionalBranch.getCondition().accept(this);
		astConditionalBranch.getBody().accept(this);
		return null;
	}

	@Override
	public Void visit(ASTWhile astWhile)
	{
		astWhile.setSourceFile(sourceFile);
		astWhile.getCondition().accept(this);
		astWhile.getBody().accept(this);
		return null;
	}

	@Override
	public Void visit(ASTForeach astForeach)
	{
		astForeach.setSourceFile(sourceFile);
		astForeach.getVariable().accept(this);
		astForeach.getIterable().accept(this);
		astForeach.getBody().accept(this);
		return null;
	}

	@Override
	public Void visit(ASTReturnStatement astReturnStatement)
	{
		astReturnStatement.setSourceFile(sourceFile);
		if (astReturnStatement.getReturnExpr() != null) {
			astReturnStatement.getReturnExpr().accept(this);
		}
		return null;
	}

	@Override
	public Void visit(ASTCallFunctionStat astCallFunctionStat)
	{
		astCallFunctionStat.setSourceFile(sourceFile);
		if (astCallFunctionStat.getInstance() != null) {
			astCallFunctionStat.getInstance().accept(this);
		}
		astCallFunctionStat.getFunctionName().accept(this);
		for (ASTExpression arg : astCallFunctionStat.getArgumentList()) {
			arg.accept(this);
		}
		return null;
	}

	@Override
	public Void visit(ASTCallFunctionExpr astCallFunctionExpr)
	{
		astCallFunctionExpr.setSourceFile(sourceFile);
		if (astCallFunctionExpr.getInstance() != null) {
			astCallFunctionExpr.getInstance().accept(this);
		}
		astCallFunctionExpr.getFunctionName().accept(this);
		for (ASTExpression arg : astCallFunctionExpr.getArgumentList()) {
			arg.accept(this);
		}
		return null;
	}

	@Override
	public Void visit(ASTNewInstance astNewInstance)
	{
		astNewInstance.setSourceFile(sourceFile);
		astNewInstance.getClassName().accept(this);
		for (ASTArgument arg : astNewInstance.getArguments()) {
			arg.accept(this);
		}
		return null;
	}

	@Override
	public Void visit(ASTNewArray astNewArray)
	{
		astNewArray.setSourceFile(sourceFile);
		astNewArray.getArrayLength().accept(this);
		return null;
	}

	@Override
	public Void visit(ASTArrayLiteral astArrayLiteral)
	{
		astArrayLiteral.setSourceFile(sourceFile);
		for (ASTExpression element : astArrayLiteral.getExpressionList()) {
			element.accept(this);
		}
		return null;
	}

	@Override
	public Void visit(ASTLambda astLambda)
	{
		astLambda.setSourceFile(sourceFile);
		for (ASTArgument arg : astLambda.getArgumentList()) {
			arg.accept(this);
		}
		astLambda.getReturnType().accept(this);
		astLambda.getBody().accept(this);
		return null;
	}

	@Override
	public Void visit(ASTIdentifierExpr astIdentifierExpr)
	{
		astIdentifierExpr.setSourceFile(sourceFile);
		return null;
	}

	@Override
	public Void visit(ASTArrayIndexExpr astArrayIndexExpr)
	{
		astArrayIndexExpr.setSourceFile(sourceFile);
		astArrayIndexExpr.getArray().accept(this);
		astArrayIndexExpr.getIndex().accept(this);
		return null;
	}

	@Override
	public Void visit(ASTIntLiteral astIntLiteral)
	{
		astIntLiteral.setSourceFile(sourceFile);
		return null;
	}

	@Override
	public Void visit(ASTStringLiteral astStringLiteral)
	{
		astStringLiteral.setSourceFile(sourceFile);
		return null;
	}

	@Override
	public Void visit(ASTTrue astTrue)
	{
		astTrue.setSourceFile(sourceFile);
		return null;
	}

	@Override
	public Void visit(ASTFalse astFalse)
	{
		astFalse.setSourceFile(sourceFile);
		return null;
	}

	@Override
	public Void visit(ASTIdentifier astIdentifier)
	{
		astIdentifier.setSourceFile(sourceFile);
		return null;
	}

	@Override
	public Void visit(ASTIntType astIntType)
	{
		astIntType.setSourceFile(sourceFile);
		return null;
	}

	@Override
	public Void visit(ASTStringType astStringType)
	{
		astStringType.setSourceFile(sourceFile);
		return null;
	}

	@Override
	public Void visit(ASTBooleanType astBooleanType)
	{
		astBooleanType.setSourceFile(sourceFile);
		return null;
	}

	@Override
	public Void visit(ASTVoidType astVoidType)
	{
		astVoidType.setSourceFile(sourceFile);
		return null;
	}

	@Override
	public Void visit(ASTIdentifierType astIdentifierType)
	{
		astIdentifierType.setSourceFile(sourceFile);
		return null;
	}

	@Override
	public Void visit(ASTIntArrayType astIntArrayType)
	{
		astIntArrayType.setSourceFile(sourceFile);
		return null;
	}

	@Override
	public Void visit(ASTStringArrayType astStringArrayType)
	{
		astStringArrayType.setSourceFile(sourceFile);
		return null;
	}

	@Override
	public Void visit(ASTFunctionType astFunctionType)
	{
		astFunctionType.setSourceFile(sourceFile);
		return null;
	}

	@Override
	public Void visit(ASTParameterizedType astParameterizedType)
	{
		astParameterizedType.setSourceFile(sourceFile);
		astParameterizedType.getBaseType().accept(this);
		return null;
	}

	// Binary operations
	@Override
	public Void visit(ASTPlus astPlus)
	{
		astPlus.setSourceFile(sourceFile);
		astPlus.getLeftSide().accept(this);
		astPlus.getRightSide().accept(this);
		return null;
	}

	@Override
	public Void visit(ASTMinus astMinus)
	{
		astMinus.setSourceFile(sourceFile);
		astMinus.getLeftSide().accept(this);
		astMinus.getRightSide().accept(this);
		return null;
	}

	@Override
	public Void visit(ASTTimes astTimes)
	{
		astTimes.setSourceFile(sourceFile);
		astTimes.getLeftSide().accept(this);
		astTimes.getRightSide().accept(this);
		return null;
	}

	@Override
	public Void visit(ASTDivision astDiv)
	{
		astDiv.setSourceFile(sourceFile);
		astDiv.getLeftSide().accept(this);
		astDiv.getRightSide().accept(this);
		return null;
	}

	@Override
	public Void visit(ASTModulus astModulus)
	{
		astModulus.setSourceFile(sourceFile);
		astModulus.getLeftSide().accept(this);
		astModulus.getRightSide().accept(this);
		return null;
	}

	@Override
	public Void visit(ASTEquals astEquals)
	{
		astEquals.setSourceFile(sourceFile);
		astEquals.getLeftSide().accept(this);
		astEquals.getRightSide().accept(this);
		return null;
	}

	@Override
	public Void visit(ASTNotEquals astNotEquals)
	{
		astNotEquals.setSourceFile(sourceFile);
		astNotEquals.getLeftSide().accept(this);
		astNotEquals.getRightSide().accept(this);
		return null;
	}

	@Override
	public Void visit(ASTLessThan astLessThan)
	{
		astLessThan.setSourceFile(sourceFile);
		astLessThan.getLeftSide().accept(this);
		astLessThan.getRightSide().accept(this);
		return null;
	}

	@Override
	public Void visit(ASTLessThanOrEqual astLessThanOrEqual)
	{
		astLessThanOrEqual.setSourceFile(sourceFile);
		astLessThanOrEqual.getLeftSide().accept(this);
		astLessThanOrEqual.getRightSide().accept(this);
		return null;
	}

	@Override
	public Void visit(ASTGreaterThan astGreaterThan)
	{
		astGreaterThan.setSourceFile(sourceFile);
		astGreaterThan.getLeftSide().accept(this);
		astGreaterThan.getRightSide().accept(this);
		return null;
	}

	@Override
	public Void visit(ASTGreaterThanOrEqual astGreaterThanOrEqual)
	{
		astGreaterThanOrEqual.setSourceFile(sourceFile);
		astGreaterThanOrEqual.getLeftSide().accept(this);
		astGreaterThanOrEqual.getRightSide().accept(this);
		return null;
	}

	@Override
	public Void visit(ASTAnd astAnd)
	{
		astAnd.setSourceFile(sourceFile);
		astAnd.getLeftSide().accept(this);
		astAnd.getRightSide().accept(this);
		return null;
	}

	@Override
	public Void visit(ASTOr astOr)
	{
		astOr.setSourceFile(sourceFile);
		astOr.getLeftSide().accept(this);
		astOr.getRightSide().accept(this);
		return null;
	}
}
