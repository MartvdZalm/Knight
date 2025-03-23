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

package knight.compiler.visitor;

import java.io.File;
import java.io.PrintWriter;
import java.io.*;

import knight.compiler.ast.declarations.*;
import knight.compiler.ast.expressions.*;
import knight.compiler.ast.expressions.operations.*;
import knight.compiler.ast.statements.*;
import knight.compiler.ast.statements.conditionals.*;
import knight.compiler.ast.types.*;
import knight.compiler.ast.*;

import knight.compiler.semantics.*;
import knight.compiler.symbol.SymbolClass;
import knight.compiler.symbol.SymbolFunction;
import knight.compiler.symbol.SymbolProgram;
import knight.compiler.symbol.SymbolVariable;

/*
 * File: CodeGenerator.java
 * @author: Mart van der Zalm
 * Date: 2024-01-06
 * Description:
 */
public class CodeGenerator implements ASTVisitor<String>
{
	private SymbolClass currentClass;
	private SymbolFunction currentFunction;

	private final String PATH;
	private final String FILENAME;
	
	StringBuilder code = new StringBuilder();

	public CodeGenerator(String progPath, String filename)
	{
		File f = new File(filename);
		String name = f.getName();

		FILENAME = name.substring(0, name.lastIndexOf("."));
		PATH = progPath;
	}

	@Override
	public String visit(ASTAssign assign)
	{
		// assign.getExpr().accept(this);

		// Binding b = assign.getId().getB();
		// int lvIndex = getLocalVarIndex(b);

		// if (lvIndex == -1) {
		// 	text.append("movq %rax, " + assign.getId() + "\n");
		// } else {
		// 	text.append("movq %rax, -" + (lvIndex * 8) + "(%rbp)\n");
		// }

		// text.append("movq $0, %rax\n");

		return null;
	}

	@Override
	public String visit(ASTBlock block)
	{
		// for (int i = 0; i < block.getStatementListSize(); i++) {
		// 	block.getStatementAt(i).accept(this);
		// }

		return null;
	}

	@Override
	public String visit(ASTIfThenElse ifThenElse)
	{
		// ifThenElse.getExpr().accept(this);
		// ifThenElse.getThen().accept(this);
		// text.append("jmp end\n");
		// text.append("else:\n");
		// ifThenElse.getElze().accept(this);
		// text.append("end:\n");

		return null;
	}

	@Override
	public String visit(ASTSkip skip)
	{
		return null;
	}

	@Override
	public String visit(ASTWhile w)
	{
		// text.append("while: \n");
		// w.getBody().accept(this);
		// w.getExpr().accept(this);

		return null;
	}

	@Override
	public String visit(ASTIntLiteral intLiteral)
	{
		return String.valueOf(intLiteral.getValue());
	}

	@Override
	public String visit(ASTPlus plus)
	{
		return plus.getLhs().accept(this) + " + " + plus.getRhs().accept(this);
	}

	@Override
	public String visit(ASTMinus minus)
	{	
		return minus.getLhs().accept(this) + " - " + minus.getRhs().accept(this);
	}

	@Override
	public String visit(ASTTimes times)
	{
		return times.getLhs().accept(this) + " * " + times.getRhs().accept(this);
	}

	@Override
	public String visit(ASTDivision division)
	{
		return division.getLhs().accept(this) + " / " + division.getRhs().accept(this);
	}

	@Override
	public String visit(ASTEquals equals)
	{
		return equals.getLhs().accept(this) + " == " + equals.getRhs().accept(this);
	}

	@Override
	public String visit(ASTLessThan lessThan)
	{
		return lessThan.getLhs().accept(this) + " < " + lessThan.getRhs().accept(this);
	}

	@Override
	public String visit(ASTLessThanOrEqual lessThanOrEqual)
	{
		return lessThanOrEqual.getLhs().accept(this) + " <= " + lessThanOrEqual.getRhs().accept(this);
	}

	@Override
	public String visit(ASTGreaterThan greaterThan)
	{
		return greaterThan.getLhs().accept(this) + " > " + greaterThan.getRhs().accept(this);
	}

	@Override
	public String visit(ASTGreaterThanOrEqual greaterThanOrEqual)
	{
		return greaterThanOrEqual.getLhs().accept(this) + " >= " + greaterThanOrEqual.getRhs().accept(this);
	}

	@Override
	public String visit(ASTAnd and)
	{
		return and.getLhs().accept(this) + " && " + and.getRhs().accept(this);
	}

	@Override
	public String visit(ASTOr or)
	{
		return or.getLhs().accept(this) + " || " + or.getRhs().accept(this);
	}

	@Override
	public String visit(ASTTrue true1)
	{
		return "true";
	}

	@Override
	public String visit(ASTFalse false1)
	{
		return "false";
	}

	@Override
	public String visit(ASTIdentifierExpr id)
	{
		return id.getId();
	}

	@Override
	public String visit(ASTNewArray na)
	{
		return "";
	}

	@Override
	public String visit(ASTNewInstance ni)
	{
		return null;
	}

	@Override
	public String visit(ASTCallFunctionExpr callFunctionExpr)
	{
		return null;
	}

	@Override
	public String visit(ASTCallFunctionStat callFunctionStat)
	{
		return null;
	}

	@Override
	public String visit(ASTIntType intType)
	{
		return "int";
	}

	@Override
	public String visit(ASTStringType stringType)
	{	
		return "std::string";
	}

