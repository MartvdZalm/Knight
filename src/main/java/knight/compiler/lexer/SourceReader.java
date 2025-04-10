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
	public SourceReaderProperties props;
	public SourceReaderProperties savedProps;

	public SourceReader(BufferedReader bufferedReader)
	{
		source = bufferedReader;
		props = new SourceReaderProperties();
	}

	public void mark(int index) throws IOException
	{
		if (index < 0) {
			throw new IllegalArgumentException("Index must not be negative");
		}

		try {
			if (!source.markSupported()) {
				throw new UnsupportedOperationException("Mark operation is not supported");
			}

			source.mark(index);
			this.savedProps = new SourceReaderProperties(props);
		} catch (IOException e) {
			throw new IOException("Error occurred while marking input stream", e);
		}
	}

	public void reset() throws IOException
	{
		try {
			if (!source.markSupported()) {
				throw new UnsupportedOperationException("Reset operation is not supported");
			}

			source.reset();
			this.props = new SourceReaderProperties(this.savedProps);
		} catch (IOException e) {
			throw new IOException("Error occurred while resetting input stream", e);
		}
	}

	public char read() throws IOException
	{
		if (props.isPriorEndLine) {
			props.row++;
			props.col = -1;
			props.line = source.readLine();
			props.isPriorEndLine = false;
		}

		if (props.line == null) {
			throw new IOException(
					"Error occurred when attempting to read from a source where the next line was null or empty");
		} else if (props.line.length() == 0) {
			props.isPriorEndLine = true;
			return ' ';
		}

		props.col++;
		if (props.col >= props.line.length()) {
			props.isPriorEndLine = true;
			return ' ';
		}

		return props.line.charAt(props.col);
	}

	public int getCol()
	{
		return props.col;
	}

	public int getRow()
	{
		return props.row;
	}

	public void close()
	{
		Symbol.symbols.clear();

		try {
			source.close();
		} catch (Exception e) {
			e.getStackTrace();
		}
	}
}

class SourceReaderProperties
{
	public int row = 0;
	public int col;
	public boolean isPriorEndLine = true;
	public String line;

	public SourceReaderProperties()
	{
	}

	public SourceReaderProperties(SourceReaderProperties props)
	{
		this.row = props.row;
		this.col = props.col;
		this.isPriorEndLine = props.isPriorEndLine;
		this.line = props.line;
	}
}
