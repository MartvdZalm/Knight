package knight.compiler.semantics.diagnostics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import knight.compiler.lexer.Token;

/*
 * File: SemanticErrors.java
 * @author: Mart van der Zalm
 * Date: 2025-04-10
 */
public class SemanticErrors
{
	public static List<NameError> errorList = new ArrayList<>();

	public static void addError(int line, int col, String errorText)
	{
		NameError error = new NameError(line, col, errorText);
		errorList.add(error);
	}

	public static void addError(Token token, String errorText)
	{
		NameError error = new NameError(token.getRow(), token.getCol(), errorText);
		errorList.add(error);
	}

	public static void sort()
	{
		Collections.sort(errorList);
	}
}
