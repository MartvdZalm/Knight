package knight.compiler.semantics.model;

import knight.compiler.ast.types.ASTType;

public class SymbolVariable extends Binding
{
	private final String name;
	private int localVariableIndex = -1;

	public SymbolVariable(String name, ASTType type)
	{
		super(type);
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public int getLocalVariableIndex()
	{
		return localVariableIndex;
	}

	public void setLocalVariableIndex(int localVariableIndex)
	{
		this.localVariableIndex = localVariableIndex;
	}

	public boolean isLocalVariable()
	{
		return localVariableIndex >= 0;
	}

	@Override
	public String toString()
	{
		return String.format("Variable(%s : %s, index=%d)", name, type, localVariableIndex);
	}
}
