package src.ast;

public interface Visitor<R>
{
    public R visit(Program program);

    public R visit(Class classDecl);

    public R visit(Function functionDecl);

    public R visit(And and);

    public R visit(IdentifierExpr identifierExpr);

    public R visit(Block block);

    public R visit(IntLiteral intLiteral);

    public R visit(True true1);

    public R visit(False false1);

    public R visit(Argument argDecl);

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

    public R visit(StringLiteral stringLiteral);

    public R visit(StringType stringType);

    public R visit(NewInstance newInstance);

    public R visit(Variable variableDecl);

    public R visit(VariableInit variableDeclInit);

    public R visit(Include includeDecl);

    public R visit(VoidType voidType);

    public R visit(Skip skip);

    public R visit(CallFunctionStat callFunction);

    public R visit(FunctionReturn functionDeclReturn);

    public R visit(LessThanOrEqual lessThanOrEqual);

    public R visit(GreaterThan greaterThan);

    public R visit(GreaterThanOrEqual greaterThanOrEqual);

    public R visit(ReturnStatement returnStatement);

    public R visit(Increment increment);

    public R visit(Modulus modulus);

    public R visit(FunctionType functionType);

    public R visit(Enumeration enumDecl);

    public R visit(Interface interDecl);
}
