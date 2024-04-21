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
 * File: CodeBuilderProgram.java
 * @author: Mart van der Zalm
 * Date: 2024-04-02
 * Description:
 */
public class CodeBuilderProgram extends CodeBuilder
{
	private List<CodeBuilderClass> classList;
	private List<CodeBuilderFunction> functionsList;
	private List<CodeBuilderVariable> variablesList;
	private List<CodeBuilderInlineASM> inlineASMList;

	public CodeBuilderProgram()
	{
		this.classList = new ArrayList<>();
		this.functionsList = new ArrayList<>();
		this.variablesList = new ArrayList<>();
		this.inlineASMList = new ArrayList<>();

		this.mock();
	}

	public CodeBuilderProgram addClasses(CodeBuilderClass... classes)
	{
		for (CodeBuilderClass classDecl : classes) {
			this.classList.add(classDecl);
		}

		return this;
	}

	public CodeBuilderProgram addFunctions(CodeBuilderFunction... functions)
	{
		for (CodeBuilderFunction function : functions) {
			this.functionsList.add(function);
		}

		return this;
	}

	public CodeBuilderProgram addVariables(CodeBuilderVariable... variables)
	{
		for (CodeBuilderVariable variable : variables) {
			this.variablesList.add(variable);
		}

		return this;
	}

	public CodeBuilderProgram addInlineASM(CodeBuilderInlineASM... inlineASMs)
	{
		for (CodeBuilderInlineASM inlineASM : inlineASMs) {
			this.inlineASMList.add(inlineASM);
		}

		return this;
	}

	protected CodeBuilderProgram mock()
	{
		return this;
	}

	public String toString()
	{
		StringBuilder programBody = new StringBuilder();

		for (CodeBuilderVariable variable : variablesList) {
			programBody.append(variable).append(" ");
		}

		for (CodeBuilderFunction function : functionsList) {
			programBody.append(function).append(" ");
		}

		for (CodeBuilderClass classDecl : classList) {
			programBody.append(classDecl).append(" ");
		}

		for (CodeBuilderInlineASM inlineASM : inlineASMList) {
			programBody.append(inlineASM).append(" ");
		}

		return programBody.toString();
	}
}