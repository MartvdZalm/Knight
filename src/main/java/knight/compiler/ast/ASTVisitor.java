package knight.compiler.ast;

/*
 * File: ASTVisitor.java
 * @author: Mart van der Zalm
 * Date: 2025-04-10
 */
public interface ASTVisitor<R>
{
	public R visit(ASTProgram program);

	public R visit(ASTClass classDecl);

	public R visit(ASTFunction functionDecl);

	public R visit(ASTIdentifierExpr identifierExpr);

	public R visit(ASTBody body);

	public R visit(ASTIntLiteral intLiteral);

	public R visit(ASTTrue true1);

	public R visit(ASTFalse false1);

	public R visit(ASTIdentifierType identifierType);

	public R visit(ASTIntType intType);

	public R visit(ASTIntArrayType intArrayType);

	public R visit(ASTIfChain ifChain);

	public R visit(ASTWhile while1);

	public R visit(ASTAssign assign);

	public R visit(ASTArrayAssign arrayAssign);

	public R visit(ASTArrayIndexExpr indexArray);

	public R visit(ASTNewArray newArray);

	public R visit(ASTCallFunctionExpr callFunc);

	public R visit(ASTIdentifier identifier);

	public R visit(ASTBooleanType booleanType);

	public R visit(ASTStringLiteral stringLiteral);

	public R visit(ASTStringType stringType);

	public R visit(ASTNewInstance newInstance);

	public R visit(ASTVariable variableDecl);

	public R visit(ASTVariableInit variableDeclInit);

	public R visit(ASTVoidType voidType);

	public R visit(ASTSkip skip);

	public R visit(ASTCallFunctionStat callFunction);

	public R visit(ASTFunctionReturn functionDeclReturn);

	public R visit(ASTReturnStatement returnStatement);

	public R visit(ASTFunctionType functionType);

	public R visit(ASTPointerAssign pointerAssign);

	public R visit(ASTThis this1);

	public R visit(ASTProperty property);

	public R visit(ASTBinaryOperation astBinaryOperation);

	public R visit(ASTConditionalBranch astConditionalBranch);

	public R visit(ASTArgument astArgument);

	public R visit(ASTNotEquals astNotEquals);

	public R visit(ASTPlus astPlus);
}
