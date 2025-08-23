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

public class CodeGenerator implements ASTVisitor<Void>
{
	private final CodeBuilder codeBuilder;
	private final HeaderManager headerManager;
	private final TypeConverter typeConverter;

	public CodeGenerator(String progPath, String filename)
	{
		this.codeBuilder = new CodeBuilder();
		this.headerManager = new HeaderManager();
		this.typeConverter = new TypeConverter(headerManager);

		headerManager.addRequiredHeader("iostream");
		headerManager.addRequiredHeader("string");
		headerManager.addRequiredHeader("vector");
		headerManager.addRequiredHeader("functional");
	}

	public String getGeneratedCode()
	{
		return codeBuilder.buildCompleteCode(headerManager);
	}

	@Override
	public Void visit(ASTProgram astProgram)
	{
		generateDeclarations(astProgram);
		generateImplementations(astProgram);
		return null;
	}

	private void generateDeclarations(ASTProgram astProgram)
	{
		codeBuilder.startDeclarationSection();

		for (AST node : astProgram.getNodes()) {
			if (node instanceof ASTClass || node instanceof ASTInterface) {
				node.accept(this);
			} else if (node instanceof ASTFunction) {
				generateFunctionDeclaration((ASTFunction) node);
			}
		}

		codeBuilder.endDeclarationSection();
	}

	private void generateImplementations(ASTProgram astProgram)
	{
		codeBuilder.startImplementationSection();

		for (AST node : astProgram.getNodes()) {
			if (node instanceof ASTFunction) {
				generateFunctionImplementation((ASTFunction) node, null);
			} else if (node instanceof ASTClass) {
				generateClassFunctions((ASTClass) node);
			}
		}

		codeBuilder.endImplementationSection();
	}

	@Override
	public Void visit(ASTClass astClass)
	{
		codeBuilder.appendToDeclarations("class " + astClass.getIdentifier().getName());

		if (astClass.getExtendsClass() != null || !astClass.getImplementsInterfaces().isEmpty()) {
			codeBuilder.append(" : ");
			boolean first = true;

			if (astClass.getExtendsClass() != null) {
				codeBuilder.append("public " + astClass.getExtendsClass().getName());
				first = false;
			}

			for (ASTIdentifier interfaceName : astClass.getImplementsInterfaces()) {
				if (!first) {
					codeBuilder.append(", ");
				}
				codeBuilder.append("public " + interfaceName.getName());
				first = false;
			}
		}

		codeBuilder.appendLine(" {");
		codeBuilder.appendLine("public:");
		codeBuilder.increaseIndent();

		for (int i = 0; i < astClass.getPropertyCount(); i++) {
			visit(astClass.getProperty(i));
		}

		for (int i = 0; i < astClass.getFunctionCount(); i++) {
			generateFunctionDeclaration(astClass.getFunction(i));
		}

		codeBuilder.decreaseIndent();
		codeBuilder.appendLine("};");
		return null;
	}

	@Override
	public Void visit(ASTInterface astInterface)
	{
		codeBuilder.appendToDeclarations("class " + astInterface.getIdentifier().getName());

		if (!astInterface.getExtendedInterfaces().isEmpty()) {
			codeBuilder.append(" : ");
			for (int i = 0; i < astInterface.getExtendedInterfaces().size(); i++) {
				if (i > 0)
					codeBuilder.append(", ");
				codeBuilder.append("public " + astInterface.getExtendedInterfaces().get(i).getName());
			}
		}

		codeBuilder.appendLine(" {");
		codeBuilder.appendLine("public:");
		codeBuilder.increaseIndent();

		for (ASTFunction function : astInterface.getFunctions()) {
			String returnType = typeConverter.convertType(function.getReturnType());
			codeBuilder.append("virtual " + returnType + " " + function.getIdentifier().getName() + "(");
			generateParameterList(function);
			codeBuilder.appendLine(") = 0;");
		}

		codeBuilder.appendLine("virtual ~" + astInterface.getIdentifier().getName() + "() {}");

		codeBuilder.decreaseIndent();
		codeBuilder.appendLine("};");
		return null;
	}

	private void generateFunctionDeclaration(ASTFunction function)
	{
		String returnType = typeConverter.convertType(function.getReturnType());
		codeBuilder.appendRawToDeclarations(returnType + " " + function.getIdentifier().getName() + "(");
		generateParameterList(function);
		codeBuilder.appendLine(");");
	}

