package knight.compiler.parser;

public class ParseException extends Exception
{
	private static final long serialVersionUID = 1L;

	public ParseException(int row, int col, String error)
	{
		super(row + ":" + col + " " + error);
	}
}
