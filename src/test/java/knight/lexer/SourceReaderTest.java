package knight.lexer;

import java.io.*;
import knight.lexer.SourceReader;

import static org.junit.Assert.*;
import org.junit.Test;

public class SourceReaderTest
{
	SourceReader sourceReader;

	@Test
	public void SourceTest()
	{   
		try {
			InputStream ioStream = this.getClass().getClassLoader().getResourceAsStream("SourceReaderTest.txt");
			Reader reader = new InputStreamReader(ioStream);
			BufferedReader br = new BufferedReader(reader);

			sourceReader = new SourceReader(br);
			char charachter = sourceReader.read();
			
			assertEquals(charachter, 'i'); 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}