	@Override
	public Void visit(ASTProperty astProperty)
	{
		String type = typeConverter.convertType(astProperty.getType());
		codeBuilder.appendToDeclarations(type + " " + astProperty.getIdentifier().getName() + ";");
		return null;
	}

	private void generateClassFunctions(ASTClass astClass)
	{
		for (int i = 0; i < astClass.getFunctionCount(); i++) {
			ASTFunction function = astClass.getFunction(i);
			generateFunctionImplementation(function, astClass.getIdentifier().getName());
		}
	}

	private void generateFunctionImplementation(ASTFunction function, String className)
	{
		String returnType = typeConverter.convertType(function.getReturnType());
		String functionName = function.getIdentifier().getName();

		if (className != null) {
			codeBuilder.appendRawToImplementations(returnType + " " + className + "::" + functionName + "(");
		} else {
			codeBuilder.appendRawToImplementations(returnType + " " + functionName + "(");
		}

		generateParameterList(function);
		codeBuilder.appendLine(") {");
		codeBuilder.increaseIndent();

		for (AST node : function.getBody().getNodes()) {
			node.accept(this);
		}

		codeBuilder.decreaseIndent();
		codeBuilder.appendLine("}\n");
	}

	private void generateParameterList(ASTFunction function)
	{
		for (int i = 0; i < function.getArgumentCount(); i++) {
			ASTArgument arg = function.getArgument(i);
			String paramType = typeConverter.convertType(arg.getType());
			codeBuilder.append(paramType + " " + arg.getIdentifier().getName());
			if (i < function.getArgumentCount() - 1) {
				codeBuilder.append(", ");
			}
		}
	}

	@Override
	public Void visit(ASTFunction astFunction)
	{
		return null;
	}

	@Override
	public Void visit(ASTBody astBody)
	{
		codeBuilder.startBlock();
		for (AST node : astBody.getNodes()) {
			node.accept(this);
		}
		codeBuilder.endBlock();
		return null;
	}

	@Override
	public Void visit(ASTAssign astAssign)
	{
		astAssign.getIdentifier().accept(this);
		codeBuilder.append(" = ");
		astAssign.getExpression().accept(this);
		codeBuilder.appendLine(";");
		return null;
	}

	@Override
	public Void visit(ASTFieldAssign astFieldAssign)
	{
		astFieldAssign.getInstance().accept(this);
		codeBuilder.append(".");
		astFieldAssign.getField().accept(this);
		codeBuilder.append(" = ");
		astFieldAssign.getValue().accept(this);
		codeBuilder.appendLine(";");
		return null;
	}

	@Override
	public Void visit(ASTVariable astVariable)
	{
		String type = typeConverter.convertType(astVariable.getType());
		codeBuilder.appendLine(type + " " + astVariable.getIdentifier().getName() + ";");
		return null;
	}

	@Override
	public Void visit(ASTVariableInit astVariableInit)
	{
		String type = typeConverter.convertType(astVariableInit.getType());
		codeBuilder.append(type + " " + astVariableInit.getIdentifier().getName() + " = ");
		astVariableInit.getExpression().accept(this);
		codeBuilder.appendLine(";");
		return null;
	}

	@Override
	public Void visit(ASTReturnStatement astReturnStatement)
	{
		codeBuilder.append("return ");
		if (astReturnStatement.getExpression() != null) {
			astReturnStatement.getExpression().accept(this);
		}
		codeBuilder.appendLine(";");
		return null;
	}

	@Override
	public Void visit(ASTWhile astWhile)
	{
		codeBuilder.append("while (");
		astWhile.getCondition().accept(this);
		codeBuilder.append(") ");
		astWhile.getBody().accept(this);
		return null;
	}

	@Override
	public Void visit(ASTIfChain astIfChain)
	{
		boolean first = true;
		for (ASTConditionalBranch branch : astIfChain.getBranches()) {
			if (first) {
				codeBuilder.append("if (");
				first = false;
			} else {
				codeBuilder.append(" else if (");
			}
			branch.getCondition().accept(this);
			codeBuilder.append(") ");
			branch.getBody().accept(this);
		}

		if (astIfChain.getElseBody() != null) {
			codeBuilder.append(" else ");
			astIfChain.getElseBody().accept(this);
		}

		return null;
	}

