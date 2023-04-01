package ast;

import java.io.FileNotFoundException;
import parser.ParseException;
import parser.Parser;

public class ASTPrinter implements Visitor<String>
{
	int level = 0;

	void incLevel()
	{
		level = level + 1;
	}

	void decLevel()
	{
		level = level - 1;
	}

	@Override
	public String visit(Print print)
	{
		return "PRINT(" + print.expression.accept(this) + ")";
	}

	@Override
	public String visit(Assign assign)
	{
		return assign.id.accept(this) + "=" + assign.expr.accept(this);
	}

	@Override
	public String visit(Skip n)
	{
		return "";
	}

	@Override
	public String visit(Block n)
	{
		StringBuilder strBuilder = new StringBuilder();
		for (Statement stat : n.body) {
			strBuilder.append(stat.accept(this) + "\n");
		}
		return strBuilder.toString();
	}

	@Override
	public String visit(IfThenElse n)
	{
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("IF " + n.expr.accept(this) + "\n");
		strBuilder.append(n.then.accept(this));

		String elze = n.elze.accept(this);

		if (elze != null && elze.trim().length() > 0) {
			strBuilder.append("ELSE \n");
			strBuilder.append(elze + "\n");
			incLevel();
		}
		decLevel();

		strBuilder.append(")\n");
		return strBuilder.toString();
	}

	@Override
	public String visit(While n)
	{
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("WHILE " + n.expr.accept(this) + "\n");

		incLevel();
		strBuilder.append(n.body.accept(this));
		decLevel();
		return strBuilder.toString();
	}

	@Override
	public String visit(IntLiteral n) 
	{
		return "INTEGER(" + n.value + ")";
	}

	@Override
	public String visit(Plus n)
	{
		return n.lhs.accept(this) + " + " + n.rhs.accept(this);
	}

	@Override
	public String visit(Minus n)
	{
		return n.lhs.accept(this) + " - " + n.rhs.accept(this);
	}

	@Override
	public String visit(Times n)
	{
		return n.lhs.accept(this) + " * " + n.rhs.accept(this);
	}

	@Override
	public String visit(Division n)
	{
		return n.lhs.accept(this) + " / " + n.rhs.accept(this);
	}

	@Override
	public String visit(Equals n)
	{
		return n.lhs.accept(this) + " == " + n.rhs.accept(this);
	}

	@Override
	public String visit(LessThan n)
	{
		return n.lhs.accept(this) + " < " + n.rhs.accept(this);
	}

	@Override
	public String visit(And n)
	{
		return n.lhs.accept(this) + " and " + n.rhs.accept(this);
	}

	@Override
	public String visit(Or n)
	{
		return n.lhs.accept(this) + " or " + n.rhs.accept(this);
	}

	@Override
	public String visit(True true1)
	{
		return "TRUE";
	}

	@Override
	public String visit(False false1)
	{
		return "FALSE";
	}

	@Override
	public String visit(IdentifierExpr identifier)
	{
		return "ID(" + identifier.varID + ")";
	}

	@Override
	public String visit(This this1)
	{
		return "THIS";
	}

	@Override
	public String visit(NewArray newArray)
	{
		return "ARRAY(" + newArray.arrayLength.accept(this) + ")";
	}

	@Override
	public String visit(NewInstance newInstance)
	{
		return "NEW-INSTANCE(" + newInstance.className.accept(this) + ")";
	}

	@Override
	public String visit(CallFunc callFunc)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("(DOT " + callFunc.instanceName.accept(this) + " FUNCTIONCALL(" + callFunc.methodId.accept(this));
		for (Expression expr : callFunc.argExprList) {
			sb.append(expr.accept(this));
		}
		sb.append("))");

