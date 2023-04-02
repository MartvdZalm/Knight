package ast;

import lexer.Token;

public abstract class ClassDecl extends Tree
{
	public ClassDecl(Token token) {
		super(token);
	}
}