	@Override
	public String visit(ASTVoidType voidType)
	{
		return "void";
	}

	@Override
	public String visit(ASTBooleanType booleanType)
	{
		return "bool";
	}

	@Override
	public String visit(ASTIntArrayType intArrayType)
	{
		return "int[]";
	}

	@Override
	public String visit(ASTIdentifierType id)
	{
		return id.getId();
	}

	@Override
	public String visit(ASTArgument argDecl)
	{
		String type = argDecl.getType().accept(this);
		String id = argDecl.getId().accept(this);

		return type + " " + id;
	}

	@Override
	public String visit(ASTIdentifier identifier)
	{
		return identifier.getId();
	}

	@Override
	public String visit(ASTArrayIndexExpr ia)
	{
		return null;
	}

	@Override
	public String visit(ASTArrayAssign aa)
	{
		return null;
	}

	@Override
	public String visit(ASTStringLiteral stringLiteral)
	{
		return stringLiteral.getValue();
	}

	@Override
	public String visit(ASTVariable variable)
	{
		String type = variable.getType().accept(this);
		String id = variable.getId().accept(this);
		return type + " " + id + ";";
	}

	@Override
	public String visit(ASTVariableInit variableInit)
	{
		String type = variableInit.getType().accept(this);
		String id = variableInit.getId().accept(this);
		return type + " " + id + " = " + variableInit.getExpr().accept(this) + ";";
	}

	@Override
	public String visit(ASTFunction functionVoid)
	{
		return null;
	}

	@Override
	public String visit(ASTFunctionReturn functionReturn)
	{
	    code.append(functionReturn.getReturnType() + " " + functionReturn.getId() + "(");
	    
	    for (int i = 0; i < functionReturn.getArgumentListSize(); i++) {
	    	code.append(functionReturn.getArgumentDeclAt(i).accept(this));

	    	if (i != functionReturn.getArgumentListSize() - 1) {
	    		code.append(", ");
	    	}
	    }
	    
	    code.append(") { \n");

	    for (int i = 0; i < functionReturn.getVariableListSize(); i++) {
	        code.append("\t" + functionReturn.getVariableDeclAt(i).accept(this) + "\n");
	    }

	    for (int i = 0; i < functionReturn.getStatementListSize(); i++) {
	        code.append(functionReturn.getStatementDeclAt(i).accept(this) + "\n");
	    }

	    code.append("\treturn " + functionReturn.getReturnExpr().accept(this) + "\n");

	   	code.append("} \n");

	    return null;
	}

	private String getLocalVariableReference(int index)
	{
	    return ((index + 1) * 8) + "(%rbp)";
	}

	@Override
	public String visit(ASTClass classDecl)
	{
		return null;
	}

	@Override
	public String visit(ASTInlineASM inlineASM)
	{
		// for (int i = 0; i < inlineASM.getLinesSize(); i++) {
		// 	String line = inlineASM.getLineAt(i);

		// 	if (line.startsWith("\"") && line.endsWith("\"")) {
        //         line = line.substring(1, line.length() - 1);
        //     }

        //     text.append(line).append("\n");
		// }
		return null;
	}

	@Override
	public String visit(ASTProgram program)
	{

		// for (int i = 0; i < program.getVariableListSize(); i++) {
		// 	program.getVariableDeclAt(i).accept(this);
		// }

		// for (int i = 0; i < program.getInlineASMListSize(); i++) {
		// 	program.getInlineASMDeclAt(i).accept(this);
		// }

		for (int i = 0; i < program.getFunctionListSize(); i++) {
			program.getFunctionDeclAt(i).accept(this);
		}

		write(code.toString());

		return null;
	}

	private File write(String code)
	{
		try {
			File f = new File(PATH + FILENAME + ".cpp");
			PrintWriter writer = new PrintWriter(f, "UTF-8");
			writer.println(code);
			writer.close();
			return f;
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

		return null;
	}

	private int getLocalArgIndex(Binding b)
	{
		// if (b != null && b instanceof SymbolVariable) {
		// 	return ((SymbolVariable) b).getLvIndex(); 
		// }
		return -1;
	}

	private int setLocalArgIndex(Binding b)
	{
		// if (b != null && b instanceof SymbolVariable) {
		// 	((SymbolVariable) b).setLvIndex(++localArg);
		// 	return localArg;
		// }
		return -1;
	}
	
	private int getLocalVarIndex(Binding b)
	{
		// if (b != null && b instanceof SymbolVariable) {
		// 	return ((SymbolVariable) b).getLvIndex();
		// }
		return -1;
	}

	private int setLocalVarIndex(Binding b)
	{
		// if (b != null && b instanceof SymbolVariable) {
		// 	((SymbolVariable) b).setLvIndex(++localVar);
		// 	return localVar;
		// }
		return -1;
	}

	@Override
	public String visit(ASTReturnStatement returnStatement)
	{
		return null;
	}

	@Override
	public String visit(ASTModulus modulus)
	{
		return null;
	}

	@Override
	public String visit(ASTFunctionType functionType)
	{
		return null;
	}

	@Override
	public String visit(ASTForLoop forLoop)
	{
		return null;
	}

	@Override
	public String visit(ASTPointerAssign pointerAssign)
	{
		return null;
	}

	@Override
	public String visit(ASTThis astThis)
	{
		return null;
	}

	@Override
	public String visit(ASTProperty property)
	{
		return null;
	}
}
