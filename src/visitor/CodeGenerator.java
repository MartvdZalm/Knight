package src.visitor;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;

import src.ast.*;
import src.semantics.*;
import src.symbol.*;

public class CodeGenerator implements Visitor<String>
{
	private Klass currClass;
	private Function currMethod;
	private int slot;
	private int labelCount;
	private final String PATH;
	private int bytecode;
	public static final String LABEL = "Label";

	private ArrayList<Procedures> procedures = new ArrayList<>(); 

	public CodeGenerator(String progPath)
	{
		PATH = progPath;
	}

	@Override
	public String visit(Print n)
	{
		StringBuilder sb = new StringBuilder();

		sb.append("mov eax, 4 \n"); // The 'write' Systemcall
		sb.append("mov ebx, 1 \n"); // stdout
		sb.append(n.getExpr().accept(this) + "\n"); // The address from the number to write
		sb.append("mov edx, " + n.getExpr().accept(this).length() + "\n"); // The length of the number
		sb.append("int 0x80 \n");

		procedures.add(Procedures.PRINT_INT);

		bytecode += 2;
		return sb.toString();
	}

	@Override
	public String visit(Println n)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("getstatic java/lang/System/out Ljava/io/PrintStream;" + "\n");
		sb.append(n.getExpr().accept(this) + "\n");
		sb.append("invokevirtual java/io/PrintStream/println(");
		sb.append(n.getExpr().type().accept(this));
		sb.append(")V" + "\n");

