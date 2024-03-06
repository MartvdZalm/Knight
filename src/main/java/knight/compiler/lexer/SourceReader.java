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

import java.io.*;

/*
 * File: SourceReader.java
 * @author: Mart van der Zalm
 * Date: 2024-01-06
 * Description:
 */
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
