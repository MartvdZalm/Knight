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
    private SourceReaderProperties props;
    private SourceReaderProperties savedProps;

    public SourceReader(BufferedReader bufferedReader) 
    {
        source = bufferedReader;
        props = new SourceReaderProperties();
    }

    public void mark(int index) throws IOException
    {
        source.mark(index);
        this.savedProps = new SourceReaderProperties(props);
    }

    public void reset() throws IOException
    {
        source.reset();
        this.props = new SourceReaderProperties(this.savedProps);
    }

    public char read() throws IOException
    {
        if (props.isPriorEndLine) {
            props.lineNumber++;
            props.positionLastChar = -1;
            props.nextLine = source.readLine();
            props.isPriorEndLine = false;
        }

        if (props.nextLine == null) {  
            throw new IOException();
        } else if (props.nextLine.length() == 0) {
            props.isPriorEndLine = true;
            return ' ';
        }

        props.positionLastChar++;
        if (props.positionLastChar >= props.nextLine.length()) {
            props.isPriorEndLine = true;
            return ' ';
        }
        return props.nextLine.charAt(props.positionLastChar);
    }

    public int getCol() 
    {
        return props.positionLastChar;
    }

    public int getRow() 
    {
        return props.lineNumber;
    }

    public void close() {
        try {
            source.close();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }
}

class SourceReaderProperties
{
    public int lineNumber = 0;
    public int positionLastChar;
    public boolean isPriorEndLine = true;
    public String nextLine;

    public SourceReaderProperties() {}

    public SourceReaderProperties(SourceReaderProperties props)
    {
        this.lineNumber = props.lineNumber;
        this.positionLastChar = props.positionLastChar;
        this.isPriorEndLine = props.isPriorEndLine;
        this.nextLine = props.nextLine;
    }
}
