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

package knight.compiler.semantics;

/*
 * File: NameError.java
 * @author: Mart van der Zalm
 * Date: 2024-01-06
 * Description:
 */
public class NameError implements Comparable<NameError>
{
	private int line;
	private int column;
	private String errorText;

	public NameError(int line, int column, String errorText)
	{
		this.line = line;
		this.column = column;
		this.errorText = errorText;
	}

	public int getLine()
	{
		return line;
	}

	public void setLine(int line)
	{
		this.line = line;
	}

	public int getColumn()
	{
		return column;
	}

	public void setColumn(int column)
	{
		this.column = column;
	}

	public String getErrorText()
	{
		return errorText;
	}

	public void setErrorText(String errorText)
	{
		this.errorText = errorText;
	}

	@Override
	public String toString()
	{
		return line + ":" + column + " error: " + errorText;
	}

	@Override
	public int compareTo(NameError o)
	{
		if (getLine() < o.getLine()) {
			return -1;
		} else if (getLine() > o.getLine()) {
			return 1;
		} else {
			if (getColumn() < o.getColumn()) {
				return -1;
			} else if (getColumn() > o.getColumn()) {
				return 1;
			} else {
				return 0;
			}
		}
	}
}
