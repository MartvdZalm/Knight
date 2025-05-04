package knight.compiler.ast;

import java.util.List;
import java.util.StringJoiner;

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

	public String join(String separator)
	{
		StringJoiner joiner = new StringJoiner(separator);

		for (T item : list) {
			joiner.add(item.toString());
		}

		return joiner.toString();
	}
}
