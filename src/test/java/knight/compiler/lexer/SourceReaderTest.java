package knight.compiler.lexer;

import java.io.*;
import knight.compiler.lexer.SourceReader;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

public class SourceReaderTest
{
	@Test
	public void testMark()
	{
		String input = "This is a string for testing my SourceReader class!";
		BufferedReader bufferedReader = new BufferedReader(new StringReader(input));
		SourceReader sourceReader = new SourceReader(bufferedReader);

		assertDoesNotThrow(() -> {
			assertEquals('T', sourceReader.read());
			assertEquals('h', sourceReader.read());
			assertEquals('i', sourceReader.read());
			assertEquals('s', sourceReader.read());
			assertEquals(' ', sourceReader.read());
			sourceReader.mark(10);
			assertEquals('i', sourceReader.read());
			assertEquals('s', sourceReader.read());
			assertEquals(' ', sourceReader.read());
			assertEquals('a', sourceReader.read());
			assertEquals(' ', sourceReader.read());
			assertEquals('s', sourceReader.read());
			sourceReader.reset();
			assertEquals('i', sourceReader.read());
			assertEquals('s', sourceReader.read());
		});
	}

	@Test
	public void testMarkIllegalArgument()
	{
		BufferedReader mockReader = mock(BufferedReader.class);
		when(mockReader.markSupported()).thenReturn(true);

		SourceReader sourceReader = new SourceReader(mockReader);

		assertThrows(IllegalArgumentException.class, () -> {
			sourceReader.mark(-1);
			sourceReader.reset();
		});

		assertDoesNotThrow(() -> {
			sourceReader.mark(1);
			sourceReader.reset();
		});
	}

	@Test
	public void testMarkUnsupportedOperation()
	{
		BufferedReader mockReader = mock(BufferedReader.class);
		when(mockReader.markSupported()).thenReturn(false);

		SourceReader sourceReader = new SourceReader(mockReader);

		assertThrows(UnsupportedOperationException.class, () -> {
			sourceReader.mark(10);
		});
	}

	@Test
	public void testMarkIOException() throws IOException
	{
		BufferedReader mockReader = mock(BufferedReader.class);
		when(mockReader.markSupported()).thenReturn(true);
		doThrow(new IOException("Simulated IOException")).when(mockReader).mark(anyInt());

		SourceReader sourceReader = new SourceReader(mockReader);

		IOException exception = assertThrows(IOException.class, () -> {
			sourceReader.mark(10);
		});

		String expectedMessage = "Error occurred while marking input stream";
		String actualMessage = exception.getMessage();
		String assertErrorMessage = "Expected exception message to contain: '" + expectedMessage
				+ "', but actual message was: '" + actualMessage + "'";
		assert (actualMessage.contains(expectedMessage)) : assertErrorMessage;
	}

	@Test
	public void testReset()
	{
		String input = "This is a string for testing my SourceReader class!";
		BufferedReader bufferedReader = new BufferedReader(new StringReader(input));
		SourceReader sourceReader = new SourceReader(bufferedReader);

		assertDoesNotThrow(() -> {
			assertEquals('T', sourceReader.read());
			sourceReader.mark(10);
			assertEquals('h', sourceReader.read());
			assertEquals('i', sourceReader.read());
			assertEquals('s', sourceReader.read());
			sourceReader.reset();
			assertEquals('h', sourceReader.read());
			assertEquals('i', sourceReader.read());
			assertEquals('s', sourceReader.read());
			sourceReader.mark(10);
			assertEquals(' ', sourceReader.read());
			assertEquals('i', sourceReader.read());
			assertEquals('s', sourceReader.read());
			sourceReader.reset();
			assertEquals(' ', sourceReader.read());
			assertEquals('i', sourceReader.read());
			assertEquals('s', sourceReader.read());
		});
	}

	@Test
	public void testResetUnsupportedOperation()
	{
		BufferedReader mockReader = mock(BufferedReader.class);
		when(mockReader.markSupported()).thenReturn(false);

		SourceReader sourceReader = new SourceReader(mockReader);

		assertThrows(UnsupportedOperationException.class, () -> {
			sourceReader.reset();
		});
	}

	@Test
	public void testResetIOException() throws IOException
	{
		BufferedReader mockReader = mock(BufferedReader.class);
		when(mockReader.markSupported()).thenReturn(true);
		doThrow(new IOException("Simulated IOException")).when(mockReader).reset();

		SourceReader sourceReader = new SourceReader(mockReader);

		IOException exception = assertThrows(IOException.class, () -> {
			sourceReader.reset();
		});

		String expectedMessage = "Error occurred while resetting input stream";
		String actualMessage = exception.getMessage();
		String assertErrorMessage = "Expected exception message to contain: '" + expectedMessage
				+ "', but actual message was: '" + actualMessage + "'";
		assert (actualMessage.contains(expectedMessage)) : assertErrorMessage;
	}

	@Test
	public void testRead() throws IOException
	{
		String input = "This is a string for testing my SourceReader class!";
		BufferedReader bufferedReader = new BufferedReader(new StringReader(input));
		SourceReader sourceReader = new SourceReader(bufferedReader);
	}

	@Test
	public void testReadIOExceptionWhenLineIsNull() throws IOException
	{
		BufferedReader bufferedReader = new BufferedReader(new StringReader(""));
		SourceReader sourceReader = new SourceReader(bufferedReader);

		assertThrows(IOException.class, sourceReader::read);
	}

	@Test
	public void testReadReturnSpaceWhenLineIsEmpty() throws IOException
	{
		BufferedReader bufferedReader = new BufferedReader(new StringReader("q"));
		SourceReader sourceReader = new SourceReader(bufferedReader);
		sourceReader.props.line = "";

		assertEquals('q', sourceReader.read());
		assertEquals(' ', sourceReader.read());
	}

	@Test
	public void testReadReturnFirstCharacterWhenLineIsNotEmpty() throws IOException
	{
		BufferedReader bufferedReader = new BufferedReader(new StringReader("Hello"));
		SourceReader sourceReader = new SourceReader(bufferedReader);

		assertEquals('H', sourceReader.read());
	}

	@Test
	public void testReadReturnNextCharacterWhenIsPriorEndLineIsFalse() throws IOException
	{
		BufferedReader bufferedReader = new BufferedReader(new StringReader(""));
		SourceReader sourceReader = new SourceReader(bufferedReader);
		sourceReader.props.isPriorEndLine = false;
		sourceReader.props.col = 0;
		sourceReader.props.line = "Hello";

		assertEquals('e', sourceReader.read());
	}

	@Test
	public void testGetCol() throws IOException
	{
		String input = "This is a string for testing my SourceReader class!";
		BufferedReader bufferedReader = new BufferedReader(new StringReader(input));
		SourceReader sourceReader = new SourceReader(bufferedReader);

		sourceReader.read();
		sourceReader.read();
		sourceReader.read();
		sourceReader.read();
		sourceReader.read();
		sourceReader.read();

		assertEquals(5, sourceReader.getCol());
	}

	@Test
	public void testGetRow() throws IOException
	{
		BufferedReader bufferedReader = new BufferedReader(new StringReader(""));
		SourceReader sourceReader = new SourceReader(bufferedReader);
		sourceReader.props.row = 5;

		assertEquals(5, sourceReader.getRow());
	}

	@Test
	public void testClose() throws IOException
	{
		BufferedReader mockReader = mock(BufferedReader.class);
		SourceReader sourceReader = new SourceReader(mockReader);
		sourceReader.close();

		verify(mockReader, times(1)).close();
	}
}