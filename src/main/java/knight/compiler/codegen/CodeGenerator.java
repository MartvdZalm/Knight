package knight.compiler.codegen;

import java.io.File;
import java.io.PrintWriter;
import java.util.Set;

import knight.compiler.ast.AST;
import knight.compiler.ast.ASTAnd;
import knight.compiler.ast.ASTArgument;
import knight.compiler.ast.ASTArrayAssign;
import knight.compiler.ast.ASTArrayIndexExpr;
import knight.compiler.ast.ASTArrayLiteral;
import knight.compiler.ast.ASTAssign;
import knight.compiler.ast.ASTBody;
import knight.compiler.ast.ASTCallFunctionExpr;
import knight.compiler.ast.ASTCallFunctionStat;
import knight.compiler.ast.ASTClass;
import knight.compiler.ast.ASTConditionalBranch;
import knight.compiler.ast.ASTDivision;
import knight.compiler.ast.ASTEquals;
import knight.compiler.ast.ASTExpression;
import knight.compiler.ast.ASTFalse;
import knight.compiler.ast.ASTForeach;
import knight.compiler.ast.ASTFunction;
import knight.compiler.ast.ASTFunctionReturn;
import knight.compiler.ast.ASTGreaterThan;
import knight.compiler.ast.ASTGreaterThanOrEqual;
import knight.compiler.ast.ASTIdentifier;
import knight.compiler.ast.ASTIdentifierExpr;
import knight.compiler.ast.ASTIfChain;
import knight.compiler.ast.ASTImport;
import knight.compiler.ast.ASTIntLiteral;
import knight.compiler.ast.ASTLambda;
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
import knight.compiler.ast.ASTTimes;
import knight.compiler.ast.ASTTrue;
import knight.compiler.ast.ASTVariable;
import knight.compiler.ast.ASTVariableInit;
import knight.compiler.ast.ASTVisitor;
import knight.compiler.ast.ASTWhile;
import knight.compiler.ast.types.ASTBooleanType;
import knight.compiler.ast.types.ASTFunctionType;
import knight.compiler.ast.types.ASTIdentifierType;
import knight.compiler.ast.types.ASTIntArrayType;
import knight.compiler.ast.types.ASTIntType;
import knight.compiler.ast.types.ASTParameterizedType;
import knight.compiler.ast.types.ASTStringArrayType;
import knight.compiler.ast.types.ASTStringType;
import knight.compiler.ast.types.ASTVoidType;
import knight.compiler.semantics.model.SymbolClass;
import knight.compiler.semantics.model.SymbolFunction;

public class CodeGenerator implements ASTVisitor<String>
{
	private SymbolClass currentClass;
	private SymbolFunction currentFunction;

	private final String PATH;
	private final String FILENAME;

	Set<String> builtInFunctions = Set.of("print", "to_upper", "to_lower", "trim", "split", "read", "now", "random",
			"to_int", "to_string", "read_line", "join", "filter", "sort");

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

		for (AST node : astBody.getNodesList()) {
			sb.append(node.accept(this) + ";\n");
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

			sb.append(")");
			return sb.toString();
		}

		if (astCallFunctionExpr.getInstance() != null) {
			sb.append(astCallFunctionExpr.getInstance().accept(this) + ".");
		}

