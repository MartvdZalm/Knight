package knight.builder.code;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import com.github.javafaker.Faker;

public class CodeBuilderFunction extends CodeBuilder
{
	private String functionName;
	private CodeBuilderType returnType;
	private List<CodeBuilderArgument> arguments;
	private List<CodeBuilderVariable> variables;
	private List<CodeBuilderStatement> statements;
	private CodeBuilderReturnStatement returnStatement;

	public CodeBuilderFunction()
	{
		this.initialize();
	}

	public CodeBuilderFunction(String functionName)
	{
		this.functionName = functionName;
		this.initialize();
	}

	@Override
	protected void initialize()
	{
		super.initialize();
		this.arguments = new ArrayList<>();
		this.variables = new ArrayList<>();
		this.statements = new ArrayList<>();
	}

	public void setReturnType(CodeBuilderType returnType)
	{
		this.returnType = returnType;
	}

	public void setReturnStatement(CodeBuilderReturnStatement returnStatement)
	{
		this.returnStatement = returnStatement;
	}

	public void mockArgument(int count)
	{
		for (int i = 0; i < count; i++) {
			this.arguments.add(new CodeBuilderArgument().mock());
		}
	} 

	public void mockVariable(int count)
	{
		for (int i = 0; i < count; i++) {
			this.variables.add(new CodeBuilderVariable().mock());
		}
	}

	public void mockStatement(int count)
	{
		for (int i = 0; i < count; i++) {
			this.statements.add(new CodeBuilderStatement().mock());
		}
	}

	public void mockReturnStatement()
	{
		this.returnStatement = new CodeBuilderReturnStatement().mock();
	}

	public void addArgument(CodeBuilderArgument argument)
	{
		this.arguments.add(argument);
	}

	public void addVariable(CodeBuilderVariable variable)
	{
		this.variables.add(variable);	
	}

	public void addStatement(CodeBuilderStatement statement)
	{
		this.statements.add(statement);
	}

	public CodeBuilderFunction mock()
	{
		if (super.empty(this.functionName)) {
			this.functionName = super.faker.lorem().word();
		}
		return this;
	}

	public CodeBuilderFunction mock(Map<String, Integer> data)
	{
		if (super.empty(this.functionName)) {
			this.functionName = super.faker.lorem().word();
		}

        if (data.containsKey("argument")) {
        	this.mockArgument(data.get("argument"));
        }

        if (data.containsKey("variable")) {
            this.mockVariable(data.get("variable"));
        }

        if (data.containsKey("statement")) {
            this.mockStatement(data.get("statement"));
        }

		return this;
	}

	@Override
	public String toString()
	{
		StringBuilder functionBody = new StringBuilder();
		StringBuilder argumentBody = new StringBuilder();

		for (int i = 0; i < arguments.size(); i++) {
		    argumentBody.append(arguments.get(i));
		    if (i < arguments.size() - 1) {
		        argumentBody.append(", ");
		    }
		}

		for (CodeBuilderVariable variable : variables) {
			functionBody.append(variable).append(" ");
		}

		for (CodeBuilderStatement statement : statements) {
			functionBody.append(statement).append(" ");
		}

		if (this.returnType == null) {
			return String.format("fn %s(%s): void { %s }", this.functionName, argumentBody.toString(), functionBody.toString());
		} else {
			if (this.returnStatement == null) {
				this.mockReturnStatement();
			}

			return String.format("fn %s(%s): %s { %s %s }", this.functionName, argumentBody.toString(), this.returnType, functionBody.toString(), this.returnStatement);
		}
	}
}