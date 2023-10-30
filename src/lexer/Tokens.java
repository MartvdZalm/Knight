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

    INCLUDE,
    CLASS,
    ENUMERATION,
    INTERFACE,
    COMMENT,
    NEW,
    FUNCTION,
    EXTENDS,
    IMPLEMENTS,

    IF,
    ELSE,
    WHILE,

    ASSIGN,
    EQUALS,  
    OR,
    AND, 

    LESSTHAN,
    LESSTHANOREQUAL,
    GREATERTHAN,
    GREATERTHANOREQUAL,

    PLUS,
    INCREMENT,
    MINUS,
    TIMES,
    DIV,
    MODULUS,
    
    RETURN, 
    VOID,
}
