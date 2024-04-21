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
 * File: ASTProgram.java
 * @author: Mart van der Zalm
 * Date: 2024-01-06
 * Description: This class represents the whole file/program in the Abstract Syntax Tree (AST) of the compiler.
 */
public class ASTProgram extends AST
{
	private List<ASTClass> classList;
	private List<ASTFunction> functionList;
	private List<ASTVariable> variableList;
	private List<ASTInlineASM> inlineASMList;

	public ASTProgram(Token token, List<ASTClass> classList, List<ASTFunction> functionList, List<ASTVariable> variableList, List<ASTInlineASM> inlineASMList)
	{
		super(token);
		this.classList = classList;
		this.functionList = functionList;
		this.variableList = variableList;
		this.inlineASMList = inlineASMList;
	}

	public List<ASTClass> getClassList()
	{
		return classList;
	}

	public List<ASTFunction> getFunctionList()
	{
		return functionList;
	}

	public List<ASTVariable> getVariableList()
	{
		return variableList;
	}

	public List<ASTInlineASM> getInlineASMList()
	{
		return inlineASMList;
	}

	public int getClassListSize()
	{
		return classList.size();
	}

	public int getFunctionListSize()
	{
		return functionList.size();
	}

	public int getVariableListSize()
	{
		return variableList.size();
	}

	public int getInlineASMListSize()
	{
		return inlineASMList.size();
	}

	public ASTClass getClassDeclAt(int index)
	{
		if (index < getClassListSize()) {
			return classList.get(index);
		}
		return null;
	}

	public ASTFunction getFunctionDeclAt(int index)
	{
		if  (index < getFunctionListSize()) {
			return functionList.get(index);
		}
		return null;
	}

	public ASTVariable getVariableDeclAt(int index)
	{
		if (index < getVariableListSize()) {
			return variableList.get(index);
		}
		return null;
	}

	public ASTInlineASM getInlineASMDeclAt(int index)
	{
		if (index < getInlineASMListSize()) {
			return inlineASMList.get(index);
		}
		return null;
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}
}
