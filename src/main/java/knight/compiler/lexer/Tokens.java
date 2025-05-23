package knight.compiler.lexer;

public enum Tokens
{
	INVALID, SENTINEL,

	LEFTPAREN, RIGHTPAREN, LEFTBRACE, RIGHTBRACE, LEFTBRACKET, RIGHTBRACKET,

	SEMICOLON, COLON, DOT, COMMA,

	IDENTIFIER, INTEGER, STRING, BOOLEAN, TRUE, FALSE,

	PUBLIC, PROTECTED, PRIVATE,

	IMPORT, CLASS, COMMENT, NEW, FUNCTION,

	IF, ELSE, WHILE, FOR,

	ASSIGN, EQUALS, NOTEQUALS, OR, AND,

	LESSTHAN, LESSTHANOREQUAL, GREATERTHAN, GREATERTHANOREQUAL,

	PLUS, INCREMENT, MINUS, TIMES, DIV, MODULUS,

	RETURN, VOID,
}
