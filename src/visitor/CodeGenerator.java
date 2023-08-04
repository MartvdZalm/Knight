package src.visitor;

import java.io.File;
import java.io.FileOutputStream;

import src.ast.*;
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
	public String visit(VarDecl vd)
	{
		return null;
	}

	@Override
	public String visit(VarDeclInit vd)
	{
		return null;
	}

	@Override
	public String visit(ArgDecl ad)
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
		String functionName = functionReturn.getFunctionName().getVarID();

		// Set the current function
		Binding bFuncName = functionReturn.getFunctionName().getB();
		currentFunction = (Function) bFuncName;

		int access = Opcodes.ACC_PUBLIC;

		// Set both maxStack and maxLocal to 0
		maxStack = 0;
		maxLocal = 0;

		// Check the access of the function (Public, Private, Protected)
		if (functionReturn.getAccess().getToken() == Tokens.PRIVATE) {
			access = Opcodes.ACC_PRIVATE;
		} else if (functionReturn.getAccess().getToken() == Tokens.PROTECTED) {
			access = Opcodes.ACC_PROTECTED;
		}
	
		// Check if function is the main function. (This will be changed later on)
		if (functionName.equals("main")) {
			methodVisitor = classWriter.visitMethod(access + Opcodes.ACC_STATIC, functionName, "([Ljava/lang/String;)V", null, null);

			// Start the class code definition
			methodVisitor.visitCode();

		} else {
			StringBuilder sb = new StringBuilder();

			sb.append("(");
			for (int i = 0; i < functionReturn.getArgListSize(); i++) {
				String type = functionReturn.getArgDeclAt(i).getType().toString();
				sb.append(type);

				Binding bFuncArg = functionReturn.getArgDeclAt(i).getId().getB();
				setLocalVarIndex(bFuncArg);

				maxLocal++;
			}
			sb.append(")");

			String returnType = functionReturn.getReturnType().toString();
			sb.append(returnType);

			String methodDescriptor = sb.toString();

			methodVisitor = classWriter.visitMethod(access, functionName, methodDescriptor, null, null);

			// Start the class code definition
			methodVisitor.visitCode();
		}

		// Loop through all the variables in the function 
		for (int i = 0; i < functionReturn.getVarListSize(); i++) {
			functionReturn.getVarDeclAt(i).accept(this);
			maxLocal++;
		}

		// Loop through all the statements in the function
		for (int i = 0; i < functionReturn.getStatListSize(); i++) {
			functionReturn.getStatAt(i).accept(this);
			maxLocal++;
		}

		// Check the return value for example 0 is success and 1 is with error. Only for main method.
		if (functionName.equals("main")) {
			int value = ((IntLiteral) functionReturn.getReturnExpr()).getValue();
			int exit = 0;

			switch (value) {
				case 0:	exit = Opcodes.ICONST_0; break;
				case 1:	exit = Opcodes.ICONST_1; break;
			}

			methodVisitor.visitInsn(exit); maxLocal++;
			methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "exit", "(I)V", false); maxStack++;
			methodVisitor.visitInsn(Opcodes.RETURN);
		} else {
			functionReturn.getReturnExpr().accept(this);

			// Return the result of the multiplication
			methodVisitor.visitInsn(Opcodes.IRETURN);
		}

		methodVisitor.visitMaxs(maxStack, maxLocal);

		// End the class definition
		methodVisitor.visitEnd();

		return null;
	}

	@Override
	public String visit(ClassDeclSimple classDecl)
	{
		Binding b = classDecl.getId().getB();
		currentClass = (Klass) b;

		classWriter = new ClassWriter(0);

		// Define the class header.
		classWriter.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER, currentClass.getId(), null, "java/lang/Object", null);
		
		// Loop through all the declarations like functions, variables, etc.
		for (int i = 0; i < classDecl.getDeclListSize(); i++) {
			classDecl.getDeclAt(i).accept(this);
		}

		// End the class definition
		classWriter.visitEnd();

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

	/*
	 * Entry of the code generation
	 */
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
}