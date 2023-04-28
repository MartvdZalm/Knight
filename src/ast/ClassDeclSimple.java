package src.ast;

import java.util.List;

import src.lexer.Token;

public class ClassDeclSimple extends ClassDecl
{
	IdentifierExpr id;
	List<Declaration> varList;
	List<FuncDecl> methodList;

	public ClassDeclSimple(Token jSymbol, IdentifierExpr className, List<Declaration> varList, List<FuncDecl> methodList) {
		super(jSymbol);
		this.id = className;
		this.varList = varList;
		this.methodList = methodList;
	}

	public IdentifierExpr getId() {
		return id;
	}

	public void setId(IdentifierExpr id) {
		this.id = id;
	}

	public int getMethodListSize() {
		return methodList.size();
	}

	public FuncDecl getMethodDeclAt(int index) {
		if (index < methodList.size()) {
			return methodList.get(index);
		}
		return null;
	}

	public int getVarListSize() {
		return varList.size();
	}

	public Declaration getVarDeclAt(int index) {
		if (index < varList.size()) {
			return varList.get(index);
		}
		return null;
	}

	@Override
	public <R> R accept(Visitor<R> v) {
		return v.visit(this);
	}

}