	@Override
	public Void visit(ASTForEach astForEach)
	{
		codeBuilder.append("for (");
		codeBuilder.append(typeConverter.convertType(astForEach.getVariable().getType()));
		codeBuilder.append(" ");
		codeBuilder.append(astForEach.getVariable().getIdentifier().getName());
		codeBuilder.append(" : ");
		astForEach.getIterable().accept(this);
		codeBuilder.append(") ");
		astForEach.getBody().accept(this);
		return null;
	}

	@Override
	public Void visit(ASTArrayAssign astArrayAssign)
	{
		astArrayAssign.getIdentifier().accept(this);
		codeBuilder.append("[");
		astArrayAssign.getArray().accept(this);
		codeBuilder.append("] = ");
		astArrayAssign.getValue().accept(this);
		codeBuilder.appendLine(";");
		return null;
	}

	@Override
	public Void visit(ASTCallFunctionStat astCallFunctionStat)
	{
		if (astCallFunctionStat.getInstance() != null) {
			astCallFunctionStat.getInstance().accept(this);
			codeBuilder.append(".");
		}
		codeBuilder.append(astCallFunctionStat.getFunctionName().getName() + "(");
		for (int i = 0; i < astCallFunctionStat.getArgumentCount(); i++) {
			astCallFunctionStat.getArgument(i).accept(this);
			if (i < astCallFunctionStat.getArgumentCount() - 1) {
				codeBuilder.append(", ");
			}
		}
		codeBuilder.appendLine(");");
		return null;
	}

	@Override
	public Void visit(ASTIntLiteral astIntLiteral)
	{
		codeBuilder.append(String.valueOf(astIntLiteral.getValue()));
		return null;
	}

	@Override
	public Void visit(ASTStringLiteral astStringLiteral)
	{
		codeBuilder.append(astStringLiteral.getValue());
		return null;
	}

	@Override
	public Void visit(ASTTrue astTrue)
	{
		codeBuilder.append("true");
		return null;
	}

	@Override
	public Void visit(ASTFalse astFalse)
	{
		codeBuilder.append("false");
		return null;
	}

	@Override
	public Void visit(ASTIdentifierExpr astIdentifierExpr)
	{
		codeBuilder.append(astIdentifierExpr.getName());
		return null;
	}

	@Override
	public Void visit(ASTCallFunctionExpr astCallFunctionExpr)
	{
		if (astCallFunctionExpr.getInstance() != null) {
			astCallFunctionExpr.getInstance().accept(this);
			codeBuilder.append(".");
		}
		codeBuilder.append(astCallFunctionExpr.getFunctionName().getName() + "(");
		for (int i = 0; i < astCallFunctionExpr.getArgumentCount(); i++) {
			astCallFunctionExpr.getArgument(i).accept(this);
			if (i < astCallFunctionExpr.getArgumentCount() - 1) {
				codeBuilder.append(", ");
			}
		}
		codeBuilder.append(")");
		return null;
	}

	@Override
	public Void visit(ASTNewInstance astNewInstance)
	{
		codeBuilder.append(astNewInstance.getClassName().getName() + "(");
		for (int i = 0; i < astNewInstance.getArguments().size(); i++) {
			astNewInstance.getArguments().get(i).accept(this);
			if (i < astNewInstance.getArguments().size() - 1) {
				codeBuilder.append(", ");
			}
		}
		codeBuilder.append(")");
		return null;
	}

	@Override
	public Void visit(ASTNewArray astNewArray)
	{
		String elementType = typeConverter.convertType(astNewArray.getType());
		codeBuilder.append("std::vector<" + elementType + ">(");
		astNewArray.getArrayLength().accept(this);
		codeBuilder.append(")");
		return null;
	}

	@Override
	public Void visit(ASTArrayIndexExpr astArrayIndexExpr)
	{
		astArrayIndexExpr.getArray().accept(this);
		codeBuilder.append("[");
		astArrayIndexExpr.getIndex().accept(this);
		codeBuilder.append("]");
		return null;
	}

	@Override
	public Void visit(ASTArrayLiteral astArrayLiteral)
	{
		String elementType = typeConverter.convertType(astArrayLiteral.getType());
		codeBuilder.append("std::vector<" + elementType + ">{");
		for (int i = 0; i < astArrayLiteral.getExpressionCount(); i++) {
			astArrayLiteral.getExpression(i).accept(this);
			if (i < astArrayLiteral.getExpressionCount() - 1) {
				codeBuilder.append(", ");
			}
		}
		codeBuilder.append("}");
		return null;
	}

