package knight.compiler.lexer;

import java.io.BufferedReader;
import java.io.IOException;

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
