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
import java.util.Map;

import knight.builder.code.CodeBuilder;
import knight.builder.code.expressions.CodeBuilderExpression;
import knight.builder.code.statements.CodeBuilderStatement;
import knight.builder.code.types.CodeBuilderType;

/*
 * File: CodeBuilderFunctionReturn.java
 * @author: Mart van der Zalm
 * Date: 2024-03-23
 * Description:
 */
public class CodeBuilderFunctionReturn extends CodeBuilderFunction
{
	private CodeBuilderType returnType;
	private CodeBuilderExpression returnStatement;

	public CodeBuilderFunctionReturn()
	{
		this.mock();
	}

	@Override
	public CodeBuilderFunctionReturn setId(String id)
	{
		super.setId(id);

		return this;
	}

	public CodeBuilderFunctionReturn setReturnType(CodeBuilderType returnType)
	{
		this.returnType = returnType;
		this.returnStatement = this.returnType.getExpr();

		return this;
	}

	public CodeBuilderFunctionReturn setReturnStatement(CodeBuilderExpression returnStatement)
	{
		this.returnStatement = returnStatement;

		return this;
	}

	public CodeBuilderFunctionReturn mock()
	{
		super.mock();

		this.returnType = super.random.type();
		this.returnStatement = this.returnType.getExpr();

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

		return String.format("fn %s(%s): %s { %s ret %s; }", super.id, argumentBody.toString(), this.returnType,
				functionBody.toString(), this.returnStatement);
	}
}