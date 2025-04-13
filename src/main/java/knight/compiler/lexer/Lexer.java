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
	public boolean exception;
	private char charachter;
	public SourceReader source;

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

	public Token peekToken()
	{
		Token token = null;
		try {
			char tempChar = charachter;
			source.mark(100); // needs to be changes!!!
			token = nextToken();
			source.reset();
			charachter = tempChar;
		} catch (Exception e) {
			exception = true;
		}

		return token;
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
			return processIdentifier();
		}

		if (Character.isDigit(charachter)) {
			return processNumber();
		}

		if (charachter == '\"') {
			return processString();
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

	public Token processIdentifier()
	{
		StringBuilder sb = new StringBuilder();

		try {
			do {
				sb.append(charachter);
				charachter = source.read();
			} while (Character.isJavaIdentifierPart(charachter));
		} catch (Exception e) {
			exception = true;
		}

		return new Token(Symbol.symbol(sb.toString(), Tokens.IDENTIFIER), source.getRow(), source.getCol());
	}

	public Token processNumber()
	{
		StringBuilder sb = new StringBuilder();

		try {
			do {
				sb.append(charachter);
				charachter = source.read();
			} while (Character.isDigit(charachter));
		} catch (Exception e) {
			exception = true;
		}

		return new Token(Symbol.symbol(sb.toString(), Tokens.INTEGER), source.getRow(), source.getCol());
	}

	public Token processString()
	{
		StringBuilder sb = new StringBuilder();

		try {
			charachter = source.read();
			while (charachter != '\"') {
				sb.append(charachter);
				charachter = source.read();
			}
			charachter = source.read();
		} catch (Exception e) {
			exception = true;
		}

		return new Token(Symbol.symbol("\"" + sb.toString().trim() + "\"", Tokens.STRING), source.getRow(),
				source.getCol());
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
}