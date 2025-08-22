package knight.compiler.codegen;

import knight.compiler.ast.AST;
import knight.compiler.ast.ASTVisitor;
import knight.compiler.ast.controlflow.ASTConditionalBranch;
import knight.compiler.ast.controlflow.ASTForEach;
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
		sb.append(astAssign.getExpression().accept(this));
		return sb.append(";\n").toString();
	}

	@Override
	public String visit(ASTBody astBody)
	{
		StringBuilder sb = new StringBuilder();

		for (AST node : astBody.getNodes()) {
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
		return astIdentifierExpr.getName();
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
		String funcName = astCallFunctionExpr.getFunctionName().getName();
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
		for (int i = 0; i < astCallFunctionExpr.getArgumentCount(); i++) {
			ASTExpression astArgument = astCallFunctionExpr.getArgument(i);
			sb.append(astArgument.accept(this));

			if (i < astCallFunctionExpr.getArgumentCount() - 1) {
				boolean currentIsString = astArgument.getType() instanceof ASTStringType;
				boolean nextIsString = astCallFunctionExpr.getArgument(i + 1).getType() instanceof ASTStringType;

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
		String funcName = astCallFunctionStat.getFunctionName().getName();
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
		for (int i = 0; i < astCallFunctionStat.getArgumentCount(); i++) {
			ASTExpression astArgument = astCallFunctionStat.getArgument(i);
			sb.append(astArgument.accept(this));

			if (i < astCallFunctionStat.getArgumentCount() - 1) {
				boolean currentIsString = astArgument.getType() instanceof ASTStringType;
				boolean nextIsString = astCallFunctionStat.getArgument(i + 1).getType() instanceof ASTStringType;

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
		return astIdentifierType.getName();
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

		sb.append("class ").append(astInterface.getIdentifier().accept(this));

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

		for (ASTFunction astFunction : astInterface.getFunctions()) {
			String returnType = astFunction.getReturnType().accept(this);
			String functionName = astFunction.getIdentifier().toString();

			sb.append("    virtual ").append(returnType).append(" ").append(functionName).append("(");

			for (int i = 0; i < astFunction.getArgumentCount(); i++) {
				if (i > 0) {
					sb.append(", ");
				}
				sb.append(astFunction.getArgument(i).accept(this));
			}

			sb.append(") = 0;\n");
		}

		sb.append("    virtual ~").append(astInterface.getIdentifier().accept(this)).append("() {}\n");

		sb.append("};\n\n");

		return sb.toString();
	}

	@Override
	public String visit(ASTIdentifier astIdentifier)
	{
		return astIdentifier.getName();
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
		String identifier = astVariable.getIdentifier().accept(this);
		return type + " " + identifier;
	}

	@Override
	public String visit(ASTVariableInit astVariableInit)
	{
		String type = astVariableInit.getType().accept(this);
		String identifier = astVariableInit.getIdentifier().accept(this);
		return type + " " + identifier + " = " + astVariableInit.getExpression().accept(this) + "; \n";
	}

	@Override
	public String visit(ASTFunction astFunction)
	{
		StringBuilder sb = new StringBuilder();

		sb.append(astFunction.getReturnType().accept(this) + " " + astFunction.getIdentifier() + "(");

		for (int i = 0; i < astFunction.getArgumentCount(); i++) {
			sb.append(astFunction.getArgument(i).accept(this));

			if (i != astFunction.getArgumentCount() - 1) {
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

		String className = astClass.getIdentifier().getName();

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

		for (int i = 0; i < astClass.getPropertyCount(); i++) {
			sb.append(astClass.getProperty(i).accept(this) + "\n");
		}

		for (int i = 0; i < astClass.getFunctionCount(); i++) {
			sb.append(astClass.getFunction(i).accept(this) + "\n");
		}

		if (astClass.getExtendsClass() != null || !astClass.getImplementsInterfaces().isEmpty()) {
			sb.append("public:\n").append("    virtual ~").append(astClass.getIdentifier().accept(this))
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
		// if (requiredHeaders.contains("iostream")) {
		// sb.append("// Standard Library Implementation\n");
		// sb.append(knight.compiler.library.LibraryCodeGenerator.generateStandardLibrary());
		// sb.append("\n");
		// }

		return sb;
	}

	@Override
	public String visit(ASTProgram astProgram)
	{
		StringBuilder sb = new StringBuilder();

		// for (ASTImport astImport : astProgram.getImportList()) {
		// sb.append(astImport.accept(this));
		// }

		for (AST node : astProgram.getNodes()) {
			sb.append(node.accept(this));
		}

		generatedCode.append(sb);

		// write(generateHeaders().append(sb).toString());

		return null;
	}

	@Override
	public String visit(ASTReturnStatement astReturnStatement)
	{
		return "return " + astReturnStatement.getExpression().accept(this);
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
		String identifier = astProperty.getIdentifier().accept(this);
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
		sb.append(astNotEquals.getLeft().accept(this));
		sb.append("!=");
		sb.append(astNotEquals.getRight().accept(this));
		return sb.toString();
	}

	@Override
	public String visit(ASTPlus astPlus)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(astPlus.getLeft().accept(this));
		sb.append("+");
		sb.append(astPlus.getRight().accept(this));
		return sb.toString();
	}

	@Override
	public String visit(ASTOr astOr)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(astOr.getLeft().accept(this));
		sb.append("||");
		sb.append(astOr.getRight().accept(this));
		return sb.toString();
	}

	@Override
	public String visit(ASTAnd astAnd)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(astAnd.getLeft().accept(this));
		sb.append("&&");
		sb.append(astAnd.getRight().accept(this));
		return sb.toString();
	}

	@Override
	public String visit(ASTEquals astEquals)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(astEquals.getLeft().accept(this));
		sb.append("==");
		sb.append(astEquals.getRight().accept(this));
		return sb.toString();
	}

	@Override
	public String visit(ASTLessThan astLessThan)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(astLessThan.getLeft().accept(this));
		sb.append("<");
		sb.append(astLessThan.getRight().accept(this));
		return sb.toString();
	}

	@Override
	public String visit(ASTLessThanOrEqual astLessThanOrEqual)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(astLessThanOrEqual.getLeft().accept(this));
		sb.append("<=");
		sb.append(astLessThanOrEqual.getRight().accept(this));
		return sb.toString();
	}

	@Override
	public String visit(ASTGreaterThan astGreaterThan)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(astGreaterThan.getLeft().accept(this));
		sb.append(">");
		sb.append(astGreaterThan.getRight().accept(this));
		return sb.toString();
	}

	@Override
	public String visit(ASTGreaterThanOrEqual astGreaterThanOrEqual)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(astGreaterThanOrEqual.getLeft().accept(this));
		sb.append(">=");
		sb.append(astGreaterThanOrEqual.getRight().accept(this));
		return sb.toString();
	}

	@Override
	public String visit(ASTMinus astMinus)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(astMinus.getLeft().accept(this));
		sb.append("-");
		sb.append(astMinus.getRight().accept(this));
		return sb.toString();
	}

	@Override
	public String visit(ASTTimes astTimes)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(astTimes.getLeft().accept(this));
		sb.append("*");
		sb.append(astTimes.getRight().accept(this));
		return sb.toString();
	}

	@Override
	public String visit(ASTDivision astDivision)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(astDivision.getLeft().accept(this));
		sb.append("/");
		sb.append(astDivision.getRight().accept(this));
		return sb.toString();
	}

	@Override
	public String visit(ASTModulus astModulus)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(astModulus.getLeft().accept(this));
		sb.append("%");
		sb.append(astModulus.getRight().accept(this));
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

		for (int i = 0; i < astArrayLiteral.getExpressionCount(); i++) {
			ASTExpression astExpression = astArrayLiteral.getExpression(i);
			sb.append(astExpression.accept(this));

			if (i < astArrayLiteral.getExpressionCount() - 1) {
				boolean currentIsString = astExpression.getType() instanceof ASTStringType;
				boolean nextIsString = astArrayLiteral.getExpression(i + 1).getType() instanceof ASTStringType;

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
	public String visit(ASTForEach astForEach)
	{
		StringBuilder sb = new StringBuilder();

		sb.append("for (");
		sb.append(astForEach.getVariable().accept(this));
		sb.append(" : ");
		sb.append(astForEach.getIterable().accept(this));
		sb.append(") {");
		sb.append(astForEach.getBody().accept(this));
		sb.append("}");

		return sb.toString();
	}

	@Override
	public String visit(ASTLambda astLambda)
	{
		StringBuilder sb = new StringBuilder();

		sb.append("[](");

		for (int i = 0; i < astLambda.getArgumentCount(); i++) {
			sb.append(astLambda.getArgument(i).accept(this));

			if (i != astLambda.getArgumentCount() - 1) {
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
		return "#include <knight/" + astImport.getIdentifier() + ".h>\n";
	}
}
