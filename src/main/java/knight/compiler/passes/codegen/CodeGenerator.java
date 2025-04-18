package knight.compiler.passes.codegen;

import java.io.File;
import java.io.PrintWriter;
import java.util.Set;

import knight.compiler.ast.ASTAnd;
import knight.compiler.ast.ASTArgument;
import knight.compiler.ast.ASTArrayAssign;
import knight.compiler.ast.ASTArrayIndexExpr;
import knight.compiler.ast.ASTAssign;
import knight.compiler.ast.ASTBody;
import knight.compiler.ast.ASTBooleanType;
import knight.compiler.ast.ASTCallFunctionExpr;
import knight.compiler.ast.ASTCallFunctionStat;
import knight.compiler.ast.ASTClass;
import knight.compiler.ast.ASTConditionalBranch;
import knight.compiler.ast.ASTDivision;
import knight.compiler.ast.ASTEquals;
import knight.compiler.ast.ASTExpression;
import knight.compiler.ast.ASTFalse;
import knight.compiler.ast.ASTFunction;
import knight.compiler.ast.ASTFunctionReturn;
import knight.compiler.ast.ASTFunctionType;
import knight.compiler.ast.ASTGreaterThan;
import knight.compiler.ast.ASTGreaterThanOrEqual;
import knight.compiler.ast.ASTIdentifier;
import knight.compiler.ast.ASTIdentifierExpr;
import knight.compiler.ast.ASTIdentifierType;
import knight.compiler.ast.ASTIfChain;
import knight.compiler.ast.ASTIntArrayType;
import knight.compiler.ast.ASTIntLiteral;
import knight.compiler.ast.ASTIntType;
import knight.compiler.ast.ASTLessThan;
import knight.compiler.ast.ASTLessThanOrEqual;
import knight.compiler.ast.ASTMinus;
import knight.compiler.ast.ASTModulus;
import knight.compiler.ast.ASTNewArray;
import knight.compiler.ast.ASTNewInstance;
import knight.compiler.ast.ASTNotEquals;
import knight.compiler.ast.ASTOr;
import knight.compiler.ast.ASTPlus;
import knight.compiler.ast.ASTProgram;
import knight.compiler.ast.ASTProperty;
import knight.compiler.ast.ASTReturnStatement;
import knight.compiler.ast.ASTStringLiteral;
import knight.compiler.ast.ASTStringType;
import knight.compiler.ast.ASTTimes;
import knight.compiler.ast.ASTTrue;
import knight.compiler.ast.ASTVariable;
import knight.compiler.ast.ASTVariableInit;
import knight.compiler.ast.ASTVisitor;
import knight.compiler.ast.ASTVoidType;
import knight.compiler.ast.ASTWhile;
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
		File file = new File(filename);
		String name = file.getName();

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
	public String visit(ASTBody astBody)
	{
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < astBody.getVariableListSize(); i++) {
			sb.append(astBody.getVariableAt(i).accept(this));
		}

		for (int i = 0; i < astBody.getStatementListSize(); i++) {
			sb.append(astBody.getStatementAt(i).accept(this));
		}

		return sb.toString();
	}

	@Override
	public String visit(ASTWhile astWhile)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("while (");
		sb.append(astWhile.getCondition().accept(this));
		sb.append(") {\n");
		sb.append(astWhile.getBody().accept(this));
		sb.append("}");

		return sb.toString();
	}

	@Override
	public String visit(ASTIntLiteral astIntLiteral)
	{
		return String.valueOf(astIntLiteral.getValue());
	}

	@Override
	public String visit(ASTTrue astTrue)
	{
		return "true";
	}

	@Override
	public String visit(ASTFalse astFalse)
	{
		return "false";
	}

	@Override
	public String visit(ASTIdentifierExpr astIdentifierExpr)
	{
		return astIdentifierExpr.getId();
	}

	@Override
	public String visit(ASTNewArray astNewArray)
	{
		return "";
	}

	@Override
	public String visit(ASTNewInstance astNewInstance)
	{
		return null;
	}

	@Override
	public String visit(ASTCallFunctionExpr astCallFunctionExpr)
	{
		StringBuilder sb = new StringBuilder();
		String funcName = astCallFunctionExpr.getFunctionName().getId();

		if (builtInFunctions.contains(funcName)) {
			sb.append("knight::" + funcName + "(");
			for (int i = 0; i < astCallFunctionExpr.getArgumentListSize(); i++) {
				ASTExpression astArgument = astCallFunctionExpr.getArgumentAt(i);
				sb.append(astArgument.accept(this));

				if (i < astCallFunctionExpr.getArgumentListSize() - 1) {
					boolean currentIsString = astArgument.getType() instanceof ASTStringType;
					boolean nextIsString = astCallFunctionExpr.getArgumentAt(i + 1).getType() instanceof ASTStringType;

					if (currentIsString && nextIsString) {
						sb.append(" + ");
					} else {
						sb.append(", ");
					}
				}
			}

			sb.append(") \n");
			return sb.toString();
		}

		sb.append(funcName + "(");;
		for (int i = 0; i < astCallFunctionExpr.getArgumentListSize(); i++) {
			ASTExpression astArgument = astCallFunctionExpr.getArgumentAt(i);
			sb.append(astArgument.accept(this));

			if (i < astCallFunctionExpr.getArgumentListSize() - 1) {
				boolean currentIsString = astArgument.getType() instanceof ASTStringType;
				boolean nextIsString = astCallFunctionExpr.getArgumentAt(i + 1).getType() instanceof ASTStringType;

				if (currentIsString && nextIsString) {
					sb.append(" + ");
				} else {
					sb.append(", ");
				}
			}
		}
		sb.append(") \n");
		return sb.toString();
	}

	@Override
	public String visit(ASTCallFunctionStat astCallFunctionStat)
	{
		StringBuilder sb = new StringBuilder();
		String funcName = astCallFunctionStat.getFunctionName().getId();

		if (builtInFunctions.contains(funcName)) {
			sb.append("knight::" + funcName + "(");
			for (int i = 0; i < astCallFunctionStat.getArgumentListSize(); i++) {
				ASTExpression astArgument = astCallFunctionStat.getArgumentAt(i);
				sb.append(astArgument.accept(this));

				if (i < astCallFunctionStat.getArgumentListSize() - 1) {
					boolean currentIsString = astArgument.getType() instanceof ASTStringType;
					boolean nextIsString = astCallFunctionStat.getArgumentAt(i + 1).getType() instanceof ASTStringType;

					if (currentIsString && nextIsString) {
						sb.append(" + ");
					} else {
						sb.append(", ");
					}
				}
			}

			sb.append("); \n");
			return sb.toString();
		}

		sb.append(funcName + "(");;
		for (int i = 0; i < astCallFunctionStat.getArgumentListSize(); i++) {
			ASTExpression astArgument = astCallFunctionStat.getArgumentAt(i);
			sb.append(astArgument.accept(this));

			if (i < astCallFunctionStat.getArgumentListSize() - 1) {
				boolean currentIsString = astArgument.getType() instanceof ASTStringType;
				boolean nextIsString = astCallFunctionStat.getArgumentAt(i + 1).getType() instanceof ASTStringType;

				if (currentIsString && nextIsString) {
					sb.append(" + ");
				} else {
					sb.append(", ");
				}
			}
		}
		sb.append("); \n");
		return sb.toString();
	}

	@Override
	public String visit(ASTIntType astIntType)
	{
		return "int";
	}

	@Override
	public String visit(ASTStringType astStringType)
	{
		return "std::string";
	}

	@Override
	public String visit(ASTVoidType astVoidType)
	{
		return "void";
	}

	@Override
	public String visit(ASTBooleanType astBooleanType)
	{
		return "bool";
	}

	@Override
	public String visit(ASTIntArrayType astIntArrayType)
	{
		return "int[]";
	}

	@Override
	public String visit(ASTIdentifierType astIdentifierType)
	{
		return astIdentifierType.getId();
	}

	@Override
	public String visit(ASTIdentifier astIdentifier)
	{
		return astIdentifier.getId();
	}

	@Override
	public String visit(ASTArrayIndexExpr astArrayIndexExpr)
	{
		return null;
	}

	@Override
	public String visit(ASTArrayAssign astArrayAssign)
	{
		return null;
	}

	@Override
	public String visit(ASTStringLiteral astStringLiteral)
	{
		return astStringLiteral.getValue();
	}

	@Override
	public String visit(ASTVariable astVariable)
	{
		String type = astVariable.getType().accept(this);
		String identifier = astVariable.getId().accept(this);
		return type + " " + identifier + "; \n";
	}

	@Override
	public String visit(ASTVariableInit astVariableInit)
	{
		String type = astVariableInit.getType().accept(this);
		String identifier = astVariableInit.getId().accept(this);
		return type + " " + identifier + " = " + astVariableInit.getExpr().accept(this) + "; \n";
	}

	@Override
	public String visit(ASTFunction astFunction)
	{
		return null;
	}

	@Override
	public String visit(ASTFunctionReturn astFunctionReturn)
	{
		code.append(astFunctionReturn.getReturnType() + " " + astFunctionReturn.getFunctionName() + "(");

		for (int i = 0; i < astFunctionReturn.getArgumentListSize(); i++) {
			code.append(astFunctionReturn.getArgumentAt(i).accept(this));

			if (i != astFunctionReturn.getArgumentListSize() - 1) {
				code.append(", ");
			}
		}

		code.append(") { \n");

		code.append(astFunctionReturn.getBody().accept(this));

		code.append("\treturn " + astFunctionReturn.getReturnExpr().accept(this) + ";\n");

		code.append("} \n");

		return null;
	}

	@Override
	public String visit(ASTClass astClass)
	{
		code.append("class " + astClass.getClassName().accept(this) + " {\n");

		for (int i = 0; i < astClass.getPropertyListSize(); i++) {
			code.append(astClass.getPropertyDeclAt(i).accept(this) + "\n");
		}

		for (int i = 0; i < astClass.getFunctionListSize(); i++) {
			code.append(astClass.getFunctionDeclAt(i).accept(this) + "\n");
		}

		code.append("};\n");

		return null;
	}

	@Override
	public String visit(ASTProgram astProgram)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("#include <knight/knight_std.h>\n");

		for (ASTClass astClass : astProgram.getClassList()) {
			astClass.accept(this);
		}

		for (ASTFunction astFunction : astProgram.getFunctionList()) {
			astFunction.accept(this);
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

	@Override
	public String visit(ASTReturnStatement astReturnStatement)
	{
		return null;
	}

	@Override
	public String visit(ASTFunctionType astFunctionType)
	{
		return null;
	}

	@Override
	public String visit(ASTProperty astProperty)
	{
		String type = astProperty.getType().accept(this);
		String identifier = astProperty.getId().accept(this);
		return type + " " + identifier + ";";
	}

	@Override
	public String visit(ASTIfChain astIfChain)
	{
		StringBuilder sb = new StringBuilder();

		// Flag to check if we need to add "else if" or just "if"
		boolean firstBranch = true;

		for (ASTConditionalBranch branch : astIfChain.getBranches()) {
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
		if (astIfChain.getElseBody() != null) {
			sb.append("else ");
			sb.append("{\n");
			sb.append(astIfChain.getElseBody().accept(this)); // Print the else body
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
	public String visit(ASTArgument astArgument)
	{
		String type = astArgument.getType().accept(this);
		String identifier = astArgument.getIdentifier().accept(this);

		return type + " " + identifier;
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

	@Override
	public String visit(ASTOr astOr)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(astOr.getLeftSide().accept(this));
		sb.append("||");
		sb.append(astOr.getRightSide().accept(this));
		return sb.toString();
	}

	@Override
	public String visit(ASTAnd astAnd)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(astAnd.getLeftSide().accept(this));
		sb.append("&&");
		sb.append(astAnd.getRightSide().accept(this));
		return sb.toString();
	}

	@Override
	public String visit(ASTEquals astEquals)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(astEquals.getLeftSide().accept(this));
		sb.append("==");
		sb.append(astEquals.getRightSide().accept(this));
		return sb.toString();
	}

	@Override
	public String visit(ASTLessThan astLessThan)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(astLessThan.getLeftSide().accept(this));
		sb.append("<");
		sb.append(astLessThan.getRightSide().accept(this));
		return sb.toString();
	}

	@Override
	public String visit(ASTLessThanOrEqual astLessThanOrEqual)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(astLessThanOrEqual.getLeftSide().accept(this));
		sb.append("<=");
		sb.append(astLessThanOrEqual.getRightSide().accept(this));
		return sb.toString();
	}

	@Override
	public String visit(ASTGreaterThan astGreaterThan)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(astGreaterThan.getLeftSide().accept(this));
		sb.append(">");
		sb.append(astGreaterThan.getRightSide().accept(this));
		return sb.toString();
	}

	@Override
	public String visit(ASTGreaterThanOrEqual astGreaterThanOrEqual)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(astGreaterThanOrEqual.getLeftSide().accept(this));
		sb.append(">=");
		sb.append(astGreaterThanOrEqual.getRightSide().accept(this));
		return sb.toString();
	}

	@Override
	public String visit(ASTMinus astMinus)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(astMinus.getLeftSide().accept(this));
		sb.append("-");
		sb.append(astMinus.getRightSide().accept(this));
		return sb.toString();
	}

	@Override
	public String visit(ASTTimes astTimes)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(astTimes.getLeftSide().accept(this));
		sb.append("*");
		sb.append(astTimes.getRightSide().accept(this));
		return sb.toString();
	}

	@Override
	public String visit(ASTDivision astDivision)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(astDivision.getLeftSide().accept(this));
		sb.append("/");
		sb.append(astDivision.getRightSide().accept(this));
		return sb.toString();
	}

	@Override
	public String visit(ASTModulus astModulus)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(astModulus.getLeftSide().accept(this));
		sb.append("%");
		sb.append(astModulus.getRightSide().accept(this));
		return sb.toString();
	}
}
