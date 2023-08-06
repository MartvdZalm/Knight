package src.lexer;

public enum Tokens 
{
    INVALID,
    SENTINEL,

    LEFTPAREN, 
    RIGHTPAREN,
    LEFTBRACE, 
    RIGHTBRACE, 
    LEFTBRACKET,
    RIGHTBRACKET,

    SEMICOLON,
    COLON,
    DOT,
    COMMA, 

    IDENTIFIER,
    INTEGER,
    STRING,
    BOOLEAN,
    TRUE,
    FALSE,

    PUBLIC,
    PROTECTED,
    PRIVATE,

    CLASS,
    COMMENT,
    NEW,
    INCLUDE,
    FUNCTION,

    IF,
    ELSE,
    WHILE,
    FOR, 

    ASSIGN,
    EQUALS,  
    OR,
    AND, 

    LESSTHAN,
    LESSTHANOREQUAL,
    GREATERTHAN,
    GREATERTHANOREQUAL,
    PLUS,
    MINUS,
    TIMES,
    DIV,
    
    RETURN, 
    VOID,
}
