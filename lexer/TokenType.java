package lexer;

public class TokenType
{
    public TokenType() {
        Symbol.symbol("INVALID", Tokens.INVALID);
        Symbol.symbol("KNIGHT", Tokens.KNIGHT);
        Symbol.symbol("Extends", Tokens.EXTENDS);
        Symbol.symbol("MAIN", Tokens.MAIN);
        Symbol.symbol("//", Tokens.COMMENT);
        Symbol.symbol("print", Tokens.PRINT);
        Symbol.symbol("println", Tokens.PRINTLN);
        Symbol.symbol("SENTINEL", Tokens.SENTINEL);
        Symbol.symbol("IDENTIFIER", Tokens.IDENTIFIER);
        Symbol.symbol("this", Tokens.THIS);
        Symbol.symbol("new", Tokens.NEW);

        Symbol.symbol("int",Tokens.INTEGER);
        Symbol.symbol("string", Tokens.STRING);

        Symbol.symbol(":", Tokens.SEMICOLON);
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
        Symbol.symbol("+", Tokens.PLUS);
        Symbol.symbol("-", Tokens.MINUS);
        Symbol.symbol("*",Tokens.TIMES);
        Symbol.symbol("/",Tokens.DIV);
        Symbol.symbol(".", Tokens.DOT);
        Symbol.symbol("length", Tokens.LENGTH);

        Symbol.symbol("Function", Tokens.FUNCTION);
        Symbol.symbol("return", Tokens.RETURN);

        Symbol.symbol("true", Tokens.TRUE);
        Symbol.symbol("false", Tokens.FALSE);
        Symbol.symbol("bool", Tokens.BOOLEAN);
    }
}