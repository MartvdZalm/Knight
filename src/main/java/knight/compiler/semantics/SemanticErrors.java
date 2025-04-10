package knight.compiler.semantics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * File: SemanticErrors.java
 * @author: Mart van der Zalm
 * Date: 2025-04-10
 */
public class SemanticErrors
{
	public static List<NameError> errorList = new ArrayList<>(); // List to store NameError objects representing
																	// semantic errors

	public static void addError(int line, int col, String errorText)
	{
		NameError error = new NameError(line, col, errorText);
		errorList.add(error);
	}

	public static void sort()
	{
		Collections.sort(errorList);
	}
}
