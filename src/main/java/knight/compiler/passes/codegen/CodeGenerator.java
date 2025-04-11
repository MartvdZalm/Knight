package knight.compiler.passes.codegen;

import java.io.File;
import java.io.PrintWriter;
import java.util.Set;

import knight.compiler.ast.ASTArgument;
import knight.compiler.ast.ASTArrayAssign;
import knight.compiler.ast.ASTArrayIndexExpr;
import knight.compiler.ast.ASTAssign;
import knight.compiler.ast.ASTBinaryOperation;
import knight.compiler.ast.ASTBody;
import knight.compiler.ast.ASTBooleanType;
import knight.compiler.ast.ASTCallFunctionExpr;
import knight.compiler.ast.ASTCallFunctionStat;
import knight.compiler.ast.ASTClass;
import knight.compiler.ast.ASTConditionalBranch;
import knight.compiler.ast.ASTFalse;
import knight.compiler.ast.ASTFunction;
import knight.compiler.ast.ASTFunctionReturn;
import knight.compiler.ast.ASTFunctionType;
import knight.compiler.ast.ASTIdentifier;
import knight.compiler.ast.ASTIdentifierExpr;
import knight.compiler.ast.ASTIdentifierType;
import knight.compiler.ast.ASTIfChain;
import knight.compiler.ast.ASTIntArrayType;
import knight.compiler.ast.ASTIntLiteral;
import knight.compiler.ast.ASTIntType;
import knight.compiler.ast.ASTNewArray;
import knight.compiler.ast.ASTNewInstance;
import knight.compiler.ast.ASTNotEquals;
import knight.compiler.ast.ASTPlus;
import knight.compiler.ast.ASTPointerAssign;
import knight.compiler.ast.ASTProgram;
import knight.compiler.ast.ASTProperty;
import knight.compiler.ast.ASTReturnStatement;
import knight.compiler.ast.ASTSkip;
import knight.compiler.ast.ASTStringLiteral;
import knight.compiler.ast.ASTStringType;
import knight.compiler.ast.ASTThis;
import knight.compiler.ast.ASTTrue;
import knight.compiler.ast.ASTVariable;
import knight.compiler.ast.ASTVariableInit;
import knight.compiler.ast.ASTVisitor;
import knight.compiler.ast.ASTVoidType;
import knight.compiler.ast.ASTWhile;
import knight.compiler.passes.symbol.model.Binding;
import knight.compiler.passes.symbol.model.SymbolClass;
import knight.compiler.passes.symbol.model.SymbolFunction;

/*
 * File: CodeGenerator.java
 * @author: Mart van der Zalm
 * Date: 2025-04-10
 */
public class CodeGenerator implements ASTVisitor<String>
{
	private SymbolClass currentClass;
	private SymbolFunction currentFunction;

	private final String PATH;
	private final String FILENAME;

	Set<String> builtInFunctions = Set.of("print", "to_upper", "to_lower", "trim", "split", "read", "now", "random",
			"to_int", "to_string", "read_line");

	StringBuilder code = new StringBuilder();

	public CodeGenerator(String progPath, String filename)
	{
		File f = new File(filename);
		String name = f.getName();

		FILENAME = name.substring(0, name.lastIndexOf("."));
		PATH = progPath;
	}

	@Override
	public String visit(ASTAssign astAssign)
	{
		StringBuilder sb = new StringBuilder();

		sb.append(astAssign.getIdentifier().accept(this));
		sb.append("=");
		sb.append(astAssign.getExpr().accept(this));

		return sb.append(";\n").toString();
	}

	@Override
	public String visit(ASTBody body)
	{
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < body.getVariableListSize(); i++) {
			sb.append(body.getVariableAt(i).accept(this));
		}

		for (int i = 0; i < body.getStatementListSize(); i++) {
			sb.append(body.getStatementAt(i).accept(this));
		}

		return sb.toString();
	}

	@Override
	public String visit(ASTSkip skip)
	{
		return null;
	}

	@Override
	public String visit(ASTWhile w)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("while (");
		sb.append(w.getExpr().accept(this));
		sb.append(") {\n");
		sb.append(w.getBody().accept(this));
		sb.append("}");

		return sb.toString();
	}

	@Override
	public String visit(ASTIntLiteral intLiteral)
	{
		return String.valueOf(intLiteral.getValue());
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
		StringBuilder sb = new StringBuilder();
		String funcName = callFunctionExpr.getFunctionName().getId();

		if (builtInFunctions.contains(funcName)) {
			sb.append("knight::" + funcName + "(");
			for (int i = 0; i < callFunctionExpr.getArgumentListSize(); i++) {
				sb.append(callFunctionExpr.getArgumentAt(i).accept(this));

				if (i != callFunctionExpr.getArgumentListSize() - 1) {
					sb.append(",");
				}
			}

			sb.append(") \n");
			return sb.toString();
		}

		return null;
	}

	@Override
	public String visit(ASTCallFunctionStat callFunctionStat)
	{
		StringBuilder sb = new StringBuilder();
		String funcName = callFunctionStat.getFunctionName().getId();

		if (builtInFunctions.contains(funcName)) {
			sb.append("knight::" + funcName + "(");
			for (int i = 0; i < callFunctionStat.getArgumentListSize(); i++) {
				sb.append(callFunctionStat.getArgumentAt(i).accept(this));
				System.out.println(callFunctionStat.getArgumentAt(i).getType());

				if (callFunctionStat.getArgumentAt(i).getType() instanceof ASTStringType) {
					sb.append("+");
				}
			}

			sb.append("); \n");
			return sb.toString();
		}

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
//
//	@Override
//	public String visit(ASTArgument argDecl)
//	{
//		String type = argDecl.getType().accept(this);
//		String id = argDecl.getId().accept(this);
//
//		return type + " " + id;
//	}

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
		return type + " " + id + "; \n";
	}

	@Override
	public String visit(ASTVariableInit variableInit)
	{
		String type = variableInit.getType().accept(this);
		String id = variableInit.getId().accept(this);
		return type + " " + id + " = " + variableInit.getExpr().accept(this) + "; \n";
	}

	@Override
	public String visit(ASTFunction functionVoid)
	{
		return null;
	}

	@Override
	public String visit(ASTFunctionReturn functionReturn)
	{
		code.append(functionReturn.getReturnType() + " " + functionReturn.getFunctionName() + "(");

		for (int i = 0; i < functionReturn.getArgumentListSize(); i++) {
			code.append(functionReturn.getArgumentAt(i).accept(this));

			if (i != functionReturn.getArgumentListSize() - 1) {
				code.append(", ");
			}
		}

		code.append(") { \n");

		code.append(functionReturn.getBody().accept(this));

		code.append("\treturn " + functionReturn.getReturnExpr().accept(this) + ";\n");

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
		code.append("class " + classDecl.getClassName().accept(this) + " {\n");

		for (int i = 0; i < classDecl.getPropertyListSize(); i++) {
			code.append(classDecl.getPropertyDeclAt(i).accept(this) + "\n");
		}

		for (int i = 0; i < classDecl.getFunctionListSize(); i++) {
			code.append(classDecl.getFunctionDeclAt(i).accept(this) + "\n");
		}

		code.append("};\n");

		return null;
	}

