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

package knight.compiler.ast.declarations;

import java.util.List;

import knight.compiler.lexer.Token;
import knight.compiler.ast.AST;
import knight.compiler.ast.ASTVisitor;

/*
 * File: ASTClass.java
 * @author: Mart van der Zalm
 * Date: 2024-01-06
 * Description:
 */
public class ASTClass extends AST
{
	private ASTIdentifier id;
	private List<ASTFunction> functionList;
	private List<ASTProperty> propertyList;

	public ASTClass(Token token, ASTIdentifier id, List<ASTFunction> functionList, List<ASTProperty> propertyList)
	{
		super(token);
		this.id = id;
		this.functionList = functionList;
		this.propertyList = propertyList;
	}

	public ASTIdentifier getId()
	{
		return id;
	}

	public List<ASTFunction> getFunctionList()
	{
		return functionList;
	}

	public List<ASTProperty> getPropertyList()
	{
		return propertyList;
	}

	public int getFunctionListSize()
	{
		return functionList.size();
	}

	public int getPropertyListSize()
	{
		return propertyList.size();
	}

	public ASTFunction getFunctionDeclAt(int index)
	{
		if  (index < getFunctionListSize()) {
			return functionList.get(index);
		}
		return null;
	}

	public ASTProperty getPropertyDeclAt(int index)
	{
		if (index < getPropertyListSize()) {
			return propertyList.get(index);
		}
		return null;
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}
}