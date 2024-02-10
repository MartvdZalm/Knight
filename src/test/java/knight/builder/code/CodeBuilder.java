package knight.builder.code;

import java.util.*;
import knight.builder.Builder;

public class CodeBuilder extends Builder
{
    public String buildEnum()
    {

    }

    public String buildInterface()
    {

    }

	public String buildClass(String className, List<String> functions, List<String> variables)
    {
        StringBuilder classBody = new StringBuilder();

        for (String function : functions) {
            classBody.append(function).append(" ");
        }

        for (String variable : variables) {
            classBody.append(variable).append(" ");
        }

        return String.format("class %s { %s }", className, classBody.toString());
    }

    public String buildFunction(String functionName, CodeBuilderTypes returnType, List<String> arguments, 
    	List<String> variables, List<String> statements, string returnExpression)
    {
    	StringBuilder functionBody = new StringBuilder();

    	for (String variable : variables) {
    		functionBody.append(variable).append(" ");
    	}

    	for (String statement : statements) {
    		functionBody.append(statement).append(" ");
    	}

        return String.format("fn %s(%s): %s { ret %s; }", functionName, parameters, returnType, functionBody);
    }

    public String buildVariable(String type, String name)
    {
        return String.format("%s %s;", type, name);
    }

    public String buildVariableInit(String type, String name, String value)
    {
    	return String.format("%s %s = %s", type, name, value);
    }

    public String buildStatement(String identifier, String expression)
    {
        return String.format("%s = %s", identifier, expression);
    }

    public String buildArguments(String type, String identifier)
    {
    	return String.format("%s %s", type, identifier);
    }
}