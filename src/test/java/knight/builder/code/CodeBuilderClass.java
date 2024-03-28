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

package knight.builder.code;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/*
 * File: CodeBuilderClass.java
 * @author: Mart van der Zalm
 * Date: 2024-02-10
 * Description:
 */
public class CodeBuilderClass extends CodeBuilder
{
	private String id;
	private List<CodeBuilderFunction> functionsList;
	private List<CodeBuilderVariable> variablesList;

	public CodeBuilderClass()
	{
		this.functionsList = new ArrayList<>();
		this.variablesList = new ArrayList<>();
		this.mock();
	}

	public CodeBuilderClass setId(String id)
	{
		this.id = id;

		return this;
	}

	public CodeBuilderClass mockFunction(int count)
	{
		for (int i = 0; i < count; i++) {
			this.functionsList.add(new CodeBuilderFunction());
		}

		return this;
	}

	public CodeBuilderClass mockVariable(int count)
	{
		for (int i = 0; i < count; i++) {
			this.variablesList.add(new CodeBuilderVariable());
		}

		return this;
	}

	public CodeBuilderClass addFunction(CodeBuilderFunction function)
	{
		this.functionsList.add(function);

		return this;
	}

	public CodeBuilderClass addVariable(CodeBuilderVariable variable)
	{
		this.variablesList.add(variable);

		return this;
	}

	protected CodeBuilderClass mock()
	{
		this.id = super.random.identifier();
		
		return this;
	}

	public String toString()
	{		
		StringBuilder classBody = new StringBuilder();

		for (CodeBuilderVariable variable : variablesList) {
			classBody.append(variable).append(" ");
		}

		for (CodeBuilderFunction function : functionsList) {
			classBody.append(function).append(" ");
		}

		return String.format("class %s { %s }", id, classBody.toString());
	}
}