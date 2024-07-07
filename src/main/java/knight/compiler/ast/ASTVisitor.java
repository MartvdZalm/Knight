/*
 * MIT License
 * 
 * Copyright (c) 2023, Mart van der Zalm
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package knight.compiler.ast;

import knight.compiler.ast.declarations.*;
import knight.compiler.ast.expressions.*;
import knight.compiler.ast.expressions.operations.*;
import knight.compiler.ast.statements.*;
import knight.compiler.ast.statements.conditionals.*;
import knight.compiler.ast.types.*;

/*
 * File: ASTVisitor.java
 * @author: Mart van der Zalm
 * Date: 2024-01-06
 * Description:
 */
public interface ASTVisitor<R>
{
    public R visit(ASTProgram program);

    public R visit(ASTClass classDecl);

    public R visit(ASTFunction functionDecl);

    public R visit(ASTAnd and);

    public R visit(ASTIdentifierExpr identifierExpr);

    public R visit(ASTBlock block);

    public R visit(ASTIntLiteral intLiteral);

    public R visit(ASTTrue true1);

    public R visit(ASTFalse false1);

    public R visit(ASTArgument argDecl);

    public R visit(ASTIdentifierType identifierType);

    public R visit(ASTIntType intType);

    public R visit(ASTIntArrayType intArrayType);

    public R visit(ASTIfThenElse ifThenElse);

    public R visit(ASTWhile while1);

    public R visit(ASTForLoop forLoop);

    public R visit(ASTAssign assign);

    public R visit(ASTArrayAssign arrayAssign);

    public R visit(ASTArrayIndexExpr indexArray);

    public R visit(ASTDivision division);

    public R visit(ASTTimes times);

    public R visit(ASTPlus plus);

    public R visit(ASTMinus minus);

    public R visit(ASTLessThan lessThan);

    public R visit(ASTEquals equals);

    public R visit(ASTOr or);

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

    public R visit(ASTLessThanOrEqual lessThanOrEqual);

    public R visit(ASTGreaterThan greaterThan);

    public R visit(ASTGreaterThanOrEqual greaterThanOrEqual);

    public R visit(ASTReturnStatement returnStatement);

    public R visit(ASTModulus modulus);

    public R visit(ASTFunctionType functionType);

    public R visit(ASTInlineASM assemblyDecl);

    public R visit(ASTPointerAssign pointerAssign);

    public R visit(ASTThis this1);
}
