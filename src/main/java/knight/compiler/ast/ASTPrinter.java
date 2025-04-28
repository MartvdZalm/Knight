package knight.compiler.ast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import knight.compiler.parser.ParseException;
import knight.compiler.parser.Parser;

/*
 * File: ASTPrinter.java
 * @author: Mart van der Zalm
 * Date: 2025-04-10
 */
public class ASTPrinter implements ASTVisitor<String>
{
	int level = 0;

	private void incLevel()
	{
		level = level + 1;
	}

	private void decLevel()
	{
		level = level - 1;
	}

	private String printInc()
	{
		char[] chars = new char[level];
		java.util.Arrays.fill(chars, '\t');
		return new String(chars);
	}

	@Override
	public String visit(ASTAssign assign)
	{
		return printInc() + "(EQSIGN " + assign.getIdentifier().accept(this) + " " + assign.getExpr().accept(this)
				+ ")";
	}

	@Override
	public String visit(ASTBody body)
	{
		StringBuilder sb = new StringBuilder();

		for (ASTVariable variable : body.getVariableList()) {
			sb.append(variable.accept(this) + "\n");
		}

		for (ASTStatement statement : body.getStatementList()) {
			sb.append(statement.accept(this) + "\n");
		}

		return sb.toString();
	}

	@Override
	public String visit(ASTWhile w)
	{
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(printInc() + "(WHILE " + w.getCondition().accept(this) + "\n");

		incLevel();
		strBuilder.append(w.getBody().accept(this));
		decLevel();

		strBuilder.append(printInc() + ")\n");
		return strBuilder.toString();
	}

	@Override
	public String visit(ASTIntLiteral intLiteral)
	{
		return "(INTLIT " + intLiteral.getValue() + ")";
	}

	@Override
	public String visit(ASTTrue true1)
	{
		return "TRUE";
	}

	@Override
	public String visit(ASTFalse false1)
	{
		return "FALSE";
	}

	@Override
	public String visit(ASTIdentifierExpr identifier)
	{
		return "(" + identifier.getId() + ")";
	}

	@Override
	public String visit(ASTNewArray newArray)
	{
		return "(NEW-INT-ARRAY " + newArray.getArrayLength().accept(this) + ")";
	}

	@Override
	public String visit(ASTNewInstance newInstance)
	{
		return "(NEW) " + newInstance.getClassName().accept(this);
	}

	@Override
	public String visit(ASTCallFunctionExpr callFunctionExpr)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("(FUN-CALL" + callFunctionExpr.getFunctionName().accept(this));
		for (ASTExpression expr : callFunctionExpr.getArgumentList()) {
			sb.append(expr.accept(this));
		}
		sb.append("))");

