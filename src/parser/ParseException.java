package src.parser;

/**
 * The ParseException class represents an exception that occurs during parsing of the source code.
 * It is a subclass of the Exception class, allowing it to be used to handle parsing errors.
 */
public class ParseException extends Exception
{
    /**
     * Creates a new ParseException object with the specified row, column, and error message.
     *
     * @param row   The row (line number) where the parsing error occurred.
     * @param col   The column (character position) where the parsing error occurred.
     * @param error A descriptive error message providing more information about the parsing error.
     */
    public ParseException(int row, int col, String error)
    {
        super(row + ":" + col + " " + error);
    }
}
