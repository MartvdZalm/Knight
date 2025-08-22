package knight.compiler.lexer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class SourceReaderTest
{
	private SourceReader sourceReader;
	private BufferedReader mockReader;

	@BeforeEach
	void setup()
	{
		mockReader = mock(BufferedReader.class);
	}

	@ParameterizedTest
	@CsvSource({ "'ABC\nDEF', 'A,B,C,D,E,F'", "'X\nY\nZ', 'X,Y,Z'" })
	public void read_should_return_correct_characters(String input, String expected) throws IOException
	{
		BufferedReader bufferedReader = new BufferedReader(new StringReader(input));
		sourceReader = new SourceReader(bufferedReader);
		String[] expectedChars = expected.split(",");

		for (String ch : expectedChars) {
			assertEquals(ch.charAt(0), sourceReader.read());
		}
	}

	@Test
	public void read_at_end_of_line_should_return_space() throws IOException
	{
		BufferedReader bufferedReader = new BufferedReader(new StringReader("A\nB"));
		sourceReader = new SourceReader(bufferedReader);
		assertEquals('A', sourceReader.read());
		assertEquals('B', sourceReader.read());
	}

	@Test
	public void getCol_should_return_correct_column_position() throws IOException
	{
		BufferedReader bufferedReader = new BufferedReader(new StringReader("12345"));
		sourceReader = new SourceReader(bufferedReader);
		assertEquals(0, sourceReader.getCol());
		sourceReader.read();
		assertEquals(1, sourceReader.getCol());
		sourceReader.read();
		assertEquals(2, sourceReader.getCol());
	}

	@Test
	public void getRow_should_increment_on_newline() throws IOException
	{
		BufferedReader bufferedReader = new BufferedReader(new StringReader("\n\n"));
		sourceReader = new SourceReader(bufferedReader);
		sourceReader.read();
		sourceReader.read();
		assertEquals(1, sourceReader.getRow());
	}

	@Test
	public void read_after_close_should_throw_IllegalStateException() throws IOException
	{
		BufferedReader bufferedReader = new BufferedReader(new StringReader("test"));
		sourceReader = new SourceReader(bufferedReader);
		sourceReader.close();
		assertThrows(IllegalStateException.class, () -> sourceReader.read());
	}

	@Test
	public void close_should_close_underlying_reader() throws IOException
	{
		sourceReader = new SourceReader(mockReader);
		sourceReader.close();
		verify(mockReader).close();
	}

	@Test
	public void read_empty_file_should_throw_IOException() throws IOException
	{
		BufferedReader bufferedReader = new BufferedReader(new StringReader(""));
		sourceReader = new SourceReader(bufferedReader);
		char c = sourceReader.read();
		assertEquals(SourceReader.EOF, c);
	}

	@Test
	public void peek_should_not_advance_reader() throws IOException
	{
		BufferedReader bufferedReader = new BufferedReader(new StringReader("ABC"));
		sourceReader = new SourceReader(bufferedReader);

		assertEquals('A', sourceReader.peek());
		assertEquals('A', sourceReader.read());
	}

	@Test
	public void snapshot_and_restore_should_restore_position() throws IOException
	{
		BufferedReader bufferedReader = new BufferedReader(new StringReader("ABC"));
		sourceReader = new SourceReader(bufferedReader);

		assertEquals('A', sourceReader.read());
		SourceReaderProperties snapshot = sourceReader.snapshot();

		assertEquals('B', sourceReader.read());
		sourceReader.restore(snapshot);

		assertEquals('B', sourceReader.read());
	}

	@Test
	public void peek_should_return_EOF_when_at_end() throws IOException
	{
		BufferedReader bufferedReader = new BufferedReader(new StringReader(""));
		sourceReader = new SourceReader(bufferedReader);

		assertEquals(SourceReader.EOF, sourceReader.peek());
	}

	@Test
	public void restore_with_null_should_throw()
	{
		BufferedReader br = new BufferedReader(new StringReader("ABC"));
		sourceReader = new SourceReader(br);

		assertThrows(IllegalArgumentException.class, () -> sourceReader.restore(null));
	}
}
