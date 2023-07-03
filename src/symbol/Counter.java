package src.symbol;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * A singleton class to provide a counter for generating unique identifiers.
 * The Counter class is used to maintain a count that can be used as a unique identifier for various purposes.
 */
public class Counter
{
	private static Counter instance; // The singleton instance of the Counter class
	private static AtomicInteger count = new AtomicInteger(0); // The atomic integer used to maintain the count

	private Counter(){} // Private constructor to prevent direct instantiation from outside the class

	/**
     * Get the singleton instance of the Counter class.
     *
     * @return The singleton instance of the Counter class.
     */
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

	/**
     * Get the current count and increment it atomically.
     * Each call to this method returns a unique count value.
     *
     * @return The current count value, which is then incremented by 1.
     */
	public int getCount()
	{
		return count.getAndIncrement();
	}
}