		bytecode += 2;
		return sb.toString();
	}

	@Override
	public String visit(Assign n)
	{
		StringBuilder sb = new StringBuilder();

		Binding b = n.getId().getB();
		int lvIndex = getLocalVarIndex(b);
		if (lvIndex == -1) { // Class Variable
			if (n.getExpr() instanceof NewInstance) {
				sb.append(n.getExpr().accept(this) + "\n");
			} else {
				sb.append(n.getExpr().accept(this) + "\n");
				sb.append("mov [" + n.id + "], eax" + "\n");
			}

			bytecode += 2;
		} else { // Local Variable
			sb.append(n.getExpr().accept(this) + "\n");
			Type t = n.getExpr().type();
			if (t instanceof IntType || t instanceof BooleanType) {
				sb.append("istore " + lvIndex + "\n");
			} else {
				sb.append("astore " + lvIndex + "\n");
			}

			bytecode += 1;
		}
		return sb.toString();
	}

	@Override
	public String visit(Skip n) {
		return "";
	}

	@Override
	public String visit(Block n) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < n.getStatListSize(); i++) {
			Statement stat = n.getStatAt(i);
			sb.append(stat.accept(this) + "\n");
		}
		return sb.toString();
	}

	@Override
	public String visit(IfThenElse n) {

		StringBuilder sb = new StringBuilder();
		String nFalse = LABEL + getNextLabel();
		String nAfter = LABEL + getNextLabel();

		// sb.append("; if statement" + "\n");
		sb.append(n.getExpr().accept(this) + "\n");

		sb.append("ifeq " + nFalse + "\n");
		sb.append(n.getThen().accept(this) + "\n");
		sb.append("goto " + nAfter + "\n");

		sb.append(nFalse + ":" + "\n");
		sb.append(n.getElze().accept(this) + "\n");
		sb.append(nAfter + ":" + "\n");

		bytecode += 4;
		return sb.toString();
	}

	@Override
	public String visit(While n) {

		StringBuilder sb = new StringBuilder();
		String nStart = LABEL + getNextLabel();
		String nStmt = LABEL + getNextLabel();
		sb.append("goto " + nStart + "\n");
		sb.append(nStmt + ":" + "\n");
		sb.append(n.getBody().accept(this) + "\n");
		sb.append(nStart + ":" + "\n");
		sb.append(n.getExpr().accept(this) + "\n");
		sb.append("ifne " + nStmt + "\n");

		bytecode += 4;
		return sb.toString();
	}

	@Override
	public String visit(IntLiteral n) {
		bytecode += 1;
		return "mov ecx, " + n.getValue() + "\n";
	}

	@Override
	public String visit(Plus n) {

		StringBuilder sb = new StringBuilder();
		if (n.type() != null && n.type() instanceof IntType) {
			sb.append(n.getLhs().accept(this) + "\n");
			sb.append(n.getRhs().accept(this) + "\n");
			sb.append("iadd" + "\n");

			bytecode += 1;
		} else {

			sb.append("new java/lang/StringBuilder" + "\n");
			sb.append("dup" + "\n");
			sb.append("invokespecial java/lang/StringBuilder/<init>()V" + "\n");

			sb.append(n.getLhs().accept(this) + "\n");
			sb.append("invokevirtual java/lang/StringBuilder/append(");
			sb.append(n.getLhs().type().accept(this));
			sb.append(")Ljava/lang/StringBuilder;" + "\n");

			sb.append(n.getRhs().accept(this) + "\n");
			sb.append("invokevirtual java/lang/StringBuilder/append(");
			sb.append(n.getRhs().type().accept(this));
			sb.append(")Ljava/lang/StringBuilder;" + "\n");

			sb.append("invokevirtual java/lang/StringBuilder/toString()Ljava/lang/String;");

			bytecode += 6;
		}
		return sb.toString();
	}

	@Override
	public String visit(Minus n) {
		StringBuilder sb = new StringBuilder();
		sb.append(n.getLhs().accept(this) + "\n");
		sb.append(n.getRhs().accept(this) + "\n");
		sb.append("isub" + "\n");

		bytecode += 1;
		return sb.toString();
	}

	@Override
	public String visit(Times n) {
		StringBuilder sb = new StringBuilder();
		sb.append(n.getLhs().accept(this) + "\n");
		sb.append(n.getRhs().accept(this) + "\n");
		sb.append("imul" + "\n");

		bytecode += 1;
		return sb.toString();
	}

	@Override
	public String visit(Division n) {
		StringBuilder sb = new StringBuilder();
		sb.append(n.getLhs().accept(this) + "\n");
		sb.append(n.getRhs().accept(this) + "\n");
		sb.append("idiv" + "\n");

		bytecode += 1;
		return sb.toString();
	}

	@Override
	public String visit(Equals n) {

		Type t = n.type();
		String nEquals = LABEL + getNextLabel();
		String nAfter = LABEL + getNextLabel();

		StringBuilder sb = new StringBuilder();
		sb.append(n.getLhs().accept(this) + "\n");
		sb.append(n.getRhs().accept(this) + "\n");

		if (t instanceof IntType || t instanceof BooleanType) {
			sb.append("if_icmpeq " + nEquals + "\n");
			sb.append("iconst_0" + "\n");
			sb.append("goto " + nAfter + "\n");

			sb.append(nEquals + ":" + "\n");
			sb.append("iconst_1" + "\n");

			sb.append(nAfter + ":" + "\n");

			bytecode += 6;
		} else if (t instanceof StringType || t instanceof IntArrayType || t instanceof IdentifierType) {
			sb.append("if_acmpeq " + nEquals + "\n");
			sb.append("iconst_0" + "\n");
			sb.append("goto " + nAfter + "\n");

			sb.append(nEquals + ":" + "\n");
			sb.append("iconst_1" + "\n");

			sb.append(nAfter + ":" + "\n");

			bytecode += 6;
		}

		return sb.toString();
	}

	@Override
	public String visit(LessThan n) {
		StringBuilder sb = new StringBuilder();
		String lessThan = LABEL + getNextLabel();
		String after = LABEL + getNextLabel();
		sb.append(n.getLhs().accept(this) + "\n");
		sb.append(n.getRhs().accept(this) + "\n");

		sb.append("if_icmplt " + lessThan + "\n");
		sb.append("iconst_0" + "\n");
		sb.append("goto " + after + "\n");

		sb.append(lessThan + ":" + "\n");
		sb.append("iconst_1" + "\n");

		sb.append(after + ":" + "\n");

		bytecode += 6;
		return sb.toString();
	}

	@Override
	public String visit(And n) {
		StringBuilder sb = new StringBuilder();
		String nElze = LABEL + getNextLabel();
		String nAfter = LABEL + getNextLabel();

		sb.append(n.getLhs().accept(this) + "\n");
		sb.append("ifeq " + nElze + "\n");

		sb.append(n.getRhs().accept(this) + "\n");
		sb.append("goto " + nAfter + "\n");

		sb.append(nElze + ":" + "\n");
		sb.append("iconst_0" + "\n");

		sb.append(nAfter + ":" + "\n");

		bytecode += 5;
		return sb.toString();
	}

	@Override
	public String visit(Or n) {
		StringBuilder sb = new StringBuilder();
		String nElze = LABEL + getNextLabel();
		String nAfter = LABEL + getNextLabel();

		sb.append(n.getLhs().accept(this) + "\n");
		sb.append("ifeq " + nElze + "\n");

		sb.append("iconst_1" + "\n");
		sb.append("goto " + nAfter + "\n");

		sb.append(nElze + ":" + "\n");
		sb.append(n.getRhs().accept(this) + "\n");
		sb.append(nAfter + ":" + "\n");

		bytecode += 5;
		return sb.toString();
	}

	@Override
	public String visit(True true1) {
		bytecode += 1;
		return "iconst_1";
	}

	@Override
	public String visit(False false1) {
		bytecode += 1;
		return "iconst_0";
	}

	@Override
	public String visit(IdentifierExpr id) {

		StringBuilder sb = new StringBuilder();

		Binding b = id.getB();
		int lvIndex = getLocalVarIndex(b);

		if (lvIndex == -1) { // Not a local variable
			sb.append(id.getVarID());

			bytecode += 2;
		} else {
			if (b.type() instanceof IntType || b.type() instanceof BooleanType) {
				sb.append("iload " + lvIndex + "\n");
			} else {
				sb.append("aload " + lvIndex + "\n");
			}

			bytecode += 1;
		}

		return sb.toString();
	}

	@Override
	public String visit(This this1) {
		bytecode += 1;
		return "aload_0";
	}

	@Override
	public String visit(NewArray na) {
		StringBuilder sb = new StringBuilder();
		sb.append(na.getArrayLength().accept(this) + "\n");
		sb.append("newarray int" + "\n");
		bytecode += 1;
		return sb.toString();
	}

	@Override
	public String visit(NewInstance ni) {
		StringBuilder sb = new StringBuilder();
		sb.append("new " + ni.getClassName() + "\n");
		sb.append("dup" + "\n");
		sb.append("invokespecial " + ni.getClassName() + "/<init>()V" + "\n");
		bytecode += 3;
		return sb.toString();
	}

	@Override
	public String visit(CallFunc cm) {
		StringBuilder sb = new StringBuilder();
		sb.append(cm.getInstanceName().accept(this) + "\n");
		Type refT = cm.getInstanceName().type();

		for (int i = 0; i < cm.getArgExprListSize(); i++) {
			sb.append(cm.getArgExprAt(i).accept(this) + "\n");
		}
		sb.append("invokevirtual " + refT.toString() + "/" + cm.getMethodId() + "(");
		Function m = (Function) cm.getMethodId().getB();

		for (int i = 0; i < m.getParamsSize(); i++) {
			sb.append(m.getParamAt(i).type().accept(this));
		}
		sb.append(")" + m.type().accept(this));

		bytecode += 1;
		return sb.toString();
	}

	@Override
	public String visit(Length length) {
		StringBuilder sb = new StringBuilder();
		sb.append(length.getArray().accept(this) + "\n");
		sb.append("arraylength" + "\n");

		bytecode += 1;
		return sb.toString();
	}

	@Override
	public String visit(IntType intType) {
		return "I";
	}

	@Override
	public String visit(StringType stringType) {
		return "Ljava/lang/String;";
	}

	@Override
	public String visit(BooleanType booleanType) {
		return "Z";
	}

	@Override
	public String visit(IntArrayType intArrayType)
	{
		return "[I";
	}

	@Override
	public String visit(IdentifierType refT)
	{
		return "L" + refT.getVarID() + ";";
	}

	@Override
	public String visit(VarDecl vd)
	{
		if (currMethod != null) {
			Binding b = vd.getId().getB();
			setLocalVarIndex(b);
			return "";
		} else {
			bytecode += 1;
			return vd.getId() + " dd 0";
		}
	}

	@Override
	public String visit(ArgDecl ad)
	{
		Binding b = ad.getId().getB();
		setLocalVarIndex(b);
		return ad.getType().accept(this);
	}

	@Override
	public String visit(FuncDeclMain funcDeclMain)
	{
		slot = 0;
		StringBuilder sb = new StringBuilder();
		currMethod = (Function) funcDeclMain.getMethodName().getB();

		sb.append("global _start" + "\n");
		sb.append("_start:" + "\n");

		for (int i = 0; i < funcDeclMain.getVarListSize(); i++) {
			VarDecl vd = funcDeclMain.getVarDeclAt(i);
			sb.append(vd.accept(this));
		}

		for (int i = 0; i < funcDeclMain.getStatListSize(); i++) {
			sb.append(funcDeclMain.getStatAt(i).accept(this) + "\n");
		}

		sb.append("mov eax, 1 \n");
		sb.append("xor ebx, ebx \n");
		sb.append("int 0x80 \n");

		currMethod = null;
		bytecode += 5;
		return sb.toString();
	}

	@Override
	public String visit(FuncDeclStandard funcDeclStandard)
	{
		slot = 0;
		int localvars = 1;
		localvars += funcDeclStandard.getArgListSize();
		localvars += funcDeclStandard.getVarListSize();

		StringBuilder sb = new StringBuilder();
		currMethod = (Function) funcDeclStandard.getMethodName().getB();

		sb.append(".method public " + currMethod.getId() + "(");

		for (int i = 0; i < funcDeclStandard.getArgListSize(); i++) {
			ArgDecl ad = funcDeclStandard.getArgDeclAt(i);
			sb.append(ad.accept(this));
		}
		sb.append(")");
		sb.append(currMethod.type().accept(this) + "\n");

		sb.append(".limit locals " + localvars * 4 + "\n");
		sb.append(".limit stack 100" + "\n");

		for (int i = 0; i < funcDeclStandard.getVarListSize(); i++) {
			VarDecl vd = funcDeclStandard.getVarDeclAt(i);
			sb.append(vd.accept(this));
		}

		for (int i = 0; i < funcDeclStandard.getStatListSize(); i++) {
			sb.append(funcDeclStandard.getStatAt(i).accept(this) + "\n");
		}

		sb.append(funcDeclStandard.getReturnExpr().accept(this) + "\n");
		if (currMethod.type() instanceof IntType || currMethod.type() instanceof BooleanType) {
			sb.append("ireturn" + "\n");
		} else {
			sb.append("areturn" + "\n");
		}
		sb.append(".end method" + "\n");

		currMethod = null;

		bytecode += 5;
		return sb.toString();
	}

	@Override
	public String visit(Program program)
	{
		String code;
		
		for (int i = 0; i < program.classList.size(); i++) {
			code = program.classList.get(i).accept(this);
			write(currClass.getId(), code);
		}
		return null;
	}

	private File write(String name, String code) {
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

	@Override
	public String visit(Identifier id) {
		Binding b = id.getB();
		int lvIndex = getLocalVarIndex(b);
		StringBuilder sb = new StringBuilder();
		if (lvIndex == -1) {

			sb.append("aload_0" + "\n");
			sb.append("getfield " + currClass.getId() + "/" + id.getVarID() + " " + b.type().accept(this) + "\n");

			bytecode += 2;
		} else {
			if (b.type() instanceof IntType || b.type() instanceof BooleanType) {
				sb.append("iload " + lvIndex + "\n");
			} else {
				sb.append("aload " + lvIndex + "\n");
			}

			bytecode += 1;
		}
		return sb.toString();
	}

	@Override
	public String visit(IndexArray ia) {
		StringBuilder sb = new StringBuilder();
		sb.append(ia.getArray().accept(this) + "\n");
		sb.append(ia.getIndex().accept(this) + "\n");
		sb.append("iaload" + "\n");

		bytecode += 1;
		return sb.toString();
	}

	@Override
	public String visit(ArrayAssign aa) {
		StringBuilder sb = new StringBuilder();
		sb.append(aa.getIdentifier().accept(this) + "\n");
		sb.append(aa.getE1().accept(this) + "\n");
		sb.append(aa.getE2().accept(this) + "\n");
		sb.append("iastore" + "\n");

		bytecode += 1;
		return sb.toString();
	}

	@Override
	public String visit(StringLiteral stringLiteral) {
		StringBuilder sb = new StringBuilder();

		bytecode += 1;

		sb.append("section .data" + "\n");
		sb.append("msg" + labelCount + ": db " + "\"" + stringLiteral.getValue() + "\"" + ",0" + "\n");
		labelCount++;
		return sb.toString();
	}

	@Override
	public String visit(ClassDeclSimple cd) {

		StringBuilder sb = new StringBuilder(); 
		labelCount = 0;

		Binding b = cd.getId().getB();
		currClass = (Klass) b;
		sb.append("section .data" + "\n");
		for (int i = 0; i < cd.getVarListSize(); i++) {
			VarDecl vd = cd.getVarDeclAt(i);
			sb.append(vd.accept(this) + "\n");
		}

		sb.append("section .text" + "\n");

		for (int i = 0; i < cd.getMethodListSize(); i++) {
			FuncDecl md = cd.getMethodDeclAt(i);
			sb.append(md.accept(this) + "\n");
		}

		// Check if there are any procedures needed.
		// if (procedures.size() > 0) {
		// 	for (int i = 0; i < procedures.size(); i++) {
		// 		if (procedures.get(i) == Procedures.PRINT_INT) {
		// 			sb.append(print_int());
		// 		}
		// 	}
		// }

		return sb.toString();
	}

	private String print_int()
	{
		StringBuilder sb = new StringBuilder();

		sb.append("print_int PROC value:DWORD \n");
		sb.append("push ebp \n");
		sb.append("mov ebp, esp \n");
		sb.append("push dword [value] \n");
		sb.append("push dword format_string\n");
		sb.append("call printf \n");
		sb.append("add esp, 8\n");
		sb.append("pop ebp \n");
		sb.append("ret \n");
		sb.append("format_string db \"%d\\n\", 0\n");
		sb.append("print_int ENDP \n");

		return sb.toString();
	}

	@Override
	public String visit(ClassDeclExtends cd)
	{
		StringBuilder sb = new StringBuilder();
		labelCount = 0;
		Binding b = cd.getId().getB();
		currClass = (Klass) b;
		sb.append("section .text\n");
		sb.append("global _start\n");
		sb.append("_start:\n");
		sb.append("push ebp\n");
		sb.append("mov ebp, esp\n");
	
		// Initialize parent class
		sb.append("call _" + currClass.parent() + "_init\n");
	
		// Initialize instance variables
		for (int i = 0; i < cd.getVarListSize(); i++) {
			VarDecl vd = cd.getVarDeclAt(i);
			sb.append(vd.accept(this) + "\n");
		}
	
		sb.append("mov eax, 0\n");
		sb.append("pop ebp\n");
		sb.append("ret\n");
	
		// Define methods
		for (int i = 0; i < cd.getMethodListSize(); i++) {
			FuncDecl md = cd.getMethodDeclAt(i);
			sb.append(md.accept(this) + "\n");
		}
	
		return sb.toString();
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

	private int getNextLabel()
	{
		return ++labelCount;
	}

	public int getLen()
	{
		return bytecode;
	}
}