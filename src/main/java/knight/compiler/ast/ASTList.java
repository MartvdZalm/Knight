package knight.compiler.ast;

import java.util.List;

/*
 * File: AST.java
 * @author: Mart van der Zalm
 * Date: 2025-04-10
 */
public class ASTList<T>
{
	private List<T> list;

	public ASTList(List<T> list)
	{
		this.list = list;
	}

	public List<T> getList()
	{
		return list;
	}

	public int getSize()
	{
		return list.size();
	}

	public T getAt(int index)
	{
		if (index < getSize()) {
			return list.get(index);
		}
		return null;
	}

	public void setList(List<T> list)
	{
		this.list = list;
	}
}
