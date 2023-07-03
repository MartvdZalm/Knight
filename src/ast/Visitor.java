package src.ast;

public interface Visitor<R>
{
    public R visit(And and);

    public R visit(IdentifierExpr identifierExpr);

    public R visit(Block block);

    public R visit(IntLiteral intLiteral);

    public R visit(True true1);

    public R visit(False false1);

    public R visit(ArgDecl argDecl);

    public R visit(VarDecl varDecl);

    public R visit(Program program);

    public R visit(IdentifierType identifierType);

    public R visit(IntType intType);

    public R visit(IntArrayType intArrayType);

    public R visit(IfThenElse ifThenElse);

    public R visit(While while1);

    public R visit(Assign assign);

    public R visit(ArrayAssign arrayAssign);

    public R visit(ArrayIndexExpr indexArray);

    public R visit(Division division);

    public R visit(Times times);

    public R visit(Plus plus);

    public R visit(Minus minus);

    public R visit(LessThan lessThan);

    public R visit(Equals equals);

    public R visit(Or or);

    public R visit(NewArray newArray);

    public R visit(CallFunctionExpr callFunc);

    public R visit(Identifier identifier);

    public R visit(BooleanType booleanType);

    public R visit(ClassDeclExtends classDeclExtends);

    public R visit(ClassDeclSimple classDeclSimple);

    public R visit(StringLiteral stringLiteral);

    public R visit(StringType stringType);

    public R visit(NewInstance newInstance);

    public R visit(VarDeclInit varDeclInit);

    public R visit(Include include);

    public R visit(VoidType voidType);

    public R visit(Skip skip);

    public R visit(CallFunctionStat callFunction);

    public R visit(ForLoop forLoop);

    public R visit(FunctionExprReturn functionBody);

    public R visit(FunctionExprVoid functionExprVoid);

}
