package ast;

import java.util.List;

import lexer.Token;

public class Program extends Tree
{
	public MainClass mClass;
	public List<ClassDecl> classList;

	public Program(Token token, MainClass mClass, List<ClassDecl> classList) {
		super(token);
		this.mClass = mClass;
		this.classList = classList;
	}

	public MainClass getmClass() {
		return mClass;
	}

	public void setmClass(MainClass mClass) {
		this.mClass = mClass;
	}

	public List<ClassDecl> getKlassList() {
		return classList;
	}

	public void setKlassList(List<ClassDecl> classList) {
		this.classList = classList;
	}

	public int getClassListSize() {
		return classList.size();
	}

	public ClassDecl getClassDeclAt(int index) {
		if (index < classList.size()) {
			return classList.get(index);
		}
		return null;
	}

	@Override
	public <R> R accept(Visitor<R> v) {
		return v.visit(this);
	}
}
