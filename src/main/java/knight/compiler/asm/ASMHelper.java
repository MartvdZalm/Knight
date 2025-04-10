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
 * File: ASMHelper.java
 * @author: Mart van der Zalm
 * Date: 2024-12-20
 * Description:
 */
public class ASMHelper
{
	public String getArgumentRegister(int index)
	{
		switch (index)
		{
		case 0:
			return "rdi";
		case 1:
			return "rsi";
		case 2:
			return "rdx";
		case 3:
			return "rcx";
		case 4:
			return "r8";
		case 5:
			return "r9";
		default:
			throw new IllegalArgumentException("Unsupported argument index: " + index);
		}
	}

	public String getLocalVariableReference(int index)
	{
		return ((index + 1) * 8) + "(%rbp)";
	}
}