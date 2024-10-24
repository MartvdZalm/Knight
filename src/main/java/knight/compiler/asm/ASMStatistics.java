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

package knight.compiler.asm;

import knight.compiler.symbol.SymbolClass;
import knight.compiler.symbol.SymbolFunction;
import knight.compiler.symbol.SymbolVariable;
import knight.compiler.semantics.Binding;
import knight.compiler.asm.declarations.ASMClass;
import knight.compiler.asm.declarations.ASMFunction;

/*
 * File: ASMStatistics.java
 * @author: Mart van der Zalm
 * Date: 2024-10-24
 * Description:
 */
public class ASMStatistics
{
	public SymbolClass currentClass;
	public SymbolFunction currentFunction; 

	public int bytes;
	public int localArg;
	public int localVar;
	public int stack;
	public int localStack;

	public ASMStatistics()
	{
		this.currentClass = null;
		this.currentFunction = null;

		this.bytes = 0;
		this.localArg = 0;
		this.localVar = 0;
		this.stack = 0;
		this.localStack = 0;
	}

	public void setClass(ASMClass asmClass)
	{
		this.currentClass = (SymbolClass) asmClass.getId().getB();
	}

	public void setFunction(ASMFunction asmFunction)
	{
		this.localArg = 0;
		this.localVar = 0;
		this.localStack = 0;
		this.currentFunction = (SymbolFunction) asmFunction.getId().getB();;
	}

	public int getLocalArgIndex(Binding b)
	{
		if (b != null && b instanceof SymbolVariable) {
			return ((SymbolVariable) b).getLvIndex();
		}
		return -1;
	}

	public int setLocalArgIndex(Binding b)
	{
		if (b != null && b instanceof SymbolVariable) {
			((SymbolVariable) b).setLvIndex(++localArg);
			return localArg;
		}
		return -1;
	}

	public int getLocalVarIndex(Binding b)
	{
		if (b != null && b instanceof SymbolVariable) {
			return ((SymbolVariable) b).getLvIndex();
		}
		return -1;
	}

	public int setLocalVarIndex(Binding b)
	{
		if (b != null && b instanceof SymbolVariable) {
			((SymbolVariable) b).setLvIndex(++localVar);
			return localVar;
		}
		return -1;
	}
}