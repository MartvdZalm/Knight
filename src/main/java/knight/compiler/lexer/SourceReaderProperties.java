package knight.compiler.lexer;

import java.io.BufferedReader;
import java.io.IOException;

class SourceReaderProperties
{
	public int row = 0;
	public int col = 0;
	public boolean isPriorEndLine = false;
	public String line;

	public SourceReaderProperties(BufferedReader bufferedReader)
	{
		try {
			this.line = bufferedReader.readLine();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public SourceReaderProperties(SourceReaderProperties other)
	{
		this.row = other.row;
		this.col = other.col;
		this.isPriorEndLine = other.isPriorEndLine;
		this.line = other.line;
	}

	@Override
	public String toString()
	{
		return "row: " + row + ", col: " + col + ", isPriorEndLine: " + isPriorEndLine + ", line: " + line;
	}
}
