package src.semantics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A utility class to manage and process semantic errors occurred during the analysis phase.
 * SemanticErrors class provides methods to add new errors and sort the list of errors based on their positions.
 */
public class SemanticErrors
{
	public static List<NameError> errorList = new ArrayList<>(); // List to store NameError objects representing semantic errors

	/**
     * Adds a new semantic error to the error list with the specified line, column, and error message.
     *
     * @param line      The line number where the semantic error occurred.
     * @param col       The column number where the semantic error occurred.
     * @param errorText The error message describing the semantic error.
     */
	public static void addError(int line, int col, String errorText)
	{
		NameError error = new NameError(line, col, errorText);
		errorList.add(error);
	}

	/**
     * Sorts the list of semantic errors based on their positions (line and column numbers).
     * The sorted order enables more organized error reporting and handling.
     */
	public static void sort()
	{
		Collections.sort(errorList);
	}
}
