package src.lexer;

import java.io.IOException;

public class Lexer 
{
    private boolean exception;
    private char charachter; 
    private SourceReader source;

    public Lexer(String sourceFile)  
    {
        new TokenType();
        try {
            source = new SourceReader(sourceFile);
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
                charachter = source.read(); // consume the first single quote
                while (charachter != '\"') {
                    str.append(charachter);
                    charachter = source.read();
                }
                charachter = source.read(); // consume the second single quote
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