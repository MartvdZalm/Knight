package knight.compiler.ast;

import knight.compiler.ast.controlflow.ASTConditionalBranch;
import knight.compiler.ast.controlflow.ASTForEach;
import knight.compiler.ast.controlflow.ASTIfChain;
import knight.compiler.ast.controlflow.ASTWhile;
import knight.compiler.ast.expressions.ASTAnd;
import knight.compiler.ast.expressions.ASTArrayIndexExpr;
import knight.compiler.ast.expressions.ASTArrayLiteral;
import knight.compiler.ast.expressions.ASTCallFunctionExpr;
import knight.compiler.ast.expressions.ASTDivision;
import knight.compiler.ast.expressions.ASTEquals;
import knight.compiler.ast.expressions.ASTFalse;
import knight.compiler.ast.expressions.ASTFieldAccessExpr;
import knight.compiler.ast.expressions.ASTGreaterThan;
import knight.compiler.ast.expressions.ASTGreaterThanOrEqual;
import knight.compiler.ast.expressions.ASTIdentifierExpr;
import knight.compiler.ast.expressions.ASTIntLiteral;
import knight.compiler.ast.expressions.ASTLambda;
import knight.compiler.ast.expressions.ASTLessThan;
import knight.compiler.ast.expressions.ASTLessThanOrEqual;
import knight.compiler.ast.expressions.ASTMinus;
import knight.compiler.ast.expressions.ASTModulus;
import knight.compiler.ast.expressions.ASTNewArray;
import knight.compiler.ast.expressions.ASTNewInstance;
import knight.compiler.ast.expressions.ASTNotEquals;
import knight.compiler.ast.expressions.ASTOr;
import knight.compiler.ast.expressions.ASTPlus;
import knight.compiler.ast.expressions.ASTStringLiteral;
import knight.compiler.ast.expressions.ASTTimes;
import knight.compiler.ast.expressions.ASTTrue;
import knight.compiler.ast.program.ASTArgument;
import knight.compiler.ast.program.ASTClass;
import knight.compiler.ast.program.ASTFunction;
import knight.compiler.ast.program.ASTIdentifier;
import knight.compiler.ast.program.ASTImport;
import knight.compiler.ast.program.ASTInterface;
import knight.compiler.ast.program.ASTProgram;
import knight.compiler.ast.program.ASTProperty;
import knight.compiler.ast.program.ASTVariable;
import knight.compiler.ast.program.ASTVariableInit;
import knight.compiler.ast.statements.ASTArrayAssign;
import knight.compiler.ast.statements.ASTAssign;
import knight.compiler.ast.statements.ASTBody;
import knight.compiler.ast.statements.ASTCallFunctionStat;
import knight.compiler.ast.statements.ASTFieldAssign;
import knight.compiler.ast.statements.ASTReturnStatement;
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

	public R visit(ASTForEach astForEach);

	public R visit(ASTLambda astLambda);

	public R visit(ASTImport astImport);

	public R visit(ASTParameterizedType astParameterizedType);

	public R visit(ASTInterface astInterface);

	public R visit(ASTFieldAssign astFieldAssign);

	public R visit(ASTFieldAccessExpr astFieldAccessExpr);
}
