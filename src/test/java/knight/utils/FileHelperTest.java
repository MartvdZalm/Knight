package knight.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.io.IOException;

public class FileHelperTest
{
	@Test
	public void isFileValid_should_return_true_for_valid_file() throws IOException
	{
		File tempFile = File.createTempFile("test", ".knight");
		tempFile.deleteOnExit();

		assertTrue(FileHelper.isFileValid(tempFile.getAbsolutePath()));
	}

	@Test
	public void isFileValid_should_return_false_for_nonexistent_file()
	{
		assertFalse(FileHelper.isFileValid("nonexistent.knight"));
	}

	@Test
	public void isFileValid_should_return_false_for_wrong_extension() throws IOException
	{
		File tempFile = File.createTempFile("test", ".txt");
		tempFile.deleteOnExit();

		assertFalse(FileHelper.isFileValid(tempFile.getAbsolutePath()));
	}

	@Test
	public void getFileExtension_should_return_correct_extension()
	{
		assertEquals("knight", FileHelper.getFileExtension(new File("file.knight")));
		assertEquals("txt", FileHelper.getFileExtension(new File("archive.tar.txt")));
		assertEquals("", FileHelper.getFileExtension(new File("noext")));
	}

	@Test
	public void removeFileExtension_should_remove_last_extension()
	{
		assertEquals("file", FileHelper.removeFileExtension("file.knight"));
		assertEquals("archive.tar", FileHelper.removeFileExtension("archive.tar.txt"));
		assertEquals("noext", FileHelper.removeFileExtension("noext"));
	}

	@Test
	public void getFileDirPath_should_return_correct_path()
	{
		File file = new File("/tmp/test.knight");
		String expected = "/tmp" + File.separator;
		assertEquals(expected, FileHelper.getFileDirPath(file.getAbsolutePath()));

		File rootFile = new File("test.knight");
		assertEquals("", FileHelper.getFileDirPath(rootFile.getPath()));
	}

	@Test
	public void write_should_create_file_with_content() throws IOException
	{
		String code = "int main() {}";
		File tempDir = new File(System.getProperty("java.io.tmpdir"));
		File file = FileHelper.write(code, tempDir.getAbsolutePath() + File.separator, "test.knight");

		assertNotNull(file);
		assertTrue(file.exists());

		String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
		assertTrue(content.contains(code));

		file.delete();
	}
}
