package knight.compiler.codegen;

import knight.compiler.ast.AST;
import knight.compiler.ast.ASTVisitor;
import knight.compiler.ast.controlflow.ASTConditionalBranch;
import knight.compiler.ast.controlflow.ASTForeach;
import knight.compiler.ast.controlflow.ASTIfChain;
import knight.compiler.ast.controlflow.ASTWhile;
import knight.compiler.ast.expressions.*;
import knight.compiler.ast.program.*;
import knight.compiler.ast.statements.*;
import knight.compiler.ast.types.*;
import knight.compiler.semantics.model.SymbolClass;
import knight.compiler.semantics.model.SymbolFunction;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class CodeGenerator implements ASTVisitor<String>
{
	private SymbolClass currentClass;
	private SymbolFunction currentFunction;
	private Set<String> requiredHeaders = new HashSet<>();
	private StringBuilder generatedCode = new StringBuilder();

	public CodeGenerator(String progPath, String filename)
	{
		File file = new File(filename);
		String name = file.getName();

		requiredHeaders.add("iostream");
		requiredHeaders.add("string");
		requiredHeaders.add("vector");
	}

	public StringBuilder getGeneratedCode()
	{
		return generatedCode;
	}

	private Optional<String> handleLibraryCall(String className)
	{
		if (className == null) {
			return Optional.empty();
		}

		// Check if this is a standard library class
		if ("Out".equals(className) || "In".equals(className)) {
			requiredHeaders.add("iostream");
			return Optional.of(""); // Library code will be generated separately
		}

		return Optional.empty();
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
		StringBuilder sb = new StringBuilder();
		sb.append(astNewInstance.getClassName().accept(this)).append("(");

		List<ASTArgument> args = astNewInstance.getArguments();
		for (int i = 0; i < args.size(); i++) {
			sb.append(args.get(i).accept(this));
			if (i < args.size() - 1) {
				sb.append(", ");
			}
		}

		sb.append(")");
		return sb.toString();
	}

	@Override
	public String visit(ASTCallFunctionExpr astCallFunctionExpr)
	{
		String funcName = astCallFunctionExpr.getFunctionName().getId();
		// String className = null;

		// if (astCallFunctionExpr.getInstance() != null) {
		// if (astCallFunctionExpr.getInstance() instanceof ASTIdentifierExpr) {
		// className = ((ASTIdentifierExpr) astCallFunctionExpr.getInstance()).getId();
		// }
		// }
		//
		// if (className != null) {
		// List<ASTExpression> arguments = astCallFunctionExpr.getArgumentList();
		// Optional<String> intrinsicCode = handleIntrinsicCall(className, funcName,
		// arguments);
		//
		// if (intrinsicCode.isPresent()) {
		// return intrinsicCode.get();
		// }
		// }

		StringBuilder sb = new StringBuilder();

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
		String funcName = astCallFunctionStat.getFunctionName().getId();
		// String className = null;
		//
		// if (astCallFunctionStat.getInstance() != null) {
		// className = astCallFunctionStat.getInstance().toString();
		//
		// }
		//
		// if (className != null) {
		// List<ASTExpression> arguments = astCallFunctionStat.getArgumentList();
		// Optional<String> intrinsicCode = handleIntrinsicCall(className, funcName,
		// arguments);
		//
		// if (intrinsicCode.isPresent()) {
		// return intrinsicCode.get();
		// }
		// }

		StringBuilder sb = new StringBuilder();

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
	public String visit(ASTInterface astInterface)
	{
		StringBuilder sb = new StringBuilder();

		sb.append("class ").append(astInterface.getName().accept(this));

		if (!astInterface.getExtendedInterfaces().isEmpty()) {
			sb.append(" : ");
			for (int i = 0; i < astInterface.getExtendedInterfaces().size(); i++) {
				if (i > 0) {
					sb.append(", ");
				}
				sb.append("public ").append(astInterface.getExtendedInterfaces().get(i).accept(this));
			}
		}

		sb.append(" {\n");
		sb.append("public:\n");

		for (ASTFunction method : astInterface.getFunctionSignatures()) {
			String returnType = method.getReturnType().accept(this);
			String functionName = method.getFunctionName().toString();

			sb.append("    virtual ").append(returnType).append(" ").append(functionName).append("(");

			for (int i = 0; i < method.getArgumentListSize(); i++) {
				if (i > 0) {
					sb.append(", ");
				}
				sb.append(method.getArgumentAt(i).accept(this));
			}

			sb.append(") = 0;\n");
		}

		sb.append("    virtual ~").append(astInterface.getName().accept(this)).append("() {}\n");

		sb.append("};\n\n");

		return sb.toString();
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
		StringBuilder sb = new StringBuilder();

		sb.append(astFunction.getReturnType().accept(this) + " " + astFunction.getFunctionName() + "(");

		for (int i = 0; i < astFunction.getArgumentListSize(); i++) {
			sb.append(astFunction.getArgumentAt(i).accept(this));

			if (i != astFunction.getArgumentListSize() - 1) {
				sb.append(", ");
			}
		}

		sb.append(") { \n");

		sb.append(astFunction.getBody().accept(this));

		sb.append("} \n");

		return sb.toString();
	}

	@Override
	public String visit(ASTClass astClass)
	{
		StringBuilder sb = new StringBuilder();

		// if (astClass.isStatic()) {
		// sb.append("static ");
		// }

		String className = astClass.getClassName().getId();

		Optional<String> libraryCode = handleLibraryCall(className);
		if (libraryCode.isPresent()) {
			return libraryCode.get();
		}

		sb.append("class ").append(className).append(" {\n");

		if (astClass.getExtendsClass() != null) {
			sb.append(" : public ").append(astClass.getExtendsClass().accept(this));
		}

		if (!astClass.getImplementsInterfaces().isEmpty()) {
			if (astClass.getExtendsClass() == null) {
				sb.append(" : ");
			} else {
				sb.append(", ");
			}

			for (int i = 0; i < astClass.getImplementsInterfaces().size(); i++) {
				if (i > 0) {
					sb.append(", ");
				}
				sb.append("public ").append(astClass.getImplementsInterfaces().get(i).accept(this));
			}
		}

		sb.append(" {\n");

		String currentAccess = "public";
		sb.append(currentAccess).append(":\n");

		for (int i = 0; i < astClass.getPropertyListSize(); i++) {
			sb.append(astClass.getPropertyDeclAt(i).accept(this) + "\n");
		}

		for (int i = 0; i < astClass.getFunctionListSize(); i++) {
			sb.append(astClass.getFunctionDeclAt(i).accept(this) + "\n");
		}

		if (astClass.getExtendsClass() != null || !astClass.getImplementsInterfaces().isEmpty()) {
			sb.append("public:\n").append("    virtual ~").append(astClass.getClassName().accept(this))
					.append("() {}\n");
		}

		sb.append("};\n");

		return sb.toString();
	}

	public StringBuilder generateHeaders()
	{
		StringBuilder sb = new StringBuilder();

		for (String header : requiredHeaders) {
			sb.append("#include <").append(header).append(">\n");
		}

		sb.append("\n");

		// Generate standard library code
		if (requiredHeaders.contains("iostream")) {
			sb.append("// Standard Library Implementation\n");
			sb.append(knight.compiler.library.LibraryCodeGenerator.generateStandardLibrary());
			sb.append("\n");
		}

		return sb;
	}

	@Override
	public String visit(ASTProgram astProgram)
	{
		StringBuilder sb = new StringBuilder();

		// for (ASTImport astImport : astProgram.getImportList()) {
		// sb.append(astImport.accept(this));
		// }

		for (AST node : astProgram.getNodeList()) {
			sb.append(node.accept(this));
		}

		generatedCode.append(sb);

		// write(generateHeaders().append(sb).toString());

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
