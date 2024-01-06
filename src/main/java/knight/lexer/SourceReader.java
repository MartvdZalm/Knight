package knight.lexer;

import java.io.*;

public class SourceReader
{
    private BufferedReader source;
    private int lineNumber = 0;
    private int positionLastChar;
    private boolean isPriorEndLine = true;
    private String nextLine;

    public SourceReader(BufferedReader bufferedReader) 
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
