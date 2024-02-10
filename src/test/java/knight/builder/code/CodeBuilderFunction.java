package knight.builder.code;

import java.util.List;
import java.util.ArrayList;

import com.github.javafaker.Faker;

public class CodeBuilderFunction extends CodeBuilder
{
	private Faker faker;
	private String functionName;
	private List<CodeBuilderArgument> arguments;
	private List<CodeBuilderVariable> variables;
	private List<CodeBuilderStatement> statements;

	public CodeBuilderFunction()
	{
		this.initialize();
	}

	public CodeBuilderFunction(String functionName)
	{
		this.functionName = functionName;
		this.initialize();
	}

	private void initialize()
	{
		this.faker = new Faker();
		this.arguments = new ArrayList<>();
		this.variables = new ArrayList<>();
		this.statements = new ArrayList<>();
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
		if (empty(this.functionName)) {
			this.functionName = this.faker.lorem().word();
		}

		this.mockArgument(1);
		this.mockVariable(1);
		this.mockStatement(1);

		return this;
	}

	public CodeBuilderFunction mock(Map<String, Integer> data)
	{
		if (empty(this.functionName)) {
			this.functionName = this.faker.lorem().word();
		}

        if (data.containsKey("argument")) {
        	this.mockArgument(data.get("argument"));
        }

        if (data.containsKey("variable")) {
            this.mockVariable(data.get("variable"));
        }

        if (data.containsKey("statement")) {
            this.mockFunction(data.get("statement"));
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

		return String.format("fn %s(%s) { %s }", this.functionName, argumentBody.toString(), functionBody.toString());
	}
}