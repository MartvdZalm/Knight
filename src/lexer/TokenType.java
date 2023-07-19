package src.lexer;

public class TokenType
{
    public TokenType()
    {
        Symbol.symbol("INVALID", Tokens.INVALID);
        Symbol.symbol("SENTINEL", Tokens.SENTINEL);

        Symbol.symbol("(", Tokens.LEFTPAREN);
        Symbol.symbol(")", Tokens.RIGHTPAREN);
        Symbol.symbol("{", Tokens.LEFTBRACE);
        Symbol.symbol("}", Tokens.RIGHTBRACE);
        Symbol.symbol("[", Tokens.LEFTBRACKET);
        Symbol.symbol("]", Tokens.RIGHTBRACKET);
        
        Symbol.symbol(";", Tokens.SEMICOLON);
        Symbol.symbol(":", Tokens.COLON);
        Symbol.symbol(",", Tokens.COMMA);
        Symbol.symbol(".", Tokens.DOT);
 
        Symbol.symbol("IDENTIFIER", Tokens.IDENTIFIER);
        Symbol.symbol("int", Tokens.INTEGER);
        Symbol.symbol("string", Tokens.STRING);
        Symbol.symbol("bool", Tokens.BOOLEAN);
        Symbol.symbol("true", Tokens.TRUE);
        Symbol.symbol("false", Tokens.FALSE);

        Symbol.symbol("public", Tokens.PUBLIC);
        Symbol.symbol("protected", Tokens.PROTECTED);
        Symbol.symbol("private", Tokens.PRIVATE);

        Symbol.symbol("class", Tokens.CLASS);
        Symbol.symbol("//", Tokens.COMMENT);
        Symbol.symbol("new", Tokens.NEW);
        Symbol.symbol("include", Tokens.INCLUDE);
        Symbol.symbol("func", Tokens.FUNCTION);
                
        Symbol.symbol("if", Tokens.IF);
        Symbol.symbol("else", Tokens.ELSE);
        Symbol.symbol("while", Tokens.WHILE);
        Symbol.symbol("for", Tokens.FOR);
        
        Symbol.symbol("=", Tokens.ASSIGN);
        Symbol.symbol("==", Tokens.EQUALS);
        Symbol.symbol("and", Tokens.AND);
        Symbol.symbol("or", Tokens.OR);
        Symbol.symbol("<", Tokens.LESSTHAN);
        Symbol.symbol(">", Tokens.GREATERTHAN);
        Symbol.symbol("+", Tokens.PLUS);
        Symbol.symbol("-", Tokens.MINUS);
        Symbol.symbol("*", Tokens.TIMES);
        Symbol.symbol("/", Tokens.DIV);
        
        Symbol.symbol("return", Tokens.RETURN);
        Symbol.symbol("void", Tokens.VOID);       
    }
}