	@Override
	public Void visit(ASTLambda astLambda)
	{
		codeBuilder.append("[");
		// Capture list (empty for now)
		codeBuilder.append("](");

		for (int i = 0; i < astLambda.getArgumentCount(); i++) {
			ASTArgument arg = astLambda.getArgument(i);
			String paramType = typeConverter.convertType(arg.getType());
			codeBuilder.append(paramType + " " + arg.getIdentifier().getName());
			if (i < astLambda.getArgumentCount() - 1) {
				codeBuilder.append(", ");
			}
		}
		codeBuilder.append(") -> " + typeConverter.convertType(astLambda.getReturnType()) + " ");
		astLambda.getBody().accept(this);
		return null;
	}

	@Override
	public Void visit(ASTPlus astPlus)
	{
		return visitBinaryOperator(astPlus, "+");
	}

	@Override
	public Void visit(ASTMinus astMinus)
	{
		return visitBinaryOperator(astMinus, "-");
	}

	@Override
	public Void visit(ASTTimes astTimes)
	{
		return visitBinaryOperator(astTimes, "*");
	}

	@Override
	public Void visit(ASTDivision astDivision)
	{
		return visitBinaryOperator(astDivision, "/");
	}

	@Override
	public Void visit(ASTModulus astModulus)
	{
		return visitBinaryOperator(astModulus, "%");
	}

	@Override
	public Void visit(ASTEquals astEquals)
	{
		return visitBinaryOperator(astEquals, "==");
	}

	@Override
	public Void visit(ASTNotEquals astNotEquals)
	{
		return visitBinaryOperator(astNotEquals, "!=");
	}

	@Override
	public Void visit(ASTLessThan astLessThan)
	{
		return visitBinaryOperator(astLessThan, "<");
	}

	@Override
	public Void visit(ASTLessThanOrEqual astLessThanOrEqual)
	{
		return visitBinaryOperator(astLessThanOrEqual, "<=");
	}

	@Override
	public Void visit(ASTGreaterThan astGreaterThan)
	{
		return visitBinaryOperator(astGreaterThan, ">");
	}

	@Override
	public Void visit(ASTGreaterThanOrEqual astGreaterThanOrEqual)
	{
		return visitBinaryOperator(astGreaterThanOrEqual, ">=");
	}

	@Override
	public Void visit(ASTAnd astAnd)
	{
		return visitBinaryOperator(astAnd, "&&");
	}

	@Override
	public Void visit(ASTOr astOr)
	{
		return visitBinaryOperator(astOr, "||");
	}

	private Void visitBinaryOperator(ASTBinaryExpression operator, String opSymbol)
	{
		operator.getLeft().accept(this);
		codeBuilder.append(" " + opSymbol + " ");
		operator.getRight().accept(this);
		return null;
	}

	@Override
	public Void visit(ASTIntType astIntType)
	{
		return null;
	}

	@Override
	public Void visit(ASTStringType astStringType)
	{
		return null;
	}

	@Override
	public Void visit(ASTVoidType astVoidType)
	{
		return null;
	}

	@Override
	public Void visit(ASTBooleanType astBooleanType)
	{
		return null;
	}

	@Override
	public Void visit(ASTIntArrayType astIntArrayType)
	{
		return null;
	}

	@Override
	public Void visit(ASTStringArrayType astStringArrayType)
	{
		return null;
	}

	@Override
	public Void visit(ASTIdentifierType astIdentifierType)
	{
		return null;
	}

	@Override
	public Void visit(ASTParameterizedType astParameterizedType)
	{
		return null;
	}

	@Override
	public Void visit(ASTFunctionType astFunctionType)
	{
		return null;
	}

	@Override
	public Void visit(ASTIdentifier astIdentifier)
	{
		return null;
	}

	@Override
	public Void visit(ASTArgument astArgument)
	{
		return null;
	}

	@Override
	public Void visit(ASTConditionalBranch astConditionalBranch)
	{
		return null;
	}

	@Override
	public Void visit(ASTImport astImport)
	{
		// headerManager.addRequiredHeader(astImport.getIdentifier().getName());
		return null;
	}
}
