package src.visitor;

import java.io.File;
import java.io.PrintWriter;

import src.ast.*;
import src.semantics.*;
import src.symbol.*;

public class CodeGenerator implements Visitor<String>
{
	private Klass currClass;
	private Function currFunc;
	private int slot;
	private int labelCount;
	private final String PATH;
	private CGContext currentContext;

	private StringBuilder BSS = new StringBuilder();
	private StringBuilder DATA = new StringBuilder();
	private StringBuilder MAIN = new StringBuilder();
	private StringBuilder FUNCTIONS = new StringBuilder();
	private StringBuilder EXIT = new StringBuilder();

	public CodeGenerator(String progPath)
	{
		PATH = progPath;
	}

	@Override
	public String visit(Assign n)
	{
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
	public String visit(ForLoop forLoop)
	{
		return null;
	}

	@Override
	public String visit(IntLiteral n)
	{
		return String.valueOf(n.getValue());
	}

	@Override
	public String visit(Plus n)
	{
		return null;
	}

	@Override
	public String visit(Minus n)
	{
		return null;
	}

	@Override
	public String visit(Times n)
	{
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
	public String visit(CallFunctionStat cm)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(cm.getMethodId() + "\n");
		for (int i = 0; i < cm.getArgExprListSize(); i++) {
			sb.append(cm.getArgExprAt(i).accept(this) + "\n");
		}

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
	public String visit(VarDecl vd)
	{
		BSS.append(vd.getId().getVarID() + ": resb 10\n");

		return null;
	}

	@Override
	public String visit(VarDeclInit vd)
	{
		if (currentContext == CGContext.MAIN) {
			if (vd.getExpr() instanceof CallFunctionExpr) {
				BSS.append(vd.getId().getVarID() + ": resq 10\n");
				MAIN.append("call " + ((CallFunctionExpr) vd.getExpr()).getMethodId() + "\n");
				MAIN.append("mov [" + vd.getId().getVarID() + "], rax");
			}
		} else {
			DATA.append(vd.getId().getVarID() + ": db " + vd.getExpr().accept(this));
			FUNCTIONS.append("mov rax, " + vd.getId().getVarID() + "\n");
		}
		
		return "";
	}

	@Override
	public String visit(ArgDecl ad)
	{
		return null;
	}

	@Override
	public String visit(FunctionVoid functionVoid)
	{
		return null;
	}

	@Override
	public String visit(FunctionReturn functionReturn)
	{	
		if (functionReturn.getFunctionName().getVarID().equals("main")) {
			for (int i = 0; i < functionReturn.getVarListSize(); i++) {
				setContext(CGContext.MAIN);
				MAIN.append(functionReturn.getVarDeclAt(i).accept(this) + "\n");
			}

			MAIN.append("\n");
			MAIN.append("mov rax, 60\n");
			MAIN.append("mov rdi, " + functionReturn.getReturnExpr().accept(this) + "\n");
			MAIN.append("syscall\n");
		} else {
			setContext(CGContext.FUNCTION);
			FUNCTIONS.append(functionReturn.getFunctionName().getVarID() + ":" + "\n");
			FUNCTIONS.append("push rbp" + "\n");
			FUNCTIONS.append("mov rbp, rsp" + "\n");

			for (int i = 0; i < functionReturn.getVarListSize(); i++) {
				DATA.append(functionReturn.getVarDeclAt(i).accept(this) + "\n");
			}

			functionReturn.getReturnExpr().accept(this);

			FUNCTIONS.append("pop rbp" + "\n");
			FUNCTIONS.append("ret" + "\n");
		}

		FUNCTIONS.append("\n");

		return "";
	}

	@Override
	public String visit(Identifier id)
	{
		return id.getVarID();
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
	public String visit(ClassDeclSimple cd)
	{
		Binding b = cd.getId().getB();
		currClass = (Klass) b;

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < cd.getDeclListSize(); i++) {
			cd.getDeclAt(i).accept(this);
		}

		return sb.toString();
	}

	@Override
	public String visit(ClassDeclExtends cd)
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
		StringBuilder code = new StringBuilder();

		code.append("BITS 64 \nCPU X64 \n\n");

		BSS.append("section .bss \n");
		DATA.append("section .data \n");

		for (int i = 0; i < program.getIncludeListSize(); i++) {
			program.getIncludeAt(i).accept(this);
		}

		for (int i = 0; i < program.getClassListSize(); i++) {
			program.getClassDeclAt(i).accept(this);
		}

		code.append(BSS.append("\n\n"));
		code.append(DATA.append("\n\n"));
		code.append("section .text\nglobal _start\n\n");
		code.append(FUNCTIONS.append("\n\n"));
		code.append("_start:\n");
		code.append(MAIN);
		code.append(EXIT.append("\n\n"));

		write(currClass.getId(), code);

		return null;
	}

	private File write(String name, StringBuilder code)
	{
		try {
			File f = new File(PATH + name + ".asm");
			PrintWriter writer = new PrintWriter(f, "UTF-8");
			writer.println(code);
			writer.close();
			return f;
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

		return null;
	}

	private void setContext(CGContext context)
	{
		this.currentContext = context;
	}

	private int getLocalVarIndex(Binding b)
	{
		if (b != null && b instanceof Variable) {
			return ((Variable) b).getLvIndex();
		}
		return -1;
	}

	private int setLocalVarIndex(Binding b)
	{
		if (b != null && b instanceof Variable) {
			((Variable) b).setLvIndex(++slot);
			return slot;
		}
		return -1;
	}
}