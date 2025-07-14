package knight.compiler.lexer;

import java.io.BufferedReader;
import java.io.IOException;

public class Lexer
{
	private static final int PEEK_BUFFER_SIZE = 1024;
	private final SourceReader source;
	private char currentChar;
	private boolean exceptionOccurred = false;

	public Lexer(BufferedReader bufferedReader)
	{
		TokenType.initialize();
		source = new SourceReader(bufferedReader);

		try {
			currentChar = source.read();
		} catch (IOException e) {
			exceptionOccurred = true;
			throw new LexerException("Failed to initialize lexer", e);
		}
	}

	public Token peekToken()
	{
		try {
			char tempChar = currentChar;
			source.mark(PEEK_BUFFER_SIZE);
			Token token = nextToken();
			source.reset();
			currentChar = tempChar;
			return token;
		} catch (IOException e) {
			exceptionOccurred = true;
			throw new LexerException("Failed to peek token", e);
		}
	}

	public Token nextToken()
	{
		if (exceptionOccurred) {
			return null;
		}

		try {
			if (currentChar == SourceReader.EOF) {
				return new Token(Symbol.symbol("EOF", Tokens.EOF), source.getRow(), source.getCol());
			}

			while (Character.isWhitespace(currentChar)) {
				currentChar = source.read();
			}

			if (Character.isJavaIdentifierStart(currentChar)) {
				return processIdentifier();
			}

			if (Character.isDigit(currentChar)) {
				return processNumber();
			}

			if (currentChar == '\"') {
				return processString();
			}

			return processOperator();
		} catch (IOException e) {
			exceptionOccurred = true;
			throw new LexerException("Failed to read next token", e);
		}
	}

	private Token processIdentifier() throws IOException
	{
		int startRow = source.getRow();
		int endCol = source.getCol();

		StringBuilder sb = new StringBuilder();
		sb.append(currentChar);

		while (true) {
			currentChar = source.read();
			if (!Character.isJavaIdentifierPart(currentChar)) {
				break;
			}
			sb.append(currentChar);
			endCol++;
		}

		return new Token(Symbol.symbol(sb.toString(), Tokens.IDENTIFIER), startRow, endCol);
	}

	private Token processNumber() throws IOException
	{
		int startRow = source.getRow();
		int endCol = source.getCol();

		StringBuilder sb = new StringBuilder();
		sb.append(currentChar);

		while (true) {
			currentChar = source.read();
			if (!Character.isDigit(currentChar)) {
				break;
			}
			sb.append(currentChar);
			endCol++;
		}

		return new Token(Symbol.symbol(sb.toString(), Tokens.INTEGER), startRow, endCol);
	}

	private Token processString() throws IOException
	{
		int startRow = source.getRow();
		int endCol = source.getCol();

		StringBuilder sb = new StringBuilder();
		currentChar = source.read();
		endCol++;

		boolean escapeNext = false;

		while (true) {
			if (currentChar == '\\' && !escapeNext) {
				escapeNext = true;
				sb.append('\\');
			} else if (currentChar == '\"' && !escapeNext) {
				break;
			} else {
				sb.append(currentChar);
				escapeNext = false;
			}

			currentChar = source.read();
			endCol++;
		}

		currentChar = source.read();
		endCol++;

		return new Token(Symbol.symbol("\"" + sb.toString() + "\"", Tokens.STRING), startRow, endCol - 1);
	}

	private Token processOperator() throws IOException
	{
		String singleChar = String.valueOf(currentChar);
		currentChar = source.read();

		if (currentChar != SourceReader.EOF) {
			String twoChars = singleChar + currentChar;
			Symbol twoCharSymbol = Symbol.symbol(twoChars, Tokens.INVALID);

			if (twoCharSymbol != null) {
				currentChar = source.read();
				return new Token(twoCharSymbol, source.getRow(), source.getCol());
			}
		}

		Symbol singleCharSymbol = Symbol.symbol(singleChar, Tokens.INVALID);
		if (singleCharSymbol == null) {
			exceptionOccurred = true;
			throw new LexerException("Invalid operator: " + singleChar);
		}

		return new Token(singleCharSymbol, source.getRow(), source.getCol());
	}
}
