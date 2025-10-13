package knight.compiler.semantics.utils;

import knight.compiler.ast.expressions.ASTIdentifierExpr;
import knight.compiler.ast.types.ASTIdentifierType;
import knight.compiler.ast.types.ASTType;
import knight.compiler.semantics.model.*;
import knight.compiler.semantics.diagnostics.DiagnosticReporter;

public class SemanticUtils
{
	private SemanticUtils()
	{
	}

	public static SymbolVariable resolveVariable(String name, ScopeManager scopeManager, SymbolProgram symbolProgram)
	{
		if (scopeManager.getCurrentScope() != null) {
			SymbolVariable variable = scopeManager.getCurrentScope().getVariable(name);
			if (variable != null) {
				return variable;
			}
		}

		if (scopeManager.isInClass()) {
			SymbolProperty property = scopeManager.getCurrentClass().getProperty(name);
			if (property != null) {
				return new SymbolVariable(property.getName(), property.getType());
			}
		}

		return symbolProgram.getGlobalVariable(name);
	}

	public static SymbolFunction resolveFunction(String functionName, ASTIdentifierExpr instanceExpr,
			ScopeManager scopeManager, SymbolProgram symbolProgram)
	{
		if (instanceExpr != null) {
			String instanceName = instanceExpr.getName();
			SymbolVariable symbolVariable = resolveVariable(instanceName, scopeManager, symbolProgram);

			if (symbolVariable == null) {
				DiagnosticReporter.error(instanceExpr, "Variable '" + instanceName + "' not found in current scope.");
				return null;
			}

			ASTType varType = symbolVariable.getType();
			if (!(varType instanceof ASTIdentifierType)) {
				DiagnosticReporter.error(instanceExpr, "Variable '" + instanceName + "' is not a class instance.");
				return null;
			}

			String typeName = ((ASTIdentifierType) varType).getName();

			SymbolClass symbolClass = symbolProgram.getClass(typeName);
			if (symbolClass != null) {
				SymbolFunction func = symbolClass.getFunction(functionName);
				if (func != null) {
					return func;
				}
			}

			SymbolInterface symbolInterface = symbolProgram.getInterface(typeName);
			if (symbolInterface != null) {
				SymbolFunction func = symbolInterface.getFunction(functionName);
				if (func != null) {
					return func;
				}
			}

			DiagnosticReporter.error(instanceExpr,
					"Function '" + functionName + "' not found in type '" + typeName + "'.");
			return null;
		}

		if (scopeManager.isInClass()) {
			SymbolFunction function = scopeManager.getCurrentClass().getFunction(functionName);
			if (function != null) {
				return function;
			}
		}

		return symbolProgram.getGlobalFunction(functionName);
	}
}