		return sb.toString();
	}

	@Override
	public String visit(ASTCallFunctionStat callFunctionStat)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("(FUN-CALL " + callFunctionStat.getFunctionName().accept(this));
		for (ASTExpression expr : callFunctionStat.getArgumentList()) {
			sb.append(expr.accept(this));
		}
		sb.append("))");

		return sb.toString();
	}

	@Override
	public String visit(ASTIntType intType)
	{
		return "(INT)";
	}

	@Override
	public String visit(ASTStringType stringType)
	{
		return "(STRING)";
	}

	@Override
	public String visit(ASTBooleanType booleanType)
	{
		return "(BOOLEAN)";
	}

	@Override
	public String visit(ASTFunctionType functionType)
	{
		return "FUNCTION";
	}

	@Override
	public String visit(ASTIntArrayType intArrayType)
	{
		return "(INT-ARRAY)";
	}

	@Override
	public String visit(ASTVoidType voitType)
	{
		return "(VOID)";
	}

	@Override
	public String visit(ASTIdentifierType identifierType)
	{
		return "(IDENTIFIER)";
	}

	@Override
	public String visit(ASTVariable varDeclaration)
	{
		return printInc() + "(VARIABLE) " + varDeclaration.getType().accept(this) + " "
				+ varDeclaration.getId().accept(this);
	}

	@Override
	public String visit(ASTVariableInit varDeclarationInit)
	{
		return printInc() + "(VARIABLE) " + varDeclarationInit.getType().accept(this) + " "
				+ varDeclarationInit.getId().accept(this) + " = " + varDeclarationInit.getExpr().accept(this);
	}

	@Override
	public String visit(ASTFunction function)
	{
		StringBuilder sb = new StringBuilder();
//		sb.append(printInc() + "(FUNCTION) " + function.getId().accept(this) + " ");

		sb.append("(PARAMETERS)");
//		for (ASTArgument arg : function.getArgumentList()) {
//			sb.append(arg.accept(this));
//		}

		sb.append(" : " + function.getReturnType().accept(this) + "\n");

		sb.append(function.getBody().accept(this));

		// incLevel();

		// for (ASTVariable variable : function.getVariableList()) {
		// sb.append(variable.accept(this) + "\n");
		// }

		// for (ASTStatement statement : function.getStatementList()) {
		// sb.append(statement.accept(this) + "\n");
		// }

		// decLevel();

		return sb.toString();
	}

	@Override
	public String visit(ASTFunctionReturn functionReturn)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("(FUNC " + functionReturn.getReturnType().accept(this) + " "
				+ functionReturn.getFunctionName().accept(this));

		sb.append("(ARG-LIST ");
		for (ASTArgument argument : functionReturn.getArgumentList()) {
			sb.append(argument.accept(this));
		}
		sb.append(")\n");
		sb.append(printInc() + "(BODY\n");

		sb.append(functionReturn.getBody().accept(this));

		// incLevel();
		// for (ASTVariable variable : functionReturn.getVariableList()) {
		// sb.append(variable.accept(this) + "\n");
		// }
		// for (ASTStatement stat : functionReturn.getStatementList()) {
		// sb.append(stat.accept(this) + "\n");
		// }
		sb.append(printInc() + "(RETURN " + functionReturn.getReturnExpr().accept(this) + ")\n");
		// decLevel();

		sb.append(printInc() + ")\n");
		sb.append(printInc() + ")");
		return sb.toString();
	}

	@Override
	public String visit(ASTProgram program)
	{
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < program.getVariableListSize(); i++) {
			sb.append(program.getVariableDeclAt(i).accept(this) + "\n");
		}

		for (ASTFunction function : program.getFunctionList()) {
			sb.append(function.accept(this) + "\n");
		}

		for (ASTClass astClass : program.getClassList()) {
			sb.append(astClass.accept(this) + "\n");
		}

		return sb.toString();
	}

	@Override
	public String visit(ASTIdentifier identifier)
	{
		return "(" + identifier.getId() + ")";
	}

	@Override
	public String visit(ASTArrayIndexExpr arrayIndexExpr)
	{
		return "(ARRAY-LOOKUP " + arrayIndexExpr.getArray().accept(this) + arrayIndexExpr.getIndex().accept(this) + ")";
	}

	@Override
	public String visit(ASTArrayAssign arrayAssign)
	{
		return printInc() + "(EQSIGN " + "(ARRAY-ASSIGN " + arrayAssign.getId().accept(this)
				+ arrayAssign.getExpression1().accept(this) + ") " + arrayAssign.getExpression2().accept(this) + ")";
	}

	@Override
	public String visit(ASTStringLiteral stringLiteral)
	{
		return "(STRINGLIT " + stringLiteral.getValue() + ")";
	}

	@Override
	public String visit(ASTClass classDecl)
	{
		StringBuilder sb = new StringBuilder();
//		sb.append("(CLASS) " + classDecl.getId().accept(this) + "\n");

		incLevel();

		for (ASTProperty property : classDecl.getPropertyList()) {
			sb.append(property.accept(this) + "\n");
		}

		for (ASTFunction function : classDecl.getFunctionList()) {
			sb.append(function.accept(this) + "\n");
		}

		decLevel();
		sb.append(")");

		return sb.toString();
	}

	@Override
	public String visit(ASTReturnStatement returnStatement)
	{
		return null;
	}

	@Override
	public String visit(ASTProperty property)
	{
		return null;
	}

	public static String printFileAst(String filename) throws FileNotFoundException, ParseException
	{
		ASTPrinter printer = new ASTPrinter();

		BufferedReader br = new BufferedReader(new FileReader(filename));
		Parser p = new Parser(br);
		AST tree = p.parse();

		return printer.visit((ASTProgram) tree);
	}

	@Override
	public String visit(ASTIfChain ifChain)
	{
		StringBuilder sb = new StringBuilder();

		boolean firstBranch = true;
		for (ASTConditionalBranch astConditionalBranch : ifChain.getBranches()) {
			if (!firstBranch) {
				sb.append(" ELSE ");
			}
			sb.append("IF (");
			sb.append(astConditionalBranch.getCondition().accept(this));
			sb.append(")\n");
			sb.append(astConditionalBranch.getBody().accept(this));
			firstBranch = false;
		}

		if (ifChain.getElseBody() != null) {
			sb.append(" ELSE ");
			sb.append(ifChain.getElseBody().accept(this));
		}

		return sb.toString();
	}

	@Override
	public String visit(ASTConditionalBranch astConditionalBranch)
	{
		StringBuilder sb = new StringBuilder();

		sb.append(astConditionalBranch.getCondition().accept(this));
		sb.append(astConditionalBranch.getBody().accept(this));

		return sb.toString();
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
		return "(NOT-EQUALS " + astNotEquals.getLeftSide().accept(this) + " " + astNotEquals.getRightSide().accept(this)
				+ ")";
	}

	@Override
	public String visit(ASTPlus astPlus)
	{
		return "(PLUS " + astPlus.getLeftSide().accept(this) + " " + astPlus.getRightSide().accept(this) + ")";
	}

	@Override
	public String visit(ASTOr astOr)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visit(ASTAnd astAnd)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visit(ASTEquals astEquals)
	{
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visit(ASTMinus astMinus)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visit(ASTTimes astTimes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visit(ASTDivision astDivision)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visit(ASTModulus astModulus)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visit(ASTStringArrayType astStringArrayType)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visit(ASTArrayLiteral astArrayLiteral)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
