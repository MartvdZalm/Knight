package src.lexer;

public class TokenType
{
    public TokenType()
    {
        Symbol.symbol("INVALID", Tokens.INVALID);
        Symbol.symbol("class", Tokens.CLASS);
        Symbol.symbol("public", Tokens.PUBLIC);
        Symbol.symbol("private", Tokens.PRIVATE);
        Symbol.symbol("protected", Tokens.PROTECTED);
        Symbol.symbol("include", Tokens.INCLUDE);
        Symbol.symbol("extends", Tokens.EXTENDS);
        Symbol.symbol("void", Tokens.VOID);
        Symbol.symbol("main", Tokens.MAIN);
        Symbol.symbol("//", Tokens.COMMENT);
        Symbol.symbol("print", Tokens.PRINT);
        Symbol.symbol("println", Tokens.PRINTLN);
        Symbol.symbol("SENTINEL", Tokens.SENTINEL);
        Symbol.symbol("IDENTIFIER", Tokens.IDENTIFIER);
        Symbol.symbol("this", Tokens.THIS);
        Symbol.symbol("new", Tokens.NEW);
        Symbol.symbol("int",Tokens.INTEGER);
        Symbol.symbol("string", Tokens.STRING);
        Symbol.symbol(";", Tokens.SEMICOLON);
        Symbol.symbol(":", Tokens.COLON);
        Symbol.symbol("std", Tokens.STD);
        Symbol.symbol("{", Tokens.LEFTBRACE);
        Symbol.symbol("}", Tokens.RIGHTBRACE);
        Symbol.symbol("(", Tokens.LEFTPAREN);
        Symbol.symbol(")", Tokens.RIGHTPAREN);
        Symbol.symbol("if", Tokens.IF);
        Symbol.symbol("else", Tokens.ELSE);
        Symbol.symbol("while", Tokens.WHILE);
        Symbol.symbol(",", Tokens.COMMA);
        Symbol.symbol("=", Tokens.ASSIGN);
        Symbol.symbol("==", Tokens.EQUALS);
        Symbol.symbol("or", Tokens.OR);
        Symbol.symbol("and",Tokens.AND);
        Symbol.symbol("<", Tokens.LESSTHAN);
        Symbol.symbol(">", Tokens.GREATERTHAN);
        Symbol.symbol("+", Tokens.PLUS);
        Symbol.symbol("-", Tokens.MINUS);
        Symbol.symbol("*",Tokens.TIMES);
        Symbol.symbol("/",Tokens.DIV);
        Symbol.symbol(".", Tokens.DOT);
        Symbol.symbol("Length", Tokens.LENGTH);
        Symbol.symbol("return", Tokens.RETURN);
        Symbol.symbol("true", Tokens.TRUE);
        Symbol.symbol("false", Tokens.FALSE);
        Symbol.symbol("bool", Tokens.BOOLEAN);
    }
}