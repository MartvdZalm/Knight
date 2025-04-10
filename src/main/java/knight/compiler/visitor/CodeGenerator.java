package knight.compiler.visitor;

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
import knight.compiler.semantics.Binding;
import knight.compiler.symbol.SymbolClass;
import knight.compiler.symbol.SymbolFunction;

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
			"to_int", "to_string");

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
		// text.append("movq %rax, " + assign.getId() + "\n");
		// } else {
		// text.append("movq %rax, -" + (lvIndex * 8) + "(%rbp)\n");
		// }

		// text.append("movq $0, %rax\n");

		return null;
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

//	@Override
//	public String visit(ASTIfThenElse ifThenElse)
//	{
//		// ifThenElse.getExpr().accept(this);
//		// ifThenElse.getThen().accept(this);
//		// text.append("jmp end\n");
//		// text.append("else:\n");
//		// ifThenElse.getElze().accept(this);
//		// text.append("end:\n");
//
//		return null;
//	}

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

			sb.append(")");
			return sb.toString();
		}

		return funcName + "(" + callFunctionExpr.getArgumentList().get(0).accept(this) + ")";
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
			}

			sb.append(");");
			return sb.toString();
		}

		return funcName + "(" + callFunctionStat.getArgumentList().get(0).accept(this) + ");";
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visit(ASTBinaryOperation astBinaryOperation)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visit(ASTConditionalBranch astConditionalBranch)
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visit(ASTPlus astPlus)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
