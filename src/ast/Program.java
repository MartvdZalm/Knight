package src.ast;

import java.util.List;

import src.lexer.Token;

public class Program extends Tree
{
	public List<ClassDecl> classList;

	public Program(Token token, List<ClassDecl> classList)
	{
		super(token);
		this.classList = classList;
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