		sb.append(funcName + "(");
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
		sb.append(")");
		return sb.toString();
	}

	@Override
	public String visit(ASTCallFunctionStat astCallFunctionStat)
	{
		StringBuilder sb = new StringBuilder();
		String funcName = astCallFunctionStat.getFunctionName().getId();

		if (builtInFunctions.contains(funcName) && astCallFunctionStat.getInstance() == null) {
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

			sb.append(")");
			return sb.toString();
		}

		if (astCallFunctionStat.getInstance() != null) {
			sb.append(astCallFunctionStat.getInstance() + ".");
		}

		sb.append(funcName + "(");
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
		sb.append(")");
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
		return "std::vector<int>";
	}

	@Override
	public String visit(ASTIdentifierType astIdentifierType)
	{
		return astIdentifierType.getId();
	}

	@Override
	public String visit(ASTParameterizedType astParameterizedType)
	{
		return "knight::" + astParameterizedType.toString();
	}

	@Override
	public String visit(ASTIdentifier astIdentifier)
	{
		return astIdentifier.getId();
	}

	@Override
	public String visit(ASTArrayIndexExpr astArrayIndexExpr)
	{
		return astArrayIndexExpr.getArray().accept(this) + "[" + astArrayIndexExpr.getIndex().accept(this) + "]";
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
		return type + " " + identifier;
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
		StringBuilder sb = new StringBuilder();

		sb.append(astFunctionReturn.getReturnType().accept(this) + " " + astFunctionReturn.getFunctionName() + "(");

		for (int i = 0; i < astFunctionReturn.getArgumentListSize(); i++) {
			sb.append(astFunctionReturn.getArgumentAt(i).accept(this));

			if (i != astFunctionReturn.getArgumentListSize() - 1) {
				sb.append(", ");
			}
		}

		sb.append(") { \n");

		sb.append(astFunctionReturn.getBody().accept(this));

		// sb.append("\treturn " + astFunctionReturn.getReturnExpr().accept(this) +
		// ";\n");

		sb.append("} \n");

		return sb.toString();
	}

	@Override
	public String visit(ASTClass astClass)
	{
		StringBuilder sb = new StringBuilder();

		sb.append("class " + astClass.getClassName().accept(this) + " {\n");

		for (int i = 0; i < astClass.getPropertyListSize(); i++) {
			sb.append(astClass.getPropertyDeclAt(i).accept(this) + "\n");
		}

		for (int i = 0; i < astClass.getFunctionListSize(); i++) {
			sb.append(astClass.getFunctionDeclAt(i).accept(this) + "\n");
		}

		sb.append("};\n");

		return sb.toString();
	}

	@Override
	public String visit(ASTProgram astProgram)
	{
		StringBuilder sb = new StringBuilder();

		for (ASTImport astImport : astProgram.getImportList()) {
			sb.append(astImport.accept(this));
		}

		for (AST node : astProgram.getNodeList()) {
			sb.append(node.accept(this));
		}

//		for (ASTVariable astVariable : astProgram.getVariableList()) {
//			sb.append(astVariable.accept(this));
//		}
//
//		for (ASTClass astClass : astProgram.getClassList()) {
//			astClass.accept(this);
//		}
//
//		for (ASTFunction astFunction : astProgram.getFunctionList()) {
//			astFunction.accept(this);
//		}

		write(sb.toString());

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
		return "return " + astReturnStatement.getReturnExpr().accept(this);
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

		boolean firstBranch = true;

		for (ASTConditionalBranch branch : astIfChain.getBranches()) {
			if (!firstBranch) {
				sb.append(" else ");
			}

			sb.append("if (");
			sb.append(branch.getCondition().accept(this));
			sb.append(") ");

			sb.append("{\n");
			sb.append(branch.getBody().accept(this));
			sb.append("\n}\n");

			firstBranch = false;
		}

		if (astIfChain.getElseBody() != null) {
			sb.append("else ");
			sb.append("{\n");
			sb.append(astIfChain.getElseBody().accept(this));
			sb.append("\n}\n");
		}

		return sb.toString();
	}

	@Override
	public String visit(ASTConditionalBranch astConditionalBranch)
	{
		StringBuilder sb = new StringBuilder();

		sb.append(astConditionalBranch.getCondition().accept(this));

		sb.append(" {\n");
		sb.append(astConditionalBranch.getBody().accept(this));
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

	@Override
	public String visit(ASTStringArrayType astStringArrayType)
	{
		return "std::vector<std::string>";
	}

	@Override
	public String visit(ASTArrayLiteral astArrayLiteral)
	{
		StringBuilder sb = new StringBuilder();

		sb.append("{");

		for (int i = 0; i < astArrayLiteral.getExpressionListSize(); i++) {
			ASTExpression astExpression = astArrayLiteral.getExpressionAt(i);
			sb.append(astExpression.accept(this));

			if (i < astArrayLiteral.getExpressionListSize() - 1) {
				boolean currentIsString = astExpression.getType() instanceof ASTStringType;
				boolean nextIsString = astArrayLiteral.getExpressionAt(i + 1).getType() instanceof ASTStringType;

				if (currentIsString && nextIsString) {
					sb.append(" + ");
				} else {
					sb.append(", ");
				}
			}
		}
		sb.append("}");

		return sb.toString();
	}

	@Override
	public String visit(ASTForeach astForeach)
	{
		StringBuilder sb = new StringBuilder();

		sb.append("for (");
		sb.append(astForeach.getVariable().accept(this));
		sb.append(" : ");
		sb.append(astForeach.getIterable().accept(this));
		sb.append(") {");
		sb.append(astForeach.getBody().accept(this));
		sb.append("}");

		return sb.toString();
	}

	@Override
	public String visit(ASTLambda astLambda)
	{
		StringBuilder sb = new StringBuilder();

		sb.append("[](");

		for (int i = 0; i < astLambda.getArgumentListSize(); i++) {
			sb.append(astLambda.getArgumentAt(i).accept(this));

			if (i != astLambda.getArgumentListSize() - 1) {
				sb.append(", ");
			}
		}

		sb.append(") -> " + astLambda.getReturnType().accept(this) + " { ");
		sb.append(astLambda.getBody().accept(this));
		sb.append("}");

		return sb.toString();
	}

	@Override
	public String visit(ASTImport astImport)
	{
		return "#include <knight/" + astImport.getLibrary() + ".h>\n";
	}
}
