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

package knight.compiler.symbol;

import java.util.Enumeration;
import java.util.Hashtable;

import knight.compiler.ast.types.ASTIdentifierType;
import knight.compiler.ast.types.ASTType;
import knight.compiler.semantics.Binding;

/*
 * File: SymbolClass.java
 * @author: Mart van der Zalm
 * Date: 2024-01-06
 * Description:
 */
public class SymbolClass extends Binding
{
	private String id;
	private Hashtable<String, SymbolFunction> functions;
	private Hashtable<String, SymbolVariable> variables;
	private String parent;

	public SymbolClass(String id, String p)
	{
		super(new ASTIdentifierType(null, id));
		this.id = id;
		parent = p;

		functions = new Hashtable<>();
		variables = new Hashtable<>();
	}

	public String getId()
	{
		return id;
	}

	public ASTType type()
	{
		return type;
	}

	public boolean addFunction(String id, ASTType type)
	{
		if (containsFunction(id)) {
			return false;
		} else {
			functions.put(id, new SymbolFunction(id, type));
			return true;
		}
	}

	public Enumeration<String> getFunctions()
	{
		return functions.keys();
	}

	public SymbolFunction getFunction(String id)
	{
		if (containsFunction(id)) {
			return functions.get(id);
		} else {
			return null;
		}
	}

	public boolean addVariable(String id, ASTType type)
	{
		if (containsVariable(id)) {
			return false;
		} else {
			variables.put(id, new SymbolVariable(id, type));
			return true;
		}
	}

	public SymbolVariable getVariable(String id)
	{
		if (containsVariable(id)) {
			return variables.get(id);
		} else {
			return null;
		}
	}

	public boolean containsVariable(String id)
	{
		return variables.containsKey(id);
	}

	public boolean containsFunction(String id)
	{
		return functions.containsKey(id);
	}

	public String parent()
	{
		return parent;
	}
}
