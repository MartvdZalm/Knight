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
import knight.compiler.ast.statements.ASTStatement;
import knight.compiler.ast.types.ASTType;

/*
 * File: ASTFunction.java
 * @author: Mart van der Zalm
 * Date: 2024-01-06
 * Description:
 */
public class ASTFunction extends AST
{
    private ASTType returnType;
    private ASTIdentifier id;
    private List<ASTArgument> argumentList;
    private List<ASTVariable> variableList;
	private List<ASTStatement> statementList;
	private List<ASTInlineASM> inlineASMList;

    public ASTFunction(Token token, ASTType returnType, ASTIdentifier id, List<ASTArgument> argumentList, List<ASTVariable> variableList, List<ASTStatement> statementList, List<ASTInlineASM> inlineASMList)
    {
        super(token);
        this.returnType = returnType;
        this.id = id;
        this.argumentList = argumentList;
        this.variableList = variableList;
        this.statementList = statementList;
        this.inlineASMList = inlineASMList;
    }

    public ASTType getReturnType()
    {
        return returnType;
    }

    public ASTIdentifier getId()
    {
    	return id;
    }

    public List<ASTArgument> getArgumentList()
    {
    	return argumentList;
    }

    public List<ASTVariable> getVariableList()
    {
    	return variableList;
    }

    public List<ASTStatement> getStatementList()
    {
    	return statementList;
    }

    public List<ASTInlineASM> getInlineASMList()
    {
    	return inlineASMList;
    }

	public int getArgumentListSize()
	{
		return argumentList.size();
	}

	public int getVariableListSize()
	{
		return variableList.size();
	}

	public int getStatementListSize()
	{
		return statementList.size();
	}

	public int getInlineASMListSize()
	{
		return inlineASMList.size();
	}

	public ASTArgument getArgumentDeclAt(int index)
	{
		if (index < getArgumentListSize()) {
			return argumentList.get(index);
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

	public ASTStatement getStatementDeclAt(int index)
	{
		if (index < getStatementListSize()) {
			return statementList.get(index);
		}
		return null;
	}

	public ASTInlineASM getInlineASMAt(int index)
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
