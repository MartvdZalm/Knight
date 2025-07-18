package knight.compiler.lexer;

import java.io.BufferedReader;
import java.io.IOException;

public class SourceReader implements AutoCloseable
{
	public static final char EOF = (char) -1;

	private final BufferedReader source;
	private SourceReaderProperties props;
	private SourceReaderProperties savedProps;
	private boolean closed = false;

	public SourceReader(BufferedReader bufferedReader)
	{
		if (bufferedReader == null) {
			throw new IllegalArgumentException("bufferedReader cannot be null");
		}

		this.source = bufferedReader;
		this.props = new SourceReaderProperties(source);
	}

	public void mark(int readAheadLimit) throws IOException
	{
		checkNotClosed();
		if (readAheadLimit < 0) {
			throw new IllegalArgumentException("Read ahead limit must not be negative");
		}

		if (!source.markSupported()) {
			throw new UnsupportedOperationException("Mark operation is not supported by the underlying reader");
		}

		source.mark(readAheadLimit);
		this.savedProps = new SourceReaderProperties(props);
	}

	public void reset() throws IOException
	{
		checkNotClosed();
		if (!source.markSupported()) {
			throw new UnsupportedOperationException("Reset operation is not supported by the underlying reader");
		}

		if (savedProps == null) {
			throw new IllegalStateException("No mark position set");
		}

		source.reset();
		this.props = new SourceReaderProperties(this.savedProps);
	}

	public char read() throws IOException
	{
		checkNotClosed();

		if (props.line == null) {
			return EOF;
		}

		if (props.isPriorEndLine) {
			props.row++;
			props.col = 0;
			props.line = source.readLine();
			props.isPriorEndLine = false;

			if (props.line == null) {
				return EOF;
			}
		}

		if (props.line.isEmpty()) {
			props.isPriorEndLine = true;
			return ' ';
		}

		char c = props.line.charAt(props.col);

		props.col++;
		if (props.col >= props.line.length()) {
			props.isPriorEndLine = true;
		}

		return c;
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
		if (closed) {
			return;
		}

		Symbol.symbols.clear();

		try {
			source.close();
		} catch (IOException ignored) {
		} finally {
			closed = true;
		}
	}

	private void checkNotClosed()
	{
		if (closed) {
			throw new IllegalStateException("SourceReader has been closed");
		}
	}
}
