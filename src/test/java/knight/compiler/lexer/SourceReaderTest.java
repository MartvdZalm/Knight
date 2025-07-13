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

	@Test
	public void mark_and_reset_should_restore_previous_position() throws IOException
	{
		String input = "Test string";
		BufferedReader bufferedReader = new BufferedReader(new StringReader(input));
		sourceReader = new SourceReader(bufferedReader);

		assertEquals('T', sourceReader.read());
		sourceReader.mark(10);
		assertEquals('e', sourceReader.read());
		assertEquals('s', sourceReader.read());
		sourceReader.reset();
		assertEquals('e', sourceReader.read());
	}

	@Test
	public void mark_with_negative_limit_should_throw_IllegalArgumentException()
	{
		BufferedReader bufferedReader = new BufferedReader(new StringReader("test"));
		sourceReader = new SourceReader(bufferedReader);
		assertThrows(IllegalArgumentException.class, () -> sourceReader.mark(-1));
	}

	@Test
	public void reset_without_mark_should_throw_IllegalStateException() throws IOException
	{
		when(mockReader.markSupported()).thenReturn(true);
		sourceReader = new SourceReader(mockReader);
		assertThrows(IllegalStateException.class, () -> sourceReader.reset());
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
	public void mark_at_end_of_line_should_work_correctly() throws IOException
	{
		BufferedReader bufferedReader = new BufferedReader(new StringReader("A\nB"));
		sourceReader = new SourceReader(bufferedReader);

		sourceReader.read();
		sourceReader.mark(10);
		sourceReader.read();
		sourceReader.reset();
		assertEquals('B', sourceReader.read());
	}

	@Test
	public void mark_when_unsupported_should_throw_UnsupportedOperationException()
	{
		when(mockReader.markSupported()).thenReturn(false);
		sourceReader = new SourceReader(mockReader);
		assertThrows(UnsupportedOperationException.class, () -> sourceReader.mark(10));
	}

	@Test
	public void reset_when_unsupported_should_throw_UnsupportedOperationException()
	{
		when(mockReader.markSupported()).thenReturn(false);
		sourceReader = new SourceReader(mockReader);
		assertThrows(UnsupportedOperationException.class, () -> sourceReader.reset());
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
}
