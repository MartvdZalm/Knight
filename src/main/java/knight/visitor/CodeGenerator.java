package knight.visitor;

import java.io.File;
import java.io.PrintWriter;
import java.io.*;

import knight.ast.*;
import knight.ast.Class;
import knight.semantics.*;
import knight.symbol.SymbolClass;
import knight.symbol.SymbolFunction;
import knight.symbol.SymbolProgram;
import knight.symbol.SymbolVariable;


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

	private int counter = 0; // This will get removed later on.

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

		Binding b = assign.getId().getB();
		int lvIndex = getLocalVarIndex(b);

		if (lvIndex == -1) {
			text.append("movq %rax, " + assign.getId() + "\n");
		} else {
			text.append("movq %rax, -" + (lvIndex * 8) + "(%rbp)\n");
		}

		text.append("movq $0, %rax\n");

		return null;
	}

	@Override
	public String visit(Block block)
	{
		for (int i = 0; i < block.getStatListSize(); i++) {
			block.getStatAt(i).accept(this);
		}

		return null;
	}

	@Override
	public String visit(IfThenElse ifThenElse)
	{
		ifThenElse.getExpr().accept(this);
		ifThenElse.getThen().accept(this);
		text.append("jmp end\n");
		text.append("else:\n");
		ifThenElse.getElze().accept(this);
		text.append("end:\n");

		return null;
	}

	@Override
	public String visit(Skip skip)
	{
		return null;
	}

	@Override
	public String visit(While w)
	{
		text.append("while: \n");
		w.getBody().accept(this);
		w.getExpr().accept(this);

		return null;
	}

	@Override
	public String visit(IntLiteral intLiteral)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(intLiteral.getValue());

		return sb.toString();
	}

	@Override
	public String visit(Plus plus)
	{
		StringBuilder sb = new StringBuilder();

		text.append("movq " + plus.getLhs().accept(this) + ", %rax" + "\n");
		text.append("addq " + plus.getRhs().accept(this) + ", %rax" + "\n");

		return sb.toString();
	}

	@Override
	public String visit(Minus minus)
	{	
		StringBuilder sb = new StringBuilder();

		text.append("movq " + minus.getLhs().accept(this) + ", %rax" + "\n");
		text.append("subq " + minus.getRhs().accept(this) + ", %rax" + "\n");

		return sb.toString();
	}

	@Override
	public String visit(Times times)
	{
		StringBuilder sb = new StringBuilder();

		text.append("movq " + times.getLhs().accept(this) + ", %rax" + "\n");
		text.append("imulq " + times.getRhs().accept(this) + ", %rax" + "\n");

		return sb.toString();
	}

	@Override
	public String visit(Division division)
	{
		StringBuilder sb = new StringBuilder();

		text.append("movq " + division.getLhs().accept(this) + ", %rax\n");
		text.append("idivq " + division.getRhs().accept(this) + "\n");

		return sb.toString();
	}

	@Override
	public String visit(Equals equals)
	{
		text.append("movl " + equals.getLhs() + ", %eax\n");
		text.append("cmpl $" + equals.getRhs().accept(this) + ", %eax\n");
		text.append("jne else\n");

		return null;
	}

	@Override
	public String visit(LessThan lessThan)
	{
		Expression lhsExpr = lessThan.getLhs();

		if (lhsExpr instanceof IdentifierExpr) {
			IdentifierExpr lhs = (IdentifierExpr) lhsExpr;
			Binding b = lhs.getB();
			int lvIndex = getLocalVarIndex(b);

			text.append("movl -" + (lvIndex * 8) + "(%rbp), %eax\n");
		} else {
			text.append("movl " + lessThan.getLhs() + ", %eax\n");
		}

		text.append("cmpl $" + lessThan.getRhs().accept(this) + ", %eax\n");
		text.append("jl while\n");

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
		StringBuilder sb = new StringBuilder();

		Binding b = id.getB();
		int lvIndex = getLocalVarIndex(b);

		sb.append("-" + lvIndex * 8 + "(%rbp)");

		return sb.toString();
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
	public String visit(CallFunctionExpr callFunctionExpr)
	{
		StringBuilder sb = new StringBuilder();

		for (int i = callFunctionExpr.getArgExprListSize() - 1; i >= 6; i--) {
			String arg = "$" + callFunctionExpr.getArgExprAt(i).accept(this);
			text.append("pushq " + arg + "\n");
		}

		for (int i = Math.min(callFunctionExpr.getArgExprListSize(), 6) - 1; i >= 0; i--) {
			String arg = "$" + callFunctionExpr.getArgExprAt(i).accept(this);
			text.append("movq " + arg + ", %" + getArgumentRegister(i) + "\n");
		}

		text.append("call " + callFunctionExpr.getMethodId() + "\n");

		if (callFunctionExpr.getArgExprListSize() > 6) {
			int stackCleanup = (callFunctionExpr.getArgExprListSize() - 6) * 8;
			text.append("addq $" + stackCleanup + ", %rsp\n");
		}

		return sb.toString();
	}

	@Override
	public String visit(CallFunctionStat callFunctionStat)
	{
		StringBuilder sb = new StringBuilder();

		for (int i = callFunctionStat.getArgExprListSize() - 1; i >= 6; i--) {
			String arg = "$" + callFunctionStat.getArgExprAt(i).accept(this);
			text.append("pushq " + arg + "\n");
		}

		for (int i = Math.min(callFunctionStat.getArgExprListSize(), 6) - 1; i >= 0; i--) {
			String arg = "$" + callFunctionStat.getArgExprAt(i).accept(this);
			text.append("movq " + arg + ", %" + getArgumentRegister(i) + "\n");
		}

		text.append("call " + callFunctionStat.getMethodId() + "\n");

		if (callFunctionStat.getArgExprListSize() > 6) {
			int stackCleanup = (callFunctionStat.getArgExprListSize() - 6) * 8;
			text.append("addq $" + stackCleanup + ", %rsp\n");
		}

		return sb.toString();

		// for (int i = callFunctionStat.getArgExprListSize() - 1; i >= 0; i--) {

		// 	Expression expr = callFunctionStat.getArgExprAt(i);

		// 	if (expr instanceof StringLiteral) {
		// 		data.append(".LC" + counter + ":\n");
		// 		data.append(".string " + expr.accept(this) + "\n");
		// 		// text.append("push .LC" + counter + "(%rip)\n");
		// 		text.append("leaq .LC" + counter + ", %r9\n");
		// 		counter++;
		// 	} else if (expr instanceof IdentifierExpr) {
		// 		text.append("leaq " + expr.accept(this) + ", %r9\n");
		// 	}
		// }
		// text.append("call " + callFunctionStat.getMethodId() + "\n");
		// text.append("movq $0, %r9\n");

		// return sb.toString();
	}

	private String getArgumentRegister(int index)
	{
	    switch (index) {
	        case 0: return "rdi";
	        case 1: return "rsi";
	        case 2: return "rdx";
	        case 3: return "rcx";
	        case 4: return "r8";
	        case 5: return "r9";
	        default: throw new IllegalArgumentException("Unsupported argument index: " + index);
	    }
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
		Binding b = argDecl.getId().getB();
		setLocalVarIndex(b);

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
		bss.append(".lcomm " + variable.getId() + " 4\n");

		return null;
	}

	@Override
	public String visit(VariableInit varDeclInit)
	{
		StringBuilder sb = new StringBuilder();

		if (currentFunction == null) {
			data.append(varDeclInit.getId() + ":\n");

			Expression expr = varDeclInit.getExpr();

			data.append(expr.type() + " " + varDeclInit.getExpr().accept(this) + "\n");
			
			if (expr instanceof Plus) {
				text.append("movq %rax, " + varDeclInit.getId() + "\n");
			}
		} else {
			Binding b = varDeclInit.getId().getB();
			setLocalVarIndex(b);

			int lvIndex = getLocalVarIndex(b);

			// Need to be changed
			if (varDeclInit.getExpr() instanceof CallFunctionExpr) {
				varDeclInit.getExpr().accept(this);
				text.append("movq %rax" + ", -" + (lvIndex * 8) + "(%rbp)\n");
			} else {
				text.append("movq $" + varDeclInit.getExpr().accept(this) + ", -" + (lvIndex * 8) + "(%rbp)\n");
			}
		}

		return sb.toString();
	}

	@Override
	public String visit(Function functionVoid)
	{
		return null;
	}

	// @Override
	// public String visit(FunctionReturn functionReturn)
	// {	
	// 	slot = 0;

	// 	currentFunction = (SymbolFunction) functionReturn.getId().getB();
	// 	text.append(".globl " + functionReturn.getId() + "\n");
	// 	text.append(".type " + functionReturn.getId() + ", @function\n");
	// 	text.append(functionReturn.getId() + ":\n");
	// 	text.append("pushq %rbp\n");
	// 	text.append("movq %rsp, %rbp\n");

	// 	for (int i = 0; i < functionReturn.getArgumentListSize(); i++) {
	// 		functionReturn.getArgumentDeclAt(i).accept(this);
	// 	}

	// 	for (int i = 0; i < functionReturn.getVariableListSize(); i++) {
	// 		functionReturn.getVariableDeclAt(i).accept(this);
	// 	}

	// 	for (int i = 0; i < functionReturn.getStatementListSize(); i++) {
	// 		functionReturn.getStatementDeclAt(i).accept(this);
	// 	}

	// 	if (functionReturn.getId().getVarID().equals("main")) {
	// 		text.append("movq $60, %rax\n");
	// 		text.append("movq $" + functionReturn.getReturnExpr().accept(this) + ", %rdi\n");
	// 		text.append("syscall\n");
	// 	} else {
	// 		functionReturn.getReturnExpr().accept(this);

	// 		text.append("movq %rbp, %rsp\n");
	// 		text.append("pop %rbp\n");
	// 		text.append("ret\n");
	// 		currentFunction = null;
	// 	}

	// 	return null;
	// }

	@Override
	public String visit(FunctionReturn functionReturn)
	{
	    slot = 0;

	    currentFunction = (SymbolFunction) functionReturn.getId().getB();
	    text.append(".globl " + functionReturn.getId() + "\n");
	    text.append(".type " + functionReturn.getId() + ", @function\n");
	    text.append(functionReturn.getId() + ":\n");
	    text.append("pushq %rbp\n");
	    text.append("movq %rsp, %rbp\n");

	    for (int i = 0; i < Math.min(functionReturn.getArgumentListSize(), 6); i++) {
	    	functionReturn.getArgumentDeclAt(i).accept(this);
	        text.append("movq %" + getArgumentRegister(i) + ", -" + getLocalVariableReference(i) + "\n");
	    }

	    for (int i = 6; i < functionReturn.getArgumentListSize(); i++) {
	    	functionReturn.getArgumentDeclAt(i).accept(this);
	        text.append("movq " + (i - 6) * 8 + "(%rbp), -" + getLocalVariableReference(i) + "\n");
	    }

	    for (int i = 0; i < functionReturn.getVariableListSize(); i++) {
	        functionReturn.getVariableDeclAt(i).accept(this);
	    }

	    for (int i = 0; i < functionReturn.getStatementListSize(); i++) {
	        functionReturn.getStatementDeclAt(i).accept(this);
	    }

	    if (functionReturn.getId().getVarID().equals("main")) {
	        text.append("movq $60, %rax\n");
	        text.append("movq $" + functionReturn.getReturnExpr().accept(this) + ", %rdi\n");
	        text.append("syscall\n");
	    } else {
	    	if (functionReturn.getReturnExpr() instanceof IdentifierExpr) {
	    		text.append("movq " + functionReturn.getReturnExpr().accept(this) + ", %rax" + "\n");
	    	} else {
	    		functionReturn.getReturnExpr().accept(this);
	    	}
	    	
	        text.append("movq %rbp, %rsp\n");
	        text.append("pop %rbp\n");
	        text.append("ret\n");
	        currentFunction = null;
	    }

	    return null;
	}

	private String getLocalVariableReference(int index)
	{
	    return ((index + 1) * 8) + "(%rbp)";
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

		sb.append(".file \"" + FILENAME + ".knight\"\n");
		data.append(".section .data\n");
		bss.append(".section .bss\n");
		text.append(".section .text\n");

		for (int i = 0; i < program.getVariableListSize(); i++) {
			program.getVariableDeclAt(i).accept(this);
		}

		for (int i = 0; i < program.getFunctionListSize(); i++) {
			program.getFunctionDeclAt(i).accept(this);
		}

		sb.append(data).append(bss).append(text);

		// sb.append(".globl length\n");
		// sb.append(".type length, @function\n");
		// sb.append("length:\n");
		// sb.append("pushq %rbp\n");
		// sb.append("pushq %rax\n");
		// sb.append("movq %rsp, %rbp\n");
		// sb.append("xor %rcx, %rcx\n");
		// sb.append("movq 16(%rbp), %rax\n");
		// sb.append("loop:\n");
		// sb.append("movb (%rax), %dl\n");
		// sb.append("test %dl, %dl\n");
		// sb.append("jz end_loop\n");
		// sb.append("inc %rcx\n");
		// sb.append("inc %rax\n");
		// sb.append("jmp loop\n");
		// sb.append("end_loop:\n");
		// sb.append("popq %rax\n");
		// sb.append("popq %rbp\n");
		// sb.append("ret\n");

		// sb.append(".globl print\n");
		// sb.append(".type print, @function\n");
		// sb.append("print:\n");
		// sb.append("pushq %rbp\n");
		// sb.append("movq %rsp, %rbp\n");
		// sb.append("movq $1, %rax\n");
		// sb.append("pushq %r9\n");
		// sb.append("call length\n");
		// sb.append("movq %r9, %rsi\n");
		// sb.append("movq %rcx, %rdx\n");
		// sb.append("syscall\n");
		// sb.append("movq %rbp, %rsp\n");
		// sb.append("pop %rbp\n");
		// sb.append("ret\n");

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

	@Override
	public String visit(ForLoop forLoop)
	{
		return null;
	}
}
