package src.semantics;

/**
 * Represents a NameError in the program's semantic analysis phase.
 * NameError is used to store information about an error related to a name (identifier) in the source code.
 * It provides the line number, column number, and error message for the specific error occurrence.
 */
public class NameError implements Comparable<NameError>
{
	private int line; // The line number where the NameError occurred
	private int column; // The column number where the NameError occurred
	private String errorText; // The error message describing the NameError

	/**
     * Constructor to create a new NameError with the specified line, column, and error message.
     *
     * @param line      The line number where the NameError occurred.
     * @param column    The column number where the NameError occurred.
     * @param errorText The error message describing the NameError.
     */
	public NameError(int line, int column, String errorText)
	{
		this.line = line;
		this.column = column;
		this.errorText = errorText;
	}

	/**
     * Get the line number where the NameError occurred.
     *
     * @return The line number of the NameError.
     */
	public int getLine()
	{
		return line;
	}

	/**
     * Set the line number for the NameError.
     *
     * @param line The line number to set for the NameError.
     */
	public void setLine(int line)
	{
		this.line = line;
	}

	/**
     * Get the column number where the NameError occurred.
     *
     * @return The column number of the NameError.
     */
	public int getColumn()
	{
		return column;
	}

	/**
     * Set the column number for the NameError.
     *
     * @param column The column number to set for the NameError.
     */
	public void setColumn(int column)
	{
		this.column = column;
	}

	/**
     * Get the error message describing the NameError.
     *
     * @return The error message of the NameError.
     */
	public String getErrorText()
	{
		return errorText;
	}

	/**
     * Set the error message for the NameError.
     *
     * @param errorText The error message to set for the NameError.
     */
	public void setErrorText(String errorText)
	{
		this.errorText = errorText;
	}

	/**
     * Returns a string representation of the NameError in the format: "line:column error: errorText"
     *
     * @return A string representation of the NameError.
     */
	@Override
	public String toString()
	{
		return line + ":" + column + " error: " + errorText;
	}

	/**
     * Compares this NameError with another NameError based on their line and column numbers.
     * This method is used to enable sorting of NameError objects.
     *
     * @param o The other NameError object to compare with.
     * @return -1 if this NameError is less than the other, 1 if greater, or 0 if they are equal.
     */
	@Override
	public int compareTo(NameError o)
	{
		if (getLine() < o.getLine()) {
			return -1;
		} else if (getLine() > o.getLine()) {
			return 1;
		} else {
			if (getColumn() < o.getColumn()) {
				return -1;
			} else if (getColumn() > o.getColumn()) {
				return 1;
			} else {
				return 0;
			}
		}
	}
}
