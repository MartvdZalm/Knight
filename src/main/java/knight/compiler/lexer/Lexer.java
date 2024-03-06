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

import java.io.IOException;
import java.io.*;

/*
 * File: Lexer.java
 * @author: Mart van der Zalm
 * Date: 2024-01-06
 * Description:
 */
public class Lexer 
{
    private boolean exception;
    private char charachter; 
    private SourceReader source;

    public Lexer(BufferedReader bufferedReader) 
    {
        new TokenType();
        try {
            source = new SourceReader(bufferedReader);
            charachter = source.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Token nextToken()
    { 
        if (exception) {
            if (source != null) {
                source.close();
                source = null;
            }
            return null;
        }

        try {
            while (Character.isWhitespace(charachter)) {  
                charachter = source.read();
            }
        } catch (Exception e) {
            exception = true;
            return nextToken();
        }

        if (Character.isJavaIdentifierStart(charachter)) {
            String identifier = "";
            try {
                do {
                    identifier += charachter;
                    charachter = source.read();
                } while (Character.isJavaIdentifierPart(charachter));
            } catch (Exception e) {
                exception = true;
            }
            return newIdToken(identifier);
        }

        if (Character.isDigit(charachter)) {
            String number = "";
            try {
                do {
                    number += charachter;
                    charachter = source.read();
                } while (Character.isDigit(charachter));
            } catch (Exception e) {
                exception = true;
            }
            return newNumberToken(number);
        }   

        if (charachter == '\"') {
            StringBuilder str = new StringBuilder();
            try {
                charachter = source.read();
                while (charachter != '\"') {
                    str.append(charachter);
                    charachter = source.read();
                }
                charachter = source.read();
            } catch (Exception e) {
                exception = true;
            }
            String token = str.toString().trim(); // remove leading/trailing whitespace

            return newStringToken(token);
        } 

        String charOld = "" + charachter;
        String op = charOld;

        try {
            charachter = source.read();
            op += charachter;
            Symbol symbol = Symbol.symbol(op, Tokens.INVALID);

            if (symbol == null) {  
                return makeToken(charOld);
            }

            charachter = source.read();
            return makeToken(op);

        } catch (Exception e) {
            exception = true;
        }
        return makeToken(op);
    }

    private Token makeToken(String newSymbol) 
    {
        if (newSymbol.equals("//")) {  
            try {
                int oldLine = source.getRow();
                do {
                    charachter = source.read();
                } while (oldLine == source.getRow());
            } catch (Exception e) {
                exception = true;
            }
            return nextToken();
        }

        Symbol symbol = Symbol.symbol(newSymbol, Tokens.INVALID); 
        if (symbol == null) {
            exception = true;
            return nextToken();
        }
        return new Token(symbol, source.getRow(), source.getCol());
    }

    private Token newIdToken(String id) 
    {
        return new Token(Symbol.symbol(id, Tokens.IDENTIFIER), source.getRow(), source.getCol());
    }

    private Token newNumberToken(String number) 
    {
        return new Token(Symbol.symbol(number, Tokens.INTEGER), source.getRow(), source.getCol());
    }

    private Token newStringToken(String str)
    {
        return new Token(Symbol.symbol(str, Tokens.STRING), source.getRow(), source.getCol());
    }
}