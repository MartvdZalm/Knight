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

package knight.compiler.ast.statements;

import java.util.List;

import knight.compiler.lexer.Token;
import knight.compiler.ast.ASTVisitor;

/*
 * File: ASTBlock.java
 * @author: Mart van der Zalm
 * Date: 2024-01-06
 * Description:
 */
public class ASTBlock extends ASTStatement
{
	private List<ASTStatement> body;

	public ASTBlock(Token token, List<ASTStatement> body)
	{
		super(token);
		this.body = body;
	}

	public List<ASTStatement> getStatementList()
	{
		return body;
	}

	public int getStatementListSize()
	{
		return body.size();
	}

	public ASTStatement getStatementAt(int index)
	{
		if (index < body.size()) {
			return body.get(index);
		}
		return null;
	}

	public void setStatementAt(int index, ASTStatement stat)
	{
		if (index < body.size()) {
			body.set(index, stat);
		}
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}
}