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
	public static final String LABEL = "Label";


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
		return "" + n.getValue();
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
	public String visit(FunctionExprReturn funcExprReturn)
	{
		return null;
	}

	@Override
	public String visit(FunctionExprVoid funcExprVoid)
	{
		return null;
	}


	@Override
	public String visit(Program program)
	{
		String code = "";

		for (int i = 0; i < program.getIncludeListSize(); i++) {
			code += program.getIncludeAt(i).accept(this);
		}

		for (int i = 0; i < program.getClassListSize(); i++) {
			code += program.getClassDeclAt(i).accept(this);
			write(currClass.getId(), code);
		}

		return null;
	}

	private File write(String name, String code)
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
	public String visit(ArrayInitializerExpr aie)
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
	public String visit(ClassDeclSimple cd)
	{
		Binding b = cd.getId().getB();
		currClass = (Klass) b;

		StringBuilder sb = new StringBuilder();

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
		return include.getId() + "\n";
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