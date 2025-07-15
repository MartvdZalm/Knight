package knight.compiler.ast;

import knight.compiler.ast.types.ASTBooleanType;
import knight.compiler.ast.types.ASTFunctionType;
import knight.compiler.ast.types.ASTIdentifierType;
import knight.compiler.ast.types.ASTIntArrayType;
import knight.compiler.ast.types.ASTIntType;
import knight.compiler.ast.types.ASTParameterizedType;
import knight.compiler.ast.types.ASTStringArrayType;
import knight.compiler.ast.types.ASTStringType;
import knight.compiler.ast.types.ASTVoidType;

public interface ASTVisitor<R>
{
	public R visit(ASTProgram astProgram);

	public R visit(ASTClass astClass);

	public R visit(ASTFunction astFunction);

	public R visit(ASTIdentifierExpr astIdentifierExpr);

	public R visit(ASTBody astBody);

	public R visit(ASTIntLiteral astIntLiteral);

	public R visit(ASTTrue astTrue);

	public R visit(ASTFalse astFalse);

	public R visit(ASTIdentifierType astIdentifierType);

	public R visit(ASTIntType astIntType);

	public R visit(ASTIntArrayType astIntArrayType);

	public R visit(ASTStringArrayType astStringArrayType);

	public R visit(ASTIfChain astIfChain);

	public R visit(ASTWhile astWhile);

	public R visit(ASTAssign astAssign);

	public R visit(ASTArrayAssign astArrayAssign);

	public R visit(ASTArrayIndexExpr astIndexArray);

	public R visit(ASTNewArray astNewArray);

	public R visit(ASTCallFunctionExpr astCallFunctionExpr);

	public R visit(ASTIdentifier astIdentifier);

	public R visit(ASTBooleanType astBooleanType);

	public R visit(ASTStringLiteral astStringLiteral);

	public R visit(ASTStringType astStringType);

	public R visit(ASTNewInstance astNewInstance);

	public R visit(ASTVariable astVariable);

	public R visit(ASTVariableInit astVariableInit);

	public R visit(ASTVoidType astVoidType);

	public R visit(ASTCallFunctionStat astCallFunctionStat);

	public R visit(ASTReturnStatement astReturnStatement);

	public R visit(ASTFunctionType astFunctionType);

	public R visit(ASTProperty astProperty);

	public R visit(ASTConditionalBranch astConditionalBranch);

	public R visit(ASTArgument astArgument);

	public R visit(ASTNotEquals astNotEquals);

	public R visit(ASTPlus astPlus);

	public R visit(ASTOr astOr);

	public R visit(ASTAnd astAnd);

	public R visit(ASTEquals astEquals);

	public R visit(ASTLessThan astLessThan);

	public R visit(ASTLessThanOrEqual astLessThanOrEqual);

	public R visit(ASTGreaterThan astGreaterThan);

	public R visit(ASTGreaterThanOrEqual astGreaterThanOrEqual);

	public R visit(ASTMinus astMinus);

	public R visit(ASTTimes astTimes);

	public R visit(ASTDivision astDivision);

	public R visit(ASTModulus astModulus);

	public R visit(ASTArrayLiteral astArrayLiteral);

	public R visit(ASTForeach astForeach);

	public R visit(ASTLambda astLambda);

	public R visit(ASTImport astImport);

	public R visit(ASTParameterizedType astParameterizedType);

	public R visit(ASTInterface astInterface);
}