//	@Override
//	public String visit(ASTInlineASM inlineASM)
//	{
//		// for (int i = 0; i < inlineASM.getLinesSize(); i++) {
//		// 	String line = inlineASM.getLineAt(i);
//
//		// 	if (line.startsWith("\"") && line.endsWith("\"")) {
//        //         line = line.substring(1, line.length() - 1);
//        //     }
//
//        //     text.append(line).append("\n");
//		// }
//		return null;
//	}

	@Override
	public String visit(ASTProgram program)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("#include <knight/knight_std.h>\n");

		for (int i = 0; i < program.getClassListSize(); i++) {
			program.getClassDeclAt(i).accept(this);
		}

		for (int i = 0; i < program.getFunctionListSize(); i++) {
			program.getFunctionDeclAt(i).accept(this);
		}

		write(sb.append(code).toString());

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
		// return ((SymbolVariable) b).getLvIndex();
		// }
		return -1;
	}

	private int setLocalArgIndex(Binding b)
	{
		// if (b != null && b instanceof SymbolVariable) {
		// ((SymbolVariable) b).setLvIndex(++localArg);
		// return localArg;
		// }
		return -1;
	}

	private int getLocalVarIndex(Binding b)
	{
		// if (b != null && b instanceof SymbolVariable) {
		// return ((SymbolVariable) b).getLvIndex();
		// }
		return -1;
	}

	private int setLocalVarIndex(Binding b)
	{
		// if (b != null && b instanceof SymbolVariable) {
		// ((SymbolVariable) b).setLvIndex(++localVar);
		// return localVar;
		// }
		return -1;
	}

	@Override
	public String visit(ASTReturnStatement returnStatement)
	{
		return null;
	}

	@Override
	public String visit(ASTFunctionType functionType)
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
		String type = property.getType().accept(this);
		String id = property.getId().accept(this);
		return type + " " + id + ";";
	}

	@Override
	public String visit(ASTIfChain ifChain)
	{
		StringBuilder sb = new StringBuilder();

		// Flag to check if we need to add "else if" or just "if"
		boolean firstBranch = true;

		for (ASTConditionalBranch branch : ifChain.getBranches()) {
			if (!firstBranch) {
				sb.append(" else ");
			}

			// Check if this branch is an "if" or an "else if" and print accordingly
			sb.append("if (");
			sb.append(branch.getCondition().accept(this)); // Print the condition for this branch
			sb.append(") ");

			// Print the body of the branch (in curly braces)
			sb.append("{\n");
			sb.append(branch.getBody().accept(this)); // Print the body of the if/else if
			sb.append("\n}\n");

			firstBranch = false; // After the first branch, subsequent branches are "else if"
		}

		// Handle the "else" part, if there is one
		if (ifChain.getElseBody() != null) {
			sb.append("else ");
			sb.append("{\n");
			sb.append(ifChain.getElseBody().accept(this)); // Print the else body
			sb.append("\n}\n");
		}

		return sb.toString();
	}

	@Override
	public String visit(ASTConditionalBranch astConditionalBranch)
	{
		// In C++, we typically print the body as a block of statements.
		StringBuilder sb = new StringBuilder();

		// The condition part of the if/else if (already enclosed in parentheses)
		sb.append(astConditionalBranch.getCondition().accept(this));

		// For now, assume the body is a list of statements enclosed in curly braces.
		sb.append(" {\n");
		sb.append(astConditionalBranch.getBody().accept(this)); // Body of the if or else if
		sb.append("\n}");

		return sb.toString();
	}

	@Override
	public String visit(ASTBinaryOperation astBinaryOperation)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visit(ASTArgument astArgument)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visit(ASTNotEquals astNotEquals)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(astNotEquals.getLeftSide().accept(this));
		sb.append("!=");
		sb.append(astNotEquals.getRightSide().accept(this));
		return sb.toString();
	}

	@Override
	public String visit(ASTPlus astPlus)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(astPlus.getLeftSide().accept(this));
		sb.append("+");
		sb.append(astPlus.getRightSide().accept(this));
		return sb.toString();
	}
}
