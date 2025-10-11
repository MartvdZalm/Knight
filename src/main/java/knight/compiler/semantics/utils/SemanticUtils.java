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

			String className = ((ASTIdentifierType) varType).getName();
			SymbolClass symbolClass = symbolProgram.getClass(className);
			if (symbolClass == null) {
				DiagnosticReporter.error(instanceExpr, "Class '" + className + "' not found in symbol table.");
				return null;
			}

			return symbolClass.getFunction(functionName);
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
