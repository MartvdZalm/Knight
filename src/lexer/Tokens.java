package src.lexer;

public enum Tokens 
{
    INVALID,
    SENTINEL,

    LEFTPAREN, 
    RIGHTPAREN,
    LEFTBRACE, 
    RIGHTBRACE, 

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
    EXTENDS,
    MAIN,
    COMMENT,
    THIS,
    NEW,
    INCLUDE,

    IF,
    ELSE,
    WHILE, 

    ASSIGN,
    EQUALS,  
    OR,
    AND, 
    LESSTHAN,
    GREATERTHAN,
    PLUS,
    MINUS,
    TIMES,
    DIV,
    
    RETURN, 
    VOID,
}
