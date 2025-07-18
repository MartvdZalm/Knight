package knight.compiler.semantics.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class CounterTest
{
	@Test
	public void testCounter_Singleton()
	{
		Counter c1 = Counter.getInstance();
		Counter c2 = Counter.getInstance();

		assertSame(c1, c2);
	}

	@Test
	public void testCounter_Increment()
	{
		Counter counter = Counter.getInstance();
		int first = counter.getCount();
		int second = counter.getCount();

		assertEquals(first + 1, second);
	}
}
