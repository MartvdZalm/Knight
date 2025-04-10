package knight.compiler.symbol;

import java.util.concurrent.atomic.AtomicInteger;

/*
 * File: Counter.java
 * @author: Mart van der Zalm
 * Date: 2025-04-10
 * Description:
 */
public class Counter
{
	private static Counter instance;
	private static AtomicInteger count = new AtomicInteger(0);

	private Counter()
	{
	}

	public static Counter getInstance()
	{
		if (instance == null) {
			synchronized (Counter.class) {
				if (instance == null) {
					instance = new Counter();
					return instance;
				}
			}
		}
		return instance;
	}

	public int getCount()
	{
		return count.getAndIncrement();
	}
}