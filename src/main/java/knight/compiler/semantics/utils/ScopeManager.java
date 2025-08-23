package knight.compiler.semantics.utils;

import knight.compiler.semantics.model.Scope;
import knight.compiler.semantics.model.SymbolClass;
import knight.compiler.semantics.model.SymbolFunction;

public class ScopeManager
{
	private Scope currentScope;
	private SymbolClass currentClass;
	private SymbolFunction currentFunction;

	public void enterClass(SymbolClass symbolClass)
	{
		this.currentClass = symbolClass;
		this.currentFunction = null;
		this.currentScope = null;
	}

	public void exitClass()
	{
		this.currentClass = null;
	}

	public void enterFunction(SymbolFunction symbolFunction)
	{
		this.currentFunction = symbolFunction;
		this.currentScope = new Scope(currentScope);
	}

	public void exitFunction()
	{
		this.currentFunction = null;
		if (currentScope != null) {
			this.currentScope = currentScope.getParentScope();
		}
	}

	public void enterBlock()
	{
		this.currentScope = new Scope(currentScope);
	}

	public void exitBlock()
	{
		if (currentScope != null) {
			this.currentScope = currentScope.getParentScope();
		}
	}

	public Scope getCurrentScope()
	{
		return currentScope;
	}

	public void setCurrentScope(Scope scope)
	{
		this.currentScope = scope;
	}

	public SymbolClass getCurrentClass()
	{
		return currentClass;
	}

	public SymbolFunction getCurrentFunction()
	{
		return currentFunction;
	}

	public boolean isInClass()
	{
		return currentClass != null;
	}

	public boolean isInFunction()
	{
		return currentFunction != null;
	}
}
