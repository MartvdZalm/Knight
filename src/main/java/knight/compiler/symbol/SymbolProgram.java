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
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Hashtable;

import knight.compiler.ast.types.ASTBooleanType;
import knight.compiler.ast.types.ASTIdentifierType;
import knight.compiler.ast.types.ASTIntArrayType;
import knight.compiler.ast.types.ASTIntType;
import knight.compiler.ast.types.ASTStringType;
import knight.compiler.ast.types.ASTType;

/*
 * File: SymbolProgram.java
 * @author: Mart van der Zalm
 * Date: 2024-01-06
 * Description:
 */
public class SymbolProgram
{
	private Hashtable<String, SymbolClass> classes; 
	private Hashtable<String, SymbolFunction> functions;
	private Hashtable<String, SymbolVariable> variables;

	private Deque<String> rstack = new ArrayDeque<String>();

	public SymbolProgram()
	{
		classes = new Hashtable<>();
		functions = new Hashtable<>();
		variables = new Hashtable<>();
	}

	public boolean addClass(String id, String parent)
	{
		if (containsClass(id)) {
			return false;
		} else {
			classes.put(id, new SymbolClass(id, parent));
		}
		return true;
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

	public boolean addVariable(String id, ASTType type)
	{
		if (containsVariable(id)) {
			return false;
		} else {
			variables.put(id, new SymbolVariable(id, type));
			return true;
		}
	}

	public SymbolClass getClass(String id)
	{
		if (containsClass(id)) {
			return classes.get(id);
		} else {
			return null;
		}
	}

	public SymbolFunction getFunction(String id)
	{
		if (containsFunction(id)) {
			return functions.get(id);
		} else {
			return null;
		}
	}

	public SymbolFunction getFunction(String id, String classScope)
	{
		if (getClass(classScope) == null) {
			return null;
		}

		SymbolClass c = getClass(classScope);
		while (c != null && !rstack.contains(c.getId())) {
			rstack.push(c.getId());
			if (c.getFunction(id) != null) {
				rstack.clear();
				return c.getFunction(id);
			} else {
				if (c.parent() == null) {
					c = null;
				} else {
					c = getClass(c.parent());
				}
			}
		}
		rstack.clear();
		return null;
	}

	public Enumeration<String> getFunctions()
	{
		return functions.keys();
	}


	public ASTType getFunctionType(String id, String classScope)
	{
		SymbolFunction m = getFunction(id, classScope);
		if (m == null) {
			return null;
		} else {
			return m.getType();
		}
	}

	public SymbolVariable getVariable(String id)
	{
		return variables.get(id);
	}

	public SymbolVariable getVariable(String id, SymbolClass sClass, SymbolFunction sFunction)
	{
		if (sFunction != null) {
			if (sFunction.getVariable(id) != null) {
				return sFunction.getVariable(id);
			}
			if (sFunction.getParam(id) != null) {
				return sFunction.getParam(id);
			}
		} else if (sClass != null) {

			while (sClass != null && !rstack.contains(sClass.getId())) {
				rstack.push(sClass.getId());
				if (sClass.getVariable(id) != null) {
					rstack.clear();
					return sClass.getVariable(id);
				} else {
					if (sClass.parent() == null) {
						sClass = null;
					} else {
						sClass = getClass(sClass.parent());
					}
				}
			}
			rstack.clear();
			return null;
		}

		return getVariable(id);
	}

	public ASTType getVariableType(String id)
	{
		SymbolVariable var = getVariable(id);
		if (var != null) {
			return var.getType();
		}
		return null;
	}

	public boolean containsClass(String id)
	{
		if (id != null) {
			return classes.containsKey(id);
		}
		return false;
	}

	public boolean containsFunction(String id)
	{
		return functions.containsKey(id);
	}

	public boolean containsVariable(String id)
	{
		SymbolVariable var = getVariable(id);
		if (var != null) {
			return true;
		}
		return false;
	}

	public boolean compareTypes(ASTType t1, ASTType t2)
	{
		if (t1 == null || t2 == null) {
			return false;
		}

		if (t1 instanceof ASTIntType && t2 instanceof ASTIntType) {
			return true;
		}
		if (t1 instanceof ASTBooleanType && t2 instanceof ASTBooleanType) {
			return true;
		}
		if (t1 instanceof ASTIntArrayType && t2 instanceof ASTIntArrayType) {
			return true;
		}
		if (t1 instanceof ASTStringType && t2 instanceof ASTStringType) {
			return true;
		}
		if (t1 instanceof ASTIdentifierType && t2 instanceof ASTIdentifierType) {
			ASTIdentifierType i1 = (ASTIdentifierType) t1;
			ASTIdentifierType i2 = (ASTIdentifierType) t2;

			SymbolClass c = getClass(i2.getId());
			while (c != null && !rstack.contains(c.getId())) {
				rstack.push(c.getId());
				if (i1.getId().equals(c.getId())) {
					rstack.clear();
					return true;
				} else {
					if (c.parent() == null) {
						rstack.clear();
						return false;
					}
					c = getClass(c.parent());
				}
			}
			rstack.clear();
		}
		return false;
	}

	public boolean absCompTypes(ASTType t1, ASTType t2)
	{
		if (t1 == null || t2 == null) {
			return false;
		}

		if (t1 instanceof ASTIntType && t2 instanceof ASTIntType) {
			return true;
		}
		if (t1 instanceof ASTIntArrayType && t2 instanceof ASTIntArrayType) {
			return true;
		}
		if (t1 instanceof ASTIdentifierType && t2 instanceof ASTIdentifierType) {
			ASTIdentifierType i1 = (ASTIdentifierType) t1;
			ASTIdentifierType i2 = (ASTIdentifierType) t2;
			if (i1.getId().equals(i2.getId())) {
				return true;
			}
		}
		
		return false;
	}
}