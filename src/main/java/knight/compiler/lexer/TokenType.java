/*
 * MIT License
 * 
 * Copyright (c) 2023, Mart van der Zalm
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package knight.compiler.lexer;

/*
 * File: TokenType.java
 * @author: Mart van der Zalm
 * Date: 2024-01-06
 * Description:
 */
public class TokenType
{
    public TokenType()
    {
        // These two can be removed I think. I will do this later when I am done testing.
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

        Symbol.symbol("id", Tokens.IDENTIFIER);
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
        Symbol.symbol("fn", Tokens.FUNCTION);
        Symbol.symbol("ext", Tokens.EXTENDS);
        Symbol.symbol("use", Tokens.IMPLEMENTS);
        Symbol.symbol("asm", Tokens.ASM);

        Symbol.symbol("if", Tokens.IF);
        Symbol.symbol("else", Tokens.ELSE);
        Symbol.symbol("while", Tokens.WHILE);
        Symbol.symbol("for", Tokens.FOR);
        
        Symbol.symbol("=", Tokens.ASSIGN);
        Symbol.symbol("==", Tokens.EQUALS);
        Symbol.symbol("&&", Tokens.AND);
        Symbol.symbol("||", Tokens.OR);

        Symbol.symbol("<", Tokens.LESSTHAN);
        Symbol.symbol("<=", Tokens.LESSTHANOREQUAL);
        Symbol.symbol(">", Tokens.GREATERTHAN);
        Symbol.symbol(">=", Tokens.GREATERTHANOREQUAL);

        Symbol.symbol("+", Tokens.PLUS);
        Symbol.symbol("-", Tokens.MINUS);
        Symbol.symbol("*", Tokens.TIMES);
        Symbol.symbol("/", Tokens.DIV);
        Symbol.symbol("%", Tokens.MODULUS);
        
        Symbol.symbol("ret", Tokens.RETURN);
        Symbol.symbol("void", Tokens.VOID);   
    }
}