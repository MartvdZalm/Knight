package knight.helper;

import java.util.*;

public class Builder
{
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

    public String buildFunction(String functionName, String parameters, String returnType, String functionBody)
    {
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
}