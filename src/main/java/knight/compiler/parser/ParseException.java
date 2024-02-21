package knight.compiler.parser;

public class ParseException extends Exception
{
    public ParseException(int row, int col, String error)
    {
        super(row + ":" + col + " " + error);
    }
}
