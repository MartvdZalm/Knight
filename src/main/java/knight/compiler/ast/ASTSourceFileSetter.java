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
		for (ASTImport astImport : astProgram.getImports()) {
			astImport.accept(this);
		}
		for (AST node : astProgram.getNodes()) {
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
		astClass.getIdentifier().accept(this);
		if (astClass.getExtendsClass() != null) {
			astClass.getExtendsClass().accept(this);
		}
		for (ASTIdentifier astInterface : astClass.getImplementsInterfaces()) {
			astInterface.accept(this);
		}
		for (ASTProperty astProperty : astClass.getProperties()) {
			astProperty.accept(this);
		}
		for (ASTFunction astFunction : astClass.getFunctions()) {
			astFunction.accept(this);
		}
		return null;
	}

	@Override
	public Void visit(ASTInterface astInterface)
	{
		astInterface.setSourceFile(sourceFile);
		astInterface.getIdentifier().accept(this);
		for (ASTFunction astFunction : astInterface.getFunctions()) {
			astFunction.accept(this);
		}
		return null;
	}

	@Override
	public Void visit(ASTFunction astFunction)
	{
		astFunction.setSourceFile(sourceFile);
		astFunction.getIdentifier().accept(this);
		astFunction.getReturnType().accept(this);
		for (ASTArgument astArgument : astFunction.getArguments()) {
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
		astVariable.getIdentifier().accept(this);
		return null;
	}

	@Override
	public Void visit(ASTVariableInit astVariableInit)
	{
		astVariableInit.setSourceFile(sourceFile);
		astVariableInit.getType().accept(this);
		astVariableInit.getIdentifier().accept(this);
		astVariableInit.getExpression().accept(this);
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
		astProperty.getIdentifier().accept(this);
		return null;
	}

	@Override
	public Void visit(ASTBody astBody)
	{
		astBody.setSourceFile(sourceFile);
		for (AST node : astBody.getNodes()) {
			node.accept(this);
		}
		return null;
	}

	@Override
	public Void visit(ASTAssign astAssign)
	{
		astAssign.setSourceFile(sourceFile);
		astAssign.getIdentifier().accept(this);
		astAssign.getExpression().accept(this);
		return null;
	}

	@Override
	public Void visit(ASTArrayAssign astArrayAssign)
	{
		astArrayAssign.setSourceFile(sourceFile);
		astArrayAssign.getIdentifier().accept(this);
		astArrayAssign.getArray().accept(this);
		astArrayAssign.getValue().accept(this);
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
	public Void visit(ASTForEach astForEach)
	{
		astForEach.setSourceFile(sourceFile);
		astForEach.getVariable().accept(this);
		astForEach.getIterable().accept(this);
		astForEach.getBody().accept(this);
		return null;
	}

	@Override
	public Void visit(ASTReturnStatement astReturnStatement)
	{
		astReturnStatement.setSourceFile(sourceFile);
		if (astReturnStatement.getExpression() != null) {
			astReturnStatement.getExpression().accept(this);
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
		for (ASTExpression arg : astCallFunctionStat.getArguments()) {
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
		for (ASTExpression arg : astCallFunctionExpr.getArguments()) {
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
		for (ASTExpression element : astArrayLiteral.getExpressions()) {
			element.accept(this);
		}
		return null;
	}

	@Override
	public Void visit(ASTLambda astLambda)
	{
		astLambda.setSourceFile(sourceFile);
		for (ASTArgument arg : astLambda.getArguments()) {
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
		astPlus.getLeft().accept(this);
		astPlus.getRight().accept(this);
		return null;
	}

	@Override
	public Void visit(ASTMinus astMinus)
	{
		astMinus.setSourceFile(sourceFile);
		astMinus.getLeft().accept(this);
		astMinus.getRight().accept(this);
		return null;
	}

	@Override
	public Void visit(ASTTimes astTimes)
	{
		astTimes.setSourceFile(sourceFile);
		astTimes.getLeft().accept(this);
		astTimes.getRight().accept(this);
		return null;
	}

	@Override
	public Void visit(ASTDivision astDiv)
	{
		astDiv.setSourceFile(sourceFile);
		astDiv.getLeft().accept(this);
		astDiv.getRight().accept(this);
		return null;
	}

	@Override
	public Void visit(ASTModulus astModulus)
	{
		astModulus.setSourceFile(sourceFile);
		astModulus.getLeft().accept(this);
		astModulus.getRight().accept(this);
		return null;
	}

	@Override
	public Void visit(ASTEquals astEquals)
	{
		astEquals.setSourceFile(sourceFile);
		astEquals.getLeft().accept(this);
		astEquals.getRight().accept(this);
		return null;
	}

	@Override
	public Void visit(ASTNotEquals astNotEquals)
	{
		astNotEquals.setSourceFile(sourceFile);
		astNotEquals.getLeft().accept(this);
		astNotEquals.getRight().accept(this);
		return null;
	}

	@Override
	public Void visit(ASTLessThan astLessThan)
	{
		astLessThan.setSourceFile(sourceFile);
		astLessThan.getLeft().accept(this);
		astLessThan.getRight().accept(this);
		return null;
	}

	@Override
	public Void visit(ASTLessThanOrEqual astLessThanOrEqual)
	{
		astLessThanOrEqual.setSourceFile(sourceFile);
		astLessThanOrEqual.getLeft().accept(this);
		astLessThanOrEqual.getRight().accept(this);
		return null;
	}

	@Override
	public Void visit(ASTGreaterThan astGreaterThan)
	{
		astGreaterThan.setSourceFile(sourceFile);
		astGreaterThan.getLeft().accept(this);
		astGreaterThan.getRight().accept(this);
		return null;
	}

	@Override
	public Void visit(ASTGreaterThanOrEqual astGreaterThanOrEqual)
	{
		astGreaterThanOrEqual.setSourceFile(sourceFile);
		astGreaterThanOrEqual.getLeft().accept(this);
		astGreaterThanOrEqual.getRight().accept(this);
		return null;
	}

	@Override
	public Void visit(ASTAnd astAnd)
	{
		astAnd.setSourceFile(sourceFile);
		astAnd.getLeft().accept(this);
		astAnd.getRight().accept(this);
		return null;
	}

	@Override
	public Void visit(ASTOr astOr)
	{
		astOr.setSourceFile(sourceFile);
		astOr.getLeft().accept(this);
		astOr.getRight().accept(this);
		return null;
	}
}
