package src.visitor;

import java.io.File;
import java.io.PrintWriter;
import java.io.*;

import src.ast.*;
import src.ast.Class;
import src.semantics.*;
import src.symbol.SymbolClass;
import src.symbol.SymbolFunction;
import src.symbol.SymbolProgram;
import src.symbol.SymbolVariable;


public class CodeGenerator implements Visitor<String>
{
	private SymbolClass currentClass;
	private SymbolFunction currentFunction;
	private final String PATH;
	private final String FILENAME;
	
	private int bytes;
	private int slot;
	private int stack;
	private int localStack;

	StringBuilder data = new StringBuilder();
	StringBuilder bss = new StringBuilder();
	StringBuilder text = new StringBuilder();

	public CodeGenerator(String progPath, String filename)
	{
		File f = new File(filename);
		String name = f.getName();

		FILENAME = name.substring(0, name.lastIndexOf("."));
		PATH = progPath;
	}

	@Override
	public String visit(Assign assign)
	{
		assign.getExpr().accept(this);
		text.append("movq %rax, " + assign.getId() + "\n");
		text.append("movq $0, %rax\n");

		return null;
	}

	@Override
	public String visit(Block n)
	{
		return null;
	}

	@Override
	public String visit(IfThenElse n)
	{
		return null;
	}

	@Override
	public String visit(Skip skip)
	{
		return null;
	}

	@Override
	public String visit(While n)
	{
		return null;
	}

	@Override
	public String visit(IntLiteral n)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(n.getValue());

		return sb.toString();
	}

	@Override
	public String visit(Plus plus)
	{
		text.append("movq " + plus.getLhs() + ", %rax\n");
		text.append("addq " + plus.getRhs() + ", %rax\n");

		return null;
	}

	@Override
	public String visit(Minus minus)
	{	
		text.append("movq " + minus.getLhs() + ", %rax\n");
		text.append("subq " + minus.getRhs() + ", %rax\n");

		return null;
	}

	@Override
	public String visit(Times n)
	{
		Binding blh = ((IdentifierExpr) n.getLhs()).getB();
		Binding brh = ((IdentifierExpr) n.getRhs()).getB();
		return null;
	}

	@Override
	public String visit(Division n)
	{
		return null;
	}

	@Override
	public String visit(Equals n)
	{
		return null;
	}

	@Override
	public String visit(LessThan n)
	{
		return null;
	}

	@Override
	public String visit(LessThanOrEqual lessThanOrEqual)
	{
		return null;
	}

	@Override
	public String visit(GreaterThan greaterThan)
	{
		return null;
	}

	@Override
	public String visit(GreaterThanOrEqual greaterThanOrEqual)
	{
		return null;
	}

	@Override
	public String visit(And n)
	{
		return null;
	}

	@Override
	public String visit(Or n)
	{
		return null;
	}

	@Override
	public String visit(True true1)
	{
		return null;
	}

	@Override
	public String visit(False false1)
	{
		return null;
	}

	@Override
	public String visit(IdentifierExpr id)
	{
		return null;
	}

	@Override
	public String visit(NewArray na)
	{
		return null;
	}

	@Override
	public String visit(NewInstance ni)
	{
		return null;
	}

	@Override
	public String visit(CallFunctionExpr cm)
	{
		return null;
	}

	@Override
	public String visit(CallFunctionStat callFunctionStat)
	{
		StringBuilder sb = new StringBuilder();

		sb.append("std::cout << " + callFunctionStat.getArgExprAt(0) + ";\n");

		return sb.toString();
	}

	@Override
	public String visit(IntType intType)
	{
		return null;
	}

	@Override
	public String visit(StringType stringType)
	{	
		return null;
	}

	@Override
	public String visit(VoidType voidType)
	{
		return null;
	}

	@Override
	public String visit(BooleanType booleanType)
	{
		return null;
	}

	@Override
	public String visit(IntArrayType intArrayType)
	{
		return null;
	}

	@Override
	public String visit(IdentifierType refT)
	{
		return null;
	}

	@Override
	public String visit(Argument argDecl)
	{
		return null;
	}

	@Override
	public String visit(Identifier id)
	{
		return null;
	}

	@Override
	public String visit(ArrayIndexExpr ia)
	{
		return null;
	}

	@Override
	public String visit(ArrayAssign aa)
	{
		return null;
	}

	@Override
	public String visit(StringLiteral stringLiteral)
	{
		return "\"" + stringLiteral.getValue() + "\"";
	}

	@Override
	public String visit(Variable variable)
	{
		bss.append(".lcomm " + variable.getId() + " 32\n");

		return null;
	}

	@Override
	public String visit(VariableInit varDeclInit)
	{
		data.append(varDeclInit.getId() + ":\n");

		Expression expr = varDeclInit.getExpr();

		if (expr.type() instanceof IntType) {
			data.append(".int " + varDeclInit.getExpr().accept(this) + "\n");
		}
		else if (expr instanceof Plus) {
			text.append("movq %rax, " + varDeclInit.getId() + "\n");
		}

		return null;
	}

	@Override
	public String visit(Function functionVoid)
	{
		return null;
	}

	@Override
	public String visit(FunctionReturn functionReturn)
	{	
		for (int i = 0; i < functionReturn.getStatementListSize(); i++) {
			functionReturn.getStatementDeclAt(i).accept(this);
		}

		text.append("movq $1, %rax\n");
		text.append("movq $" + functionReturn.getReturnExpr().accept(this) + ", %rbx\n");
		text.append("int $0x80\n");

		return null;
	}

	@Override
	public String visit(Class classDecl)
	{
		return null;
	}

	@Override
	public String visit(Include include)
	{
		return null;
	}

	@Override
	public String visit(Program program)
	{
		StringBuilder sb = new StringBuilder();

		data.append(".section .data\n");
		bss.append(".section .bss\n");
		text.append(".section .text\n");
		text.append(".globl _start\n");
		text.append("_start:\n");

		for (int i = 0; i < program.getVariableListSize(); i++) {
			program.getVariableDeclAt(i).accept(this);
		}

		for (int i = 0; i < program.getFunctionListSize(); i++) {
			program.getFunctionDeclAt(i).accept(this);
		}

		sb.append(data).append(bss).append(text);
		write(sb.toString());

		return null;
	}

	private File write(String code)
	{
		try {
			File f = new File(PATH + FILENAME + ".s");
			PrintWriter writer = new PrintWriter(f, "UTF-8");
			writer.println(code);
			writer.close();
			return f;
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

		return null;
	}
	
	private int getLocalVarIndex(Binding b)
	{
		if (b != null && b instanceof SymbolVariable) {
			return ((SymbolVariable) b).getLvIndex();
		}
		return -1;
	}

	private int setLocalVarIndex(Binding b)
	{
		if (b != null && b instanceof SymbolVariable) {
			((SymbolVariable) b).setLvIndex(++slot);
			return slot;
		}
		return -1;
	}

	@Override
	public String visit(ReturnStatement returnStatement)
	{
		return null;
	}

	@Override
	public String visit(Increment increment)
	{
		return null;
	}

	@Override
	public String visit(Modulus modulus)
	{
		return null;
	}

	@Override
	public String visit(FunctionType functionType)
	{
		return null;
	}

	@Override
	public String visit(Enumeration enumDecl)
	{
		return null;
	}

	@Override
	public String visit(Interface interDecl)
	{
		return null;
	}
}
