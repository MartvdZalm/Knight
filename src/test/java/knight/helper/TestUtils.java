/*
 * MIT License
 * 
 * Copyright (c) 2023, Mart van der Zalm
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package knight.helper;

import static org.junit.jupiter.api.Assertions.*;
import knight.compiler.ast.AST;

/*
 * File: TestUtils.java
 * @author: Mart van der Zalm
 * Date: 2024-03-28
 * Description:
 */
public class TestUtils
{
	public <A, B> void expectsClass(Class<A> expects, Class<B> actual)
	{
		assertTrue(expects.equals(actual), "Expects " + expects + " Actual " + actual);
		// assertTrue("Expects " + expects + " Actual " + actual, expects.equals(actual));
	}

	public <A, B> void expectsClasses(Class<A>[] expects, Class<B>[] actual)
	{
		if (expects.length != actual.length) {
            throw new IllegalArgumentException("Length of expects and actual arrays must be equal");
        }

        for (int i = 0; i < expects.length; i++) {
            // assertTrue("Expects " + expects[i] + " Actual " + actual[i], expects[i].equals(actual[i]));
            assertTrue(expects[i].equals(actual[i]), "Expects " + expects[i] + " Actual " + actual[i]);
        }
	}

	// public <A, B> A castExpectClass(Class<A> expects, Class<B> actual)
	// {
	//     assertTrue("Expects " + expects + " Actual " + actual, expects.equals(actual));
	    
	//        try {
    //     return expects.cast(actual.newInstance());
    // } catch (Exception e) {
    //     e.printStackTrace(); // Handle this exception properly in your code
    //     return null;
    // }
	// }

	public <A, B> A castExpectClass(Class<A> expects, Object obj)
	{
		assertTrue(expects.isInstance(obj), "Expects " + expects + " Actual " + obj.getClass());
	    // assertTrue("Expects " + expects + " Actual " + obj.getClass(), expects.isInstance(obj));
	    return expects.cast(obj);
	}
}