package knight.builder.code;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import com.github.javafaker.Faker;

public class CodeBuilderClass extends CodeBuilder
{
	private Faker faker;
	private String className;
	private List<CodeBuilderFunction> functions;
	private List<CodeBuilderVariable> variables;

	public CodeBuilderClass()
	{
		this.initialize();
	}

	public CodeBuilderClass(String className)
	{
		this.className = className;
		this.initialize();
	}

	private void initialize()
	{
		this.faker = new Faker();
		this.functions = new ArrayList<>();
		this.variables = new ArrayList<>();
	}

	public void mockFunction(int count)
	{
		for (int i = 0; i < count; i++) {
			this.functions.add(new CodeBuilderFunction().mock());
		}
	}

	public void mockVariable(int count)
	{
		for (int i = 0; i < count; i++) {
			this.variables.add(new CodeBuilderVariable().mock());
		}
	}

	public void addFunction(CodeBuilderFunction function)
	{
		this.functions.add(function);
	}

	public void addVariable(CodeBuilderVariable variable)
	{
		this.variables.add(variable);
	}

	public CodeBuilderClass mock()
	{
		if (empty(this.className)) {
			this.className = this.faker.lorem().word();
		}

		this.mockFunction(1);
		this.mockVariable(1);

		return this;
	}

	public CodeBuilderClass mock(Map<String, Integer> data)
	{
		if (empty(this.className)) {
			this.className = this.faker.lorem().word();
		}

        if (data.containsKey("function")) {
            this.mockFunction(data.get("function"));
        }

        if (data.containsKey("variable")) {
            this.mockVariable(data.get("variable"));
        }

        return this;
	}

	@Override
	public String toString()
	{
		StringBuilder classBody = new StringBuilder();

		for (CodeBuilderVariable variable : variables) {
			classBody.append(variable).append(" ");
		}

		for (CodeBuilderFunction function : functions) {
			classBody.append(function).append(" ");
		}

		return String.format("class %s { %s }", className, classBody.toString());
	}
}