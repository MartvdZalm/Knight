package src.visitor;

import java.io.File;
import java.io.FileOutputStream;
import src.ast.*;
import src.lexer.Token;
import src.lexer.Tokens;
import src.semantics.*;
import src.symbol.*;

import org.objectweb.asm.*;

public class CodeGenerator implements Visitor<String>
{
	private Klass currentClass;
	private Function currentFunction;
	private int slot;
	private final String PATH;

	// Variables related to ASM
	private ClassWriter classWriter;
	private MethodVisitor methodVisitor;
	
	private int maxStack;
	private int maxLocal;

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
		// Load the arguments to the stack with the right local variable index
		Binding blh = ((IdentifierExpr) n.getLhs()).getB();
		Binding brh = ((IdentifierExpr) n.getRhs()).getB();

		methodVisitor.visitVarInsn(Opcodes.ILOAD, getLocalVarIndex(blh)); // Load first parameter tot the stack.
		methodVisitor.visitVarInsn(Opcodes.ILOAD, getLocalVarIndex(brh)); // Load second parameter tot the stack.

		methodVisitor.visitInsn(Opcodes.IMUL); // Multiply the top two values on the stack as an int
		maxStack++;

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
		// This is for testing. Will be removed later.
		if (callFunctionStat.getMethodId().getVarID().equals("print")) {
			IdentifierExpr expr = (IdentifierExpr) callFunctionStat.getArgExprAt(0);
			Binding b = expr.getB();
			String descriptor = expr.type().toString();

			// Get the reference to the standard output stream
			methodVisitor.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");

			// Create a new instance of MyClass
			methodVisitor.visitTypeInsn(Opcodes.NEW, currentClass.getId());

			// Duplicate the reference on the stack
			methodVisitor.visitInsn(Opcodes.DUP);

			// Invoke the constructor of MyClass
			methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, currentClass.getId(), "<init>", "()V", false);

			// Get the value of the field 'a'
			methodVisitor.visitFieldInsn(Opcodes.GETFIELD, currentClass.getId(), expr.getVarID(), descriptor);

			// Invoke the println method with the int argument
			methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(" + descriptor + ")V", false);


			// Increment maxStack and maxLocal
			maxStack = Math.max(maxStack, 2); // 2 stack elements required for println
			maxLocal = Math.max(maxLocal, getLocalVarIndex(b) + 1); // 1 local variable required for b
		}

		return null;
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
	public String visit(ArgDecl argDecl)
	{
		Binding b = argDecl.getId().getB();
		setLocalVarIndex(b);
		return argDecl.getType().accept(this);
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
		return null;
	}

	@Override
	public String visit(VarDeclNoInit vd)
	{
		return null;
	}

	@Override
	public String visit(VarDeclInit varDeclInit)
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
		return null;
	}

	@Override
	public String visit(ClassDeclSimple classDecl)
	{
		return null;
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
		for (int i = 0; i < program.getIncludeListSize(); i++) {
			program.getIncludeAt(i).accept(this);
		}

		for (int i = 0; i < program.getClassListSize(); i++) {
			program.getClassDeclAt(i).accept(this);
			write(currentClass.getId(), classWriter.toByteArray());
		}

		return null;
	}

	private File write(String name, byte[] code)
	{
		try {
			File f = new File(PATH + name + ".class");
			FileOutputStream output = new FileOutputStream(f);
			output.write(code);
			output.close();
			return f;
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

		return null;
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
	public String visit(EnumDecl enumDecl) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'visit'");
	}

	@Override
	public String visit(Extends extends1) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'visit'");
	}

	@Override
	public String visit(Implements implements1) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'visit'");
	}
}
