package knight.lexer;

import java.io.*;
import static org.junit.Assert.*;
import org.junit.Test;

import knight.lexer.SourceReader;

public class SourceReaderTest
{
	@Test
	public void SourceTest()
	{   
		try {
			InputStream ioStream = this.getClass().getClassLoader().getResourceAsStream("SourceReaderTest.txt");
			Reader reader = new InputStreamReader(ioStream);
			BufferedReader br = new BufferedReader(reader);

			SourceReader sourceReader = new SourceReader(br);
			assertEquals('i', sourceReader.read()); 
			assertEquals(0, sourceReader.getCol());
			assertEquals(1, sourceReader.getRow());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}