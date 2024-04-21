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
import knight.builder.code.statements.CodeBuilderStatement;

/*
 * File: CodeBuilderFunction.java
 * @author: Mart van der Zalm
 * Date: 2024-02-10
 * Description:
 */
public class CodeBuilderFunction extends CodeBuilder
{
	protected String id;
	protected List<CodeBuilderArgument> argumentsList;
	protected List<CodeBuilderVariable> variablesList;
	protected List<CodeBuilderStatement> statementsList;
	protected List<CodeBuilderInlineASM> inlineASMList;

	public CodeBuilderFunction()
	{
		this.argumentsList = new ArrayList<>();
		this.variablesList = new ArrayList<>();
		this.statementsList = new ArrayList<>();
		this.inlineASMList = new ArrayList<>();

		this.mock();
	}

	public CodeBuilderFunction setId(String id)
	{
		this.id = id;

		return this;
	}

	public CodeBuilderFunction addArguments(CodeBuilderArgument... arguments)
	{
		for (CodeBuilderArgument argument : arguments) {
			this.argumentsList.add(argument);
		}

		return this;
	}

	public CodeBuilderFunction addVariables(CodeBuilderVariable... variables)
	{
		for (CodeBuilderVariable variable : variables) {
			this.variablesList.add(variable);
		}

		return this;
	}

	public CodeBuilderFunction addStatements(CodeBuilderStatement... statements)
	{
		for (CodeBuilderStatement statement : statements) {
			this.statementsList.add(statement);
		}

		return this;
	}

	public CodeBuilderFunction addInlineASM(CodeBuilderInlineASM... inlineASMs)
	{
		for (CodeBuilderInlineASM inlineASM : inlineASMs) {
			this.inlineASMList.add(inlineASM);
		}

		return this;
	}

	protected CodeBuilderFunction mock()
	{
		this.id = super.random.identifier();
		
		return this;
	}

	public String toString()
	{
		StringBuilder functionBody = new StringBuilder();
		StringBuilder argumentBody = new StringBuilder();

		for (int i = 0; i < argumentsList.size(); i++) {
		    argumentBody.append(argumentsList.get(i));
		    if (i < argumentsList.size() - 1) {
		        argumentBody.append(", ");
		    }
		}

		for (CodeBuilderVariable variable : variablesList) {
			functionBody.append(variable).append(" ");
		}

		for (CodeBuilderStatement statement : statementsList) {
			functionBody.append(statement).append(" ");
		}

		for (CodeBuilderInlineASM inlineASM : inlineASMList) {
			functionBody.append(inlineASM).append(" ");
		}

		return String.format("fn %s(%s): void {\n %s \n}\n", this.id, argumentBody.toString(), functionBody.toString());
	}
}