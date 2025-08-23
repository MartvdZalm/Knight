package knight.compiler.codegen;

public class CodeBuilder
{
	private final StringBuilder declarations = new StringBuilder();
	private final StringBuilder implementations = new StringBuilder();
	private int indentLevel = 0;
	private StringBuilder currentBuffer = implementations;
	private boolean newLine = true;

	public void startDeclarationSection()
	{
		currentBuffer = declarations;
	}

	public void endDeclarationSection()
	{
		currentBuffer = implementations;
		declarations.append("\n");
	}

	public void startImplementationSection()
	{
		currentBuffer = implementations;
	}

	public void endImplementationSection()
	{
		implementations.append("\n");
	}

	public void startBlock()
	{
		appendLine("{");
		indentLevel++;
	}

	public void endBlock()
	{
		indentLevel--;
		appendLine("}");
	}

	public void append(String code)
	{
		if (newLine) {
			currentBuffer.append("    ".repeat(indentLevel));
			newLine = false;
		}
		currentBuffer.append(code);
	}

	public void appendLine(String line)
	{
		if (line != null && !line.isEmpty()) {
			currentBuffer.append("    ".repeat(indentLevel)).append(line);
		}
		currentBuffer.append("\n");
		newLine = true;
	}

	public void appendToDeclarations(String code)
	{
		declarations.append("    ".repeat(indentLevel)).append(code).append("\n");
	}

	public void appendToImplementations(String code)
	{
		implementations.append("    ".repeat(indentLevel)).append(code).append("\n");
	}

	public void appendRawToDeclarations(String code)
	{
		declarations.append(code);
	}

	public void appendRawToImplementations(String code)
	{
		implementations.append(code);
	}

	public void increaseIndent()
	{
		indentLevel++;
	}

	public void decreaseIndent()
	{
		indentLevel = Math.max(0, indentLevel - 1);
	}

	public String buildCompleteCode(HeaderManager headerManager)
	{
		StringBuilder completeCode = new StringBuilder();

		completeCode.append(headerManager.generateHeaderIncludes());
		completeCode.append("\n");

		completeCode.append(declarations.toString());
		completeCode.append("\n");

		completeCode.append(implementations.toString());

		return completeCode.toString();
	}
}
