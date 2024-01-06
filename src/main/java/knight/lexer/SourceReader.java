package knight.lexer;

import java.io.*;

public class SourceReader
{
    private BufferedReader source;
    private int lineNumber = 0; // line number of source program
    private int positionLastChar; // position of last character processed
    private boolean isPriorEndLine = true; // if true then last character read was newline
    private String nextLine; // so read in the next line

    public SourceReader(BufferedReader bufferedReader) throws IOException 
    {
        source = bufferedReader;
    }

    public char read() throws IOException
    {
        if (isPriorEndLine) {
            lineNumber++;
            positionLastChar = -1;
            nextLine = source.readLine();
            isPriorEndLine = false;
        }

        if (nextLine == null) {  
            throw new IOException();
        } 
        else if (nextLine.length() == 0) {
            isPriorEndLine = true;
            return ' ';
        }

        positionLastChar++;
        if (positionLastChar >= nextLine.length()) {
            isPriorEndLine = true;
            return ' ';
        }
        return nextLine.charAt(positionLastChar);
    }

    public int getCol() 
    {
        return positionLastChar;
    }

    public int getRow() 
    {
        return lineNumber;
    }

    public void close() {
        try {
            source.close();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }
}
