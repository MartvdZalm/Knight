package src.visitor;

import java.io.File;
import java.io.PrintWriter;
import java.io.FileOutputStream;

import src.ast.*;
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
		return null;
	}

	@Override
	public String visit(ClassDeclSimple classDecl)
	{
		Binding b = classDecl.getId().getB();
		currentClass = (Klass) b;

		classWriter = new ClassWriter(0);

		// Define the class header.
		// V1_8: The class version.
		// ACC_PUBLIC + ACC_SUPER: The class access flags.
		// currentClass.getId(): Class name.
		// "java/lang/object": The internal name of the superclass.
		// null: The names of the interfaces implemented by the class.
		classWriter.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, currentClass.getId(), null, "java/lang/Object", null);
		
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