		return sb.toString();
	}

	@Override
	public String visit(Length length)
	{
		return "DOT(" + length.array.accept(this) + " LENGTH)";
	}

	@Override
	public String visit(IntType intType)
	{
		return "INT";
	}

	@Override
	public String visit(StringType stringType)
	{
		return "STRING";
	}

	@Override
	public String visit(BooleanType booleanType)
	{
		return "BOOLEAN";
	}

	@Override
	public String visit(IntArrayType intArrayType)
	{
		return "INT-ARRAY";
	}

	@Override
	public String visit(IdentifierType identifierType)
	{
		return "ID(" + identifierType.varID + ")";
	}

	@Override
	public String visit(VarDecl varDeclaration)
	{
		return "VARIABLE(" + varDeclaration.type.accept(this) + " " + varDeclaration.id.accept(this) + ")";
	}

	@Override
	public String visit(ArgDecl argDecl)
	{
		return "(" + argDecl.type.accept(this) + " " + argDecl.id.accept(this) + ")";
	}

	@Override
	public String visit(FuncDecl funcDecl)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("FUNCTION(" + funcDecl.returnType.accept(this) + " " + funcDecl.methodName.accept(this) + " ");

		sb.append("ARGUMENTS ");
		for (ArgDecl arg : funcDecl.argList) {
			sb.append(arg.accept(this));
		}

		sb.append("\n(");

		for (VarDecl var : funcDecl.varList) {
			sb.append(var.accept(this) + "\n");
		}
		for (Statement stat : funcDecl.statList) {
			sb.append(stat.accept(this) + "\n");
		}
		if (funcDecl.getReturnExpr() == null) {
			sb.append("\nRETURN VOID\n");
		} else {
			sb.append("\nRETURN " + funcDecl.returnExpr.accept(this) + "\n");
		}
		
		sb.append(")");
		return sb.toString();
	}

	@Override
	public String visit(MainClass mainClass)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("MAINCLASS\n");
		sb.append(")\n");

		for (VarDecl var : mainClass.varList) {
			sb.append(var.accept(this) + "\n");
		}

		for (Statement stat : mainClass.statList) {
			sb.append(stat.accept(this) + "\n");
		}
		sb.append(")");
		return sb.toString();
	}

	@Override
	public String visit(Program program)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(program.mClass.accept(this) + "\n");

		for (ClassDecl klass : program.classList) {
			sb.append(klass.accept(this) + "\n");
		}

		return sb.toString();
	}

	@Override
	public String visit(Identifier identifier)
	{
		return "ID(" + identifier.varID + ")";
	}

	@Override
	public String visit(IndexArray indexArray)
	{
		return "ARRAY-LOOKUP(" + indexArray.array.accept(this) + indexArray.index.accept(this) + ")";
	}

	@Override
	public String visit(ArrayAssign arrayAssign)
	{
		return "(EQSIGN " + "(ARRAY-ASSIGN " + arrayAssign.identifier.accept(this) + arrayAssign.e1.accept(this) + ") " + arrayAssign.e2.accept(this) + ")";
	}

	@Override
	public String visit(StringLiteral stringLiteral)
	{
		return "STRING(" + stringLiteral.value + ")";
	}

	@Override
	public String visit(ClassDeclSimple classDeclSimple)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("\nCLASS " + classDeclSimple.id.accept(this) + "\n");
		sb.append(")\n");
		for (VarDecl var : classDeclSimple.varList) {
			sb.append(var.accept(this) + "\n");
		}
		for (FuncDecl method : classDeclSimple.methodList) { //
			sb.append(method.accept(this) + "\n");
		}
		sb.append(")");
		return sb.toString();
	}

	@Override
	public String visit(ClassDeclExtends classDeclExtends)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("CLASS(" + classDeclExtends.id.accept(this));
		sb.append(" EXTENDS " + classDeclExtends.parent.accept(this));
		sb.append("\n");
		incLevel();
		for (VarDecl var : classDeclExtends.varList) {
			sb.append(var.accept(this) + "\n");
		}
		for (FuncDecl method : classDeclExtends.methodList) { //
			sb.append(method.accept(this) + "\n");
		}
		decLevel();
		sb.append(")");
		return sb.toString();
	}

	public static String printFileAst() throws FileNotFoundException, ParseException
	{
		ASTPrinter printer = new ASTPrinter();
		Parser p = new Parser("Empire.k");
		Tree tree = p.parse();
		return printer.visit((Program) tree);
	}

    public static void main(String[] args) throws FileNotFoundException, ParseException
    {
        System.out.println(printFileAst());
    }
}