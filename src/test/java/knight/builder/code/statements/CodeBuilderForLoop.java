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

package knight.builder.code.statements;

import knight.builder.code.declarations.CodeBuilderVariableInit;
import knight.builder.code.expressions.CodeBuilderExpression;
import knight.builder.code.expressions.CodeBuilderIntLiteral;
import knight.builder.code.expressions.CodeBuilderIdentifierExpr;
import knight.builder.code.expressions.operations.CodeBuilderLessThan;
import knight.builder.code.types.CodeBuilderIntType;

/*
 * File: CodeBuilderForLoop.java
 * @author: Mart van der Zalm
 * Date: 2024-03-28
 * Description:
 */
public class CodeBuilderForLoop extends CodeBuilderStatement
{
	private CodeBuilderVariableInit initialization;
	private CodeBuilderExpression condition;
	private String update; // Needs to be fixed!!
	private CodeBuilderStatement body;

	public CodeBuilderForLoop()
	{
		this.mock();
	}

	public CodeBuilderForLoop setInitialization(CodeBuilderVariableInit initialization)
	{
		this.initialization = initialization;
	
		return this;
	}

	public CodeBuilderForLoop setCondition(CodeBuilderExpression condition)
	{
		this.condition = condition;

		return this;
	}

	public CodeBuilderForLoop setUpdate(String update)
	{
		this.update = update;

		return this;
	}

	public CodeBuilderStatement setBody(CodeBuilderStatement body)
	{
		this.body = body;

		return this;
	}

	protected CodeBuilderForLoop mock()
	{
		this.initialization = new CodeBuilderVariableInit().setType(new CodeBuilderIntType()).setId("i").setExpr(new CodeBuilderIntLiteral().setValue(0));
		this.condition = new CodeBuilderLessThan().setLhs(new CodeBuilderIdentifierExpr().setId("i")).setRhs(new CodeBuilderIntLiteral().setValue(10));
		this.update = "i = i + 1";
		this.body = super.random.statement();

		return this;
	}

	public String toString()
	{
		return String.format("for (%s %s; %s) { %s }", this.initialization, this.condition, this.update, this.body);
	}
}