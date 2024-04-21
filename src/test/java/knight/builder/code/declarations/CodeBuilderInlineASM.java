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

package knight.builder.code.declarations;

import java.util.List;
import java.util.ArrayList;

import knight.builder.code.CodeBuilder;

/*
 * File: CodeBuilderInlineASM.java
 * @author: Mart van der Zalm
 * Date: 2024-01-04
 * Description:
 */
public class CodeBuilderInlineASM extends CodeBuilder
{
	private List<String> lines;

	public CodeBuilderInlineASM()
	{
		this.lines = new ArrayList<>();

		this.mock();
	}

	public CodeBuilderInlineASM setLines(List<String> lines)
	{
		this.lines = lines;

		return this;
	}

	protected CodeBuilderInlineASM mock()
	{
		this.lines.add("call length");
		this.lines.add("movq $1, %rax");
		this.lines.add("movq %rdi, %rsi");
		this.lines.add("xor %rdi, %rdi");
		this.lines.add("movq %rcx, %rdx");
		this.lines.add("syscall");

		return this;
	}

	public String toString()
	{
		StringBuilder body = new StringBuilder();

		for (String line : lines) {
			body.append("\"" + line + "\"\n");
		}

		return String.format("asm {\n %s \n}", body);
	}
}