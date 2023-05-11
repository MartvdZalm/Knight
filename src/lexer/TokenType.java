package src.lexer;

public class TokenType
{
    public TokenType()
    {
        Symbol.symbol("(", Tokens.LEFTPAREN);
        Symbol.symbol(")", Tokens.RIGHTPAREN);
        
        Symbol.symbol("{", Tokens.LEFTBRACE);
        Symbol.symbol("}", Tokens.RIGHTBRACE);
        
        Symbol.symbol(",", Tokens.COMMA);
        Symbol.symbol(".", Tokens.DOT);
        Symbol.symbol(":", Tokens.COLON);
        Symbol.symbol(";", Tokens.SEMICOLON);
        Symbol.symbol("=", Tokens.ASSIGN);
        Symbol.symbol("==", Tokens.EQUALS);
        Symbol.symbol("+", Tokens.PLUS);
        Symbol.symbol("-", Tokens.MINUS);
        Symbol.symbol("*", Tokens.TIMES);
        Symbol.symbol("/", Tokens.DIV);
        Symbol.symbol("<", Tokens.LESSTHAN);
        Symbol.symbol(">", Tokens.GREATERTHAN);
        
        Symbol.symbol("and", Tokens.AND);
        Symbol.symbol("or", Tokens.OR);
        
        Symbol.symbol("class", Tokens.CLASS);
        Symbol.symbol("extends", Tokens.EXTENDS);
        Symbol.symbol("if", Tokens.IF);
        Symbol.symbol("else", Tokens.ELSE);
        Symbol.symbol("include", Tokens.INCLUDE);
        Symbol.symbol("main", Tokens.MAIN);
        Symbol.symbol("new", Tokens.NEW);
        Symbol.symbol("print", Tokens.PRINT);
        Symbol.symbol("println", Tokens.PRINTLN);

        Symbol.symbol("return", Tokens.RETURN);
        Symbol.symbol("this", Tokens.THIS);
        Symbol.symbol("void", Tokens.VOID);
        Symbol.symbol("while", Tokens.WHILE);

        Symbol.symbol("protected", Tokens.PROTECTED);
        Symbol.symbol("public", Tokens.PUBLIC);
        Symbol.symbol("private", Tokens.PRIVATE);
        
        Symbol.symbol("bool", Tokens.BOOLEAN);
        Symbol.symbol("true", Tokens.TRUE);
        Symbol.symbol("false", Tokens.FALSE);
        
        Symbol.symbol("int", Tokens.INTEGER);
        Symbol.symbol("string", Tokens.STRING);

        Symbol.symbol("Length", Tokens.LENGTH);
        Symbol.symbol("IDENTIFIER", Tokens.IDENTIFIER);
        Symbol.symbol("INVALID", Tokens.INVALID);
        Symbol.symbol("SENTINEL", Tokens.SENTINEL